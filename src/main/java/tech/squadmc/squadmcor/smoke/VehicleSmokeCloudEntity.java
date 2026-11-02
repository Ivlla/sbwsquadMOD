package tech.squadmc.squadmcor.smoke;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

/**
 * Перенесено из aasgranate (AasSmokeSourceEntity) — логика 1:1,
 * зависимость от исходного мода отсутствует.
 *
 * Универсальный "источник" объёмного дыма для техники — то же самое облако,
 * что рисуется у дымовых шашек M18/RDG-2, но без гранаты/анимации.
 *
 * Два режима использования (оба — просто эта же сущность):
 *
 * 1. "Разовое" облако (залп дымовых гранатомётов):
 *    VehicleSmokeSystem.fireVolley(...) создаёт облака с фиксированным
 *    holdTicks — растёт → держится holdTicks → гаснет, точь-в-точь как M18.
 *
 * 2. "Постоянный" дым дымогенератора: создать один раз, затем
 *    keepAlive() на каждом тике, пока генератор включён. keepAlive
 *    отодвигает "конец удержания" на KEEPALIVE_GRACE_TICKS вперёд; как
 *    только вызовы прекращаются (генератор выключен/техника уничтожена),
 *    запас истекает и начинается обычное затухание — вызов "stop" не нужен.
 *
 * Может следовать за техникой: attachToCarrier(vehicle, localOffset) —
 * центр облака каждый тик пересчитывается из позиции/поворота носителя.
 * Если носитель исчез, дым доигрывает удержание/затухание на месте.
 */
public class VehicleSmokeCloudEntity extends Entity implements VehicleSmokeEmitter {

    /** Сколько тиков "про запас" даёт один вызов keepAlive() поверх текущего возраста облака. */
    public static final int KEEPALIVE_GRACE_TICKS = 40; // 2 сек

    private static final EntityDataAccessor<Integer> DATA_SMOKE_TICKS =
            SynchedEntityData.defineId(VehicleSmokeCloudEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_RADIUS =
            SynchedEntityData.defineId(VehicleSmokeCloudEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEIGHT =
            SynchedEntityData.defineId(VehicleSmokeCloudEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_GROW_TICKS =
            SynchedEntityData.defineId(VehicleSmokeCloudEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HOLD_TICKS =
            SynchedEntityData.defineId(VehicleSmokeCloudEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FADE_TICKS =
            SynchedEntityData.defineId(VehicleSmokeCloudEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CARRIER_ID =
            SynchedEntityData.defineId(VehicleSmokeCloudEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_INTENSITY =
            SynchedEntityData.defineId(VehicleSmokeCloudEntity.class, EntityDataSerializers.FLOAT);
    private static final int SYNC_INTERVAL = 10;

    // --- серверные (не синхронизируемые напрямую) поля ---
    private int smokeTicks = 0;
    private Vec3 carrierLocalOffset = Vec3.ZERO;

    // --- клиентские поля (сглаженное локальное продолжение тиков между синками) ---
    private int lastSyncedSmokeTicks = -1;

    public VehicleSmokeCloudEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_SMOKE_TICKS, 0);
        this.entityData.define(DATA_RADIUS, 6.5F);
        this.entityData.define(DATA_HEIGHT, 3.0F);
        this.entityData.define(DATA_GROW_TICKS, 600);
        this.entityData.define(DATA_HOLD_TICKS, 1200);
        this.entityData.define(DATA_FADE_TICKS, 200);
        this.entityData.define(DATA_CARRIER_ID, -1);
        this.entityData.define(DATA_INTENSITY, 1.0F);
    }

    /** Вызывается сервером сразу после addFreshEntity(), см. VehicleSmokeSystem. */
    public void configure(float radius, float height, int growTicks, int holdTicks, int fadeTicks) {
        this.entityData.set(DATA_RADIUS, radius);
        this.entityData.set(DATA_HEIGHT, height);
        this.entityData.set(DATA_GROW_TICKS, growTicks);
        this.entityData.set(DATA_HOLD_TICKS, holdTicks);
        this.entityData.set(DATA_FADE_TICKS, fadeTicks);
    }

    /** Привязать облако к движущемуся носителю (технике). offset — локальные координаты. */
    public void attachToCarrier(Entity carrier, Vec3 localOffset) {
        this.entityData.set(DATA_CARRIER_ID, carrier.getId());
        this.carrierLocalOffset = localOffset;
    }

    /** Продлить удержание облака ещё немного вперёд. Вызывать каждый тик, пока генератор активен. */
    public void keepAlive() {
        if (this.level().isClientSide) return;
        int growTicks = this.entityData.get(DATA_GROW_TICKS);
        int desiredHold = Math.max(
                this.entityData.get(DATA_HOLD_TICKS),
                (this.smokeTicks - growTicks) + KEEPALIVE_GRACE_TICKS);
        this.entityData.set(DATA_HOLD_TICKS, desiredHold);
    }

    // ------------------------------------------------------------------
    // VehicleSmokeEmitter
    // ------------------------------------------------------------------

    @Override
    public boolean isSmoking() {
        return !this.isRemoved();
    }

    @Override
    public float getSmokeTicks(float partialTick) {
        return this.smokeTicks + partialTick;
    }

    @Override
    public float getSmokeDensity(float partialTick) {
        float t = getSmokeTicks(partialTick);
        int growTicks = this.entityData.get(DATA_GROW_TICKS);
        int holdTicks = this.entityData.get(DATA_HOLD_TICKS);
        int fadeTicks = Math.max(1, this.entityData.get(DATA_FADE_TICKS));
        if (t <= 0.0F) return 0.0F;
        if (t < growTicks) return t / (float) growTicks;
        if (t < growTicks + holdTicks) return 1.0F;
        float f = (t - growTicks - holdTicks) / (float) fadeTicks;
        return f >= 1.0F ? 0.0F : 1.0F - f;
    }

    @Override
    public float getSmokeGrowth(float partialTick) {
        float t = getSmokeTicks(partialTick);
        int growTicks = this.entityData.get(DATA_GROW_TICKS);
        if (growTicks <= 0) return 1.0F;
        if (t <= 0.0F) return 0.0F;
        return Math.min(1.0F, t / (float) growTicks);
    }

    @Override
    public float getSmokeRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    @Override
    public float getSmokeHeight() {
        return this.entityData.get(DATA_HEIGHT);
    }

    // ------------------------------------------------------------------

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            tickClient();
            return;
        }
        tickServer();
    }

    private void tickServer() {
        int carrierId = this.entityData.get(DATA_CARRIER_ID);
        if (carrierId >= 0) {
            Entity carrier = this.level().getEntity(carrierId);
            if (carrier != null && carrier.isAlive()) {
                float yawRad = -carrier.getYRot() * ((float) Math.PI / 180F);
                double ox = carrierLocalOffset.x * Math.cos(yawRad) - carrierLocalOffset.z * Math.sin(yawRad);
                double oz = carrierLocalOffset.x * Math.sin(yawRad) + carrierLocalOffset.z * Math.cos(yawRad);
                this.setPos(carrier.getX() + ox, carrier.getY() + carrierLocalOffset.y, carrier.getZ() + oz);
            } else {
                // Носитель пропал — перестаём гоняться за ним, дым доигрывает на месте.
                this.entityData.set(DATA_CARRIER_ID, -1);
            }
        }

        this.smokeTicks++;
        if (this.smokeTicks % SYNC_INTERVAL == 0) {
            this.entityData.set(DATA_SMOKE_TICKS, this.smokeTicks);
        }

        int total = this.entityData.get(DATA_GROW_TICKS)
                + this.entityData.get(DATA_HOLD_TICKS)
                + this.entityData.get(DATA_FADE_TICKS);
        if (this.smokeTicks >= total) {
            this.discard();
        }
    }

    private void tickClient() {
        int synced = this.entityData.get(DATA_SMOKE_TICKS);
        if (synced != this.lastSyncedSmokeTicks) {
            this.lastSyncedSmokeTicks = synced;
            this.smokeTicks = synced;
        } else {
            this.smokeTicks++;
        }
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        float radius = getSmokeRadius();
        float height = getSmokeHeight();
        return this.getBoundingBox().inflate(radius + 4.0, height + 2.0, radius + 4.0);
    }

    // Маркер: не толкается, не подбирает урон, не коллизирует.
    @Override public boolean isPushable() { return false; }
    @Override public boolean isPickable() { return false; }
    @Override protected boolean canAddPassenger(Entity passenger) { return false; }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("SmokeTicks", this.smokeTicks);
        tag.putFloat("Radius", this.entityData.get(DATA_RADIUS));
        tag.putFloat("Height", this.entityData.get(DATA_HEIGHT));
        tag.putInt("GrowTicks", this.entityData.get(DATA_GROW_TICKS));
        tag.putInt("HoldTicks", this.entityData.get(DATA_HOLD_TICKS));
        tag.putInt("FadeTicks", this.entityData.get(DATA_FADE_TICKS));
        tag.putFloat("Intensity", this.entityData.get(DATA_INTENSITY));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("SmokeTicks")) this.smokeTicks = tag.getInt("SmokeTicks");
        if (tag.contains("Radius")) this.entityData.set(DATA_RADIUS, tag.getFloat("Radius"));
        if (tag.contains("Height")) this.entityData.set(DATA_HEIGHT, tag.getFloat("Height"));
        if (tag.contains("GrowTicks")) this.entityData.set(DATA_GROW_TICKS, tag.getInt("GrowTicks"));
        if (tag.contains("HoldTicks")) this.entityData.set(DATA_HOLD_TICKS, tag.getInt("HoldTicks"));
        if (tag.contains("FadeTicks")) this.entityData.set(DATA_FADE_TICKS, tag.getInt("FadeTicks"));
        if (tag.contains("Intensity")) this.entityData.set(DATA_INTENSITY, tag.getFloat("Intensity"));
        this.entityData.set(DATA_SMOKE_TICKS, this.smokeTicks);
    }
    public void setIntensity(float v) { this.entityData.set(DATA_INTENSITY, Mth.clamp(v, 0.05F, 1.0F)); }

    @Override
    public float getSmokeIntensity() { return this.entityData.get(DATA_INTENSITY); }
}
