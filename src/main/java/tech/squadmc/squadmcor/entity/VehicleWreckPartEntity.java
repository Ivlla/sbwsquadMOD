package tech.squadmc.squadmcor.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/**
 * One solid block of a wreck's hull. Minecraft looks for colliding entities only near the area in
 * question, by each entity's position, so one large box goes unnoticed from much of its own extent.
 * A wreck is therefore built from small boxes laid along its rotated hull. They are not saved: the
 * wreck recreates them on load.
 */
public class VehicleWreckPartEntity extends Entity {

    private static final EntityDataAccessor<Float> WIDTH =
            SynchedEntityData.defineId(VehicleWreckPartEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT =
            SynchedEntityData.defineId(VehicleWreckPartEntity.class, EntityDataSerializers.FLOAT);

    private int ownerId = -1;

    public VehicleWreckPartEntity(EntityType<? extends VehicleWreckPartEntity> type, Level level) {
        super(type, level);
    }

    void setup(VehicleWreckEntity owner, float width, float height) {
        this.ownerId = owner.getId();
        this.entityData.set(WIDTH, width);
        this.entityData.set(HEIGHT, height);
        refreshDimensions();
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(WIDTH, 1F);
        this.entityData.define(HEIGHT, 1F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (WIDTH.equals(key) || HEIGHT.equals(key)) refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(this.entityData.get(WIDTH), this.entityData.get(HEIGHT));
    }

    @Override
    public void tick() {
        if (level().isClientSide) return;
        Entity owner = level().getEntity(this.ownerId);
        if (owner == null || owner.isRemoved()) discard();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
