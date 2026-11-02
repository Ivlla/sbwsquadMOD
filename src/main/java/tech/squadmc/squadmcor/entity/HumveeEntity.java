package tech.squadmc.squadmcor.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HumveeEntity extends SquadBaseVehicleEntity {
    private static final EntityDataAccessor<Float> STEERING_ANGLE =
            SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.FLOAT);

    private float prevSteeringAngle = 0.0F;
    private float wheelRotation = 0.0F;
    private float prevWheelRotation = 0.0F;

    public HumveeEntity(EntityType<? extends HumveeEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STEERING_ANGLE, 0.0F);
    }

    public float getSteeringAngle() {
        return this.entityData.get(STEERING_ANGLE);
    }

    public void setSteeringAngle(float angle) {
        this.entityData.set(STEERING_ANGLE, angle);
    }

    public float getPrevSteeringAngle() {
        return this.prevSteeringAngle;
    }

    public float getWheelRotation() {
        return this.wheelRotation;
    }

    public float getPrevWheelRotation() {
        return this.prevWheelRotation;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.6F) * damage);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("SteeringAngle", this.getSteeringAngle());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SteeringAngle")) {
            this.setSteeringAngle(compound.getFloat("SteeringAngle"));
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.prevSteeringAngle = this.getSteeringAngle();
        float currentAngle = this.getSteeringAngle();

        Vec3 movement = this.getDeltaMovement();
        double speed = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
        boolean isMoving = speed > 0.05;

        boolean turningLeft = this.leftInputDown();
        boolean turningRight = this.rightInputDown();

        if (turningLeft && !turningRight) {
            currentAngle = Math.min(45.0F, currentAngle + 2.0F);
            this.setSteeringAngle(currentAngle);
        } else if (turningRight && !turningLeft) {
            currentAngle = Math.max(-45.0F, currentAngle - 2.0F);
            this.setSteeringAngle(currentAngle);
        } else if (isMoving && Math.abs(currentAngle) > 0.5F) {
            currentAngle *= 0.9F;
            this.setSteeringAngle(currentAngle);
        }

        if (isMoving && Math.abs(currentAngle) > 1.0F) {
            float turnAmount = currentAngle * 0.009F * (float)speed;
            this.setYRot(this.getYRot() + turnAmount);
        }

        this.prevWheelRotation = this.wheelRotation;
        this.wheelRotation += (float)(speed * 20.0F);

        // РљР»РёРµРЅС‚СЃРєР°СЏ С‡Р°СЃС‚СЊ РґР»СЏ Р·РІСѓРєРѕРІ РґРІРёРіР°С‚РµР»СЏ РјР°С€РёРЅС‹
        if (this.level().isClientSide) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () ->
                    tech.squadmc.squadmcor.client.sound.HumveeSoundManager.updateSounds(this)
            );
        }
    }
}
