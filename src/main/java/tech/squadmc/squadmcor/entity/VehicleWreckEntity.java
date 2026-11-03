package tech.squadmc.squadmcor.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkHooks;
import tech.squadmc.squadmcor.client.wreck.VehicleWreckClient;
import tech.squadmc.squadmcor.init.ModEntities;

/**
 * The burnt-out hull left by a destroyed vehicle. It is a separate entity so the vehicle itself is
 * removed at the moment of its death, which is what AAS reads as the vehicle being lost.
 */
public class VehicleWreckEntity extends Entity {

    public static final int FIRE_TICKS = 20 * 60 * 2;
    public static final int SMOKE_TICKS = 20 * 60 * 5;
    public static final int LIFETIME_TICKS = 20 * 60 * 15;

    private static final double GRAVITY = 0.04;

    private static final EntityDataAccessor<String> SOURCE =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> TURRET_YAW =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BARREL_PITCH =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> TURRET_LOST =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> WIDTH =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> AGE =
            SynchedEntityData.defineId(VehicleWreckEntity.class, EntityDataSerializers.INT);

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
        wreck.entityData.set(WIDTH, vehicle.getBbWidth());
        wreck.entityData.set(HEIGHT, vehicle.getBbHeight());
        wreck.refreshDimensions();
        vehicle.ejectPassengers();
        vehicle.generateWreckageLoot();
        vehicle.discard();
        level.addFreshEntity(wreck);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SOURCE, "");
        this.entityData.define(TURRET_YAW, 0F);
        this.entityData.define(BARREL_PITCH, 0F);
        this.entityData.define(TURRET_LOST, false);
        this.entityData.define(WIDTH, 2F);
        this.entityData.define(HEIGHT, 2F);
        this.entityData.define(AGE, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (WIDTH.equals(key) || HEIGHT.equals(key)) refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(this.entityData.get(WIDTH), this.entityData.get(HEIGHT));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> VehicleWreckClient.tick(this));
            return;
        }
        int age = this.entityData.get(AGE) + 1;
        if (age >= LIFETIME_TICKS) {
            discard();
            return;
        }
        this.entityData.set(AGE, age);
        if (!onGround()) {
            setDeltaMovement(getDeltaMovement().add(0, -GRAVITY, 0));
            move(MoverType.SELF, getDeltaMovement());
        }
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

    public int age() {
        return this.entityData.get(AGE);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(SOURCE, tag.getString("Source"));
        this.entityData.set(TURRET_YAW, tag.getFloat("TurretYaw"));
        this.entityData.set(BARREL_PITCH, tag.getFloat("BarrelPitch"));
        this.entityData.set(TURRET_LOST, tag.getBoolean("TurretLost"));
        this.entityData.set(WIDTH, tag.getFloat("Width"));
        this.entityData.set(HEIGHT, tag.getFloat("Height"));
        this.entityData.set(AGE, tag.getInt("Age"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Source", source());
        tag.putFloat("TurretYaw", turretYaw());
        tag.putFloat("BarrelPitch", barrelPitch());
        tag.putBoolean("TurretLost", turretLost());
        tag.putFloat("Width", this.entityData.get(WIDTH));
        tag.putFloat("Height", this.entityData.get(HEIGHT));
        tag.putInt("Age", age());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
