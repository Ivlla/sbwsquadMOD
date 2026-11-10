package tech.squadmc.squadmcor.entity;

import com.atsuishio.superbwarfare.data.vehicle.subdata.OBBInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkHooks;
import tech.squadmc.squadmcor.client.wreck.VehicleWreckClient;
import tech.squadmc.squadmcor.fx.MtsFxServer;
import tech.squadmc.squadmcor.init.ModEntities;

import java.util.ArrayList;
import java.util.List;

/**
 * The burnt-out hull left by a destroyed vehicle. It is a separate entity so the vehicle itself is
 * removed at the moment of its death, which is what AAS reads as the vehicle being lost.
 */
public class VehicleWreckEntity extends Entity {

    public static final int FIRE_TICKS = 20 * 60 * 2;
    public static final int SMOKE_TICKS = 20 * 60 * 5;
    public static final int LIFETIME_TICKS = 20 * 60 * 15;

    private static final double GRAVITY = 0.04;
    private static final float HEAVY_VEHICLE_HEALTH = 400F;
    private static final float MAX_PART_WIDTH = 2F;
    private static final float MIN_PART_WIDTH = 0.5F;
    /** Spacing as a share of part width, so a rotated hull leaves no gaps between parts. */
    private static final float PART_OVERLAP = 0.75F;
    private static final int MAX_PARTS = 64;
    private static final int PART_SPAWN_TIMEOUT = 100;

    private static final EntityDataAccessor<String> SOURCE =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> TURRET_YAW =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BARREL_PITCH =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> TURRET_LOST =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_AMMUNITION =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> HALF_X =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HALF_Z =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.FLOAT);
    // A clock origin stays constant while ticking: late viewers still receive the exact age,
    // without a dirty metadata packet every tick. Rebased after chunk unload/reload.
    private static final EntityDataAccessor<Long> AGE_ORIGIN =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.LONG);
    private int serverAge;

    /** Each is {centerX, centerZ, halfX, halfZ, top} relative to the wreck, in vehicle axes. */
    private final List<float[]> hullBoxes = new ArrayList<>();
    private boolean partsSpawned = false;

    public VehicleWreckEntity(EntityType<? extends VehicleWreckEntity> type, Level level) {
        super(type, level);
    }

    /** Replaces a destroyed vehicle with its wreck. */
    public static void replace(ServerLevel level, VehicleEntity vehicle) {
        VehicleWreckEntity wreck = new VehicleWreckEntity(ModEntities.VEHICLE_WRECK.get(), level);
        wreck.moveTo(vehicle.getX(), vehicle.getY(), vehicle.getZ(), vehicle.getYRot(), 0F);
        wreck.entityData.set(SOURCE, EntityType.getKey(vehicle.getType()).toString());
        wreck.entityData.set(TURRET_YAW, vehicle.getTurretYRot());
        wreck.entityData.set(BARREL_PITCH, vehicle.getTurretXRot());
        wreck.entityData.set(TURRET_LOST, vehicle.getSympatheticDetonated());
        wreck.entityData.set(HAS_AMMUNITION, hasAmmunition(vehicle));
        wreck.takeShapeOf(vehicle);
        MtsFxServer.vehicleDeath(level, vehicle.position().add(0, 1, 0), vehicle.getMaxHealth() >= HEAVY_VEHICLE_HEALTH);
        vehicle.ejectPassengers();
        vehicle.generateWreckageLoot();
        vehicle.discard();
        level.addFreshEntity(wreck);
    }

    /**
     * Only armed vehicles may play ammunition cook-off effects after destruction.
     * Empty civilian trucks have no weapon entries; unarmed vehicles may use
     * placeholder entries (Nothing/Empty) or smoke-only launchers.
     */
    private static boolean hasAmmunition(VehicleEntity vehicle) {
        for (String weapon : vehicle.getGunDataMap().keySet()) {
            if (weapon == null || weapon.isBlank()) continue;
            String key = weapon.toLowerCase(java.util.Locale.ROOT);
            if (key.equals("nothing") || key.equals("empty") || key.equals("none")
                    || key.equals("smokelauncher") || key.equals("smokegrenades")
                    || key.equals("smoke")) continue;
            return true;
        }
        return false;
    }

    /** Records the hull: the vehicle's hitboxes without its wheels, which the wreck no longer has. */
    private void takeShapeOf(VehicleEntity vehicle) {
        double halfX = vehicle.getBbWidth() / 2, halfZ = halfX, height = vehicle.getBbHeight();
        for (OBB obb : vehicle.getOBBs()) {
            AABB box = OBB.getWorldAABB(obb);
            halfX = Math.max(halfX, Math.max(box.maxX - vehicle.getX(), vehicle.getX() - box.minX));
            halfZ = Math.max(halfZ, Math.max(box.maxZ - vehicle.getZ(), vehicle.getZ() - box.minZ));
            height = Math.max(height, box.maxY - vehicle.getY());
        }
        this.entityData.set(HALF_X, (float) halfX);
        this.entityData.set(HALF_Z, (float) halfZ);
        this.entityData.set(HEIGHT, (float) height);

        for (OBBInfo info : vehicle.computed().getObb()) {
            if (info.getPart() == OBB.Part.WHEEL_LEFT || info.getPart() == OBB.Part.WHEEL_RIGHT) continue;
            Vec3 center = info.getPosition(), size = info.getSize();
            this.hullBoxes.add(new float[]{(float) center.x, (float) center.z, (float) size.x, (float) size.z, (float) (center.y + size.y)});
        }
        if (this.hullBoxes.isEmpty()) {
            float half = vehicle.getBbWidth() / 2;
            this.hullBoxes.add(new float[]{0F, 0F, half, half, vehicle.getBbHeight()});
        }
    }

    /** Whole hull, for drawing and for placing effects; the wreck's own box is deliberately tiny. */
    public AABB hullBox() {
        float halfX = this.entityData.get(HALF_X), halfZ = this.entityData.get(HALF_Z);
        return new AABB(getX() - halfX, getY(), getZ() - halfZ, getX() + halfX, getY() + this.entityData.get(HEIGHT), getZ() + halfZ);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return hullBox();
    }

    private void spawnHullParts() {
        this.partsSpawned = true;
        double yaw = Math.toRadians(getYRot());
        double cos = Math.cos(yaw), sin = Math.sin(yaw);
        int spawned = 0;
        for (float[] box : this.hullBoxes) {
            float sizeX = box[2] * 2, sizeZ = box[3] * 2;
            float width = Math.min(MAX_PART_WIDTH, Math.max(MIN_PART_WIDTH, Math.min(sizeX, sizeZ)));
            float step = width * PART_OVERLAP;
            int countX = Math.max(1, (int) Math.ceil((sizeX - width) / step) + 1);
            int countZ = Math.max(1, (int) Math.ceil((sizeZ - width) / step) + 1);
            for (int ix = 0; ix < countX && spawned < MAX_PARTS; ix++) {
                for (int iz = 0; iz < countZ && spawned < MAX_PARTS; iz++) {
                    double localX = box[0] + (countX == 1 ? 0 : -box[2] + width / 2 + ix * (sizeX - width) / (countX - 1));
                    double localZ = box[1] + (countZ == 1 ? 0 : -box[3] + width / 2 + iz * (sizeZ - width) / (countZ - 1));
                    VehicleWreckPartEntity part = new VehicleWreckPartEntity(ModEntities.VEHICLE_WRECK_PART.get(), level());
                    part.setup(this, width, Math.max(0.5F, box[4]));
                    part.setPos(getX() + localX * cos - localZ * sin, getY(), getZ() + localX * sin + localZ * cos);
                    level().addFreshEntity(part);
                    spawned++;
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SOURCE, "");
        this.entityData.define(TURRET_YAW, 0F);
        this.entityData.define(BARREL_PITCH, 0F);
        this.entityData.define(TURRET_LOST, false);
        this.entityData.define(HAS_AMMUNITION, false);
        this.entityData.define(HALF_X, 1F);
        this.entityData.define(HALF_Z, 1F);
        this.entityData.define(HEIGHT, 2F);
        this.entityData.define(AGE_ORIGIN, Long.MIN_VALUE);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(1F, 1F);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> VehicleWreckClient.tick(this));
            return;
        }
        int age = ++this.serverAge;
        if (age >= LIFETIME_TICKS) {
            discard();
            return;
        }
        this.entityData.set(AGE_ORIGIN, level().getGameTime() - age);
        if (!onGround()) {
            setDeltaMovement(getDeltaMovement().add(0, -GRAVITY, 0));
            move(MoverType.SELF, getDeltaMovement());
        }
        if (!this.partsSpawned && (onGround() || age >= PART_SPAWN_TIMEOUT)) spawnHullParts();
    }

    public String source() {
        return this.entityData.get(SOURCE);
    }

    public float turretYaw() {
        return this.entityData.get(TURRET_YAW);
    }

    public float barrelPitch() {
        return this.entityData.get(BARREL_PITCH);
    }

    public boolean turretLost() {
        return this.entityData.get(TURRET_LOST);
    }

    public boolean hasAmmunition() {
        return this.entityData.get(HAS_AMMUNITION);
    }

    public int age() {
        if (!level().isClientSide) return this.serverAge;
        long origin = this.entityData.get(AGE_ORIGIN);
        return origin == Long.MIN_VALUE ? 0 : (int) Math.max(0L, Math.min(LIFETIME_TICKS, level().getGameTime() - origin));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(SOURCE, tag.getString("Source"));
        this.entityData.set(TURRET_YAW, tag.getFloat("TurretYaw"));
        this.entityData.set(BARREL_PITCH, tag.getFloat("BarrelPitch"));
        this.entityData.set(TURRET_LOST, tag.getBoolean("TurretLost"));
        this.entityData.set(HAS_AMMUNITION, tag.getBoolean("HasAmmunition"));
        this.entityData.set(HALF_X, tag.getFloat("HalfX"));
        this.entityData.set(HALF_Z, tag.getFloat("HalfZ"));
        this.entityData.set(HEIGHT, tag.getFloat("Height"));
        this.serverAge = Math.max(0, tag.getInt("Age"));
        this.entityData.set(AGE_ORIGIN, level().getGameTime() - this.serverAge);
        this.hullBoxes.clear();
        ListTag boxes = tag.getList("HullBoxes", Tag.TAG_LIST);
        for (int i = 0; i < boxes.size(); i++) {
            ListTag values = boxes.getList(i);
            float[] box = new float[values.size()];
            for (int j = 0; j < box.length; j++) box[j] = values.getFloat(j);
            this.hullBoxes.add(box);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Source", source());
        tag.putFloat("TurretYaw", turretYaw());
        tag.putFloat("BarrelPitch", barrelPitch());
        tag.putBoolean("TurretLost", turretLost());
        tag.putBoolean("HasAmmunition", hasAmmunition());
        tag.putFloat("HalfX", this.entityData.get(HALF_X));
        tag.putFloat("HalfZ", this.entityData.get(HALF_Z));
        tag.putFloat("Height", this.entityData.get(HEIGHT));
        tag.putInt("Age", age());
        ListTag boxes = new ListTag();
        for (float[] box : this.hullBoxes) {
            ListTag values = new ListTag();
            for (float value : box) values.add(FloatTag.valueOf(value));
            boxes.add(values);
        }
        tag.put("HullBoxes", boxes);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
