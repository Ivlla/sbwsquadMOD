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
import tech.squadmc.squadmcor.init.ModEntities;

public class StrykerMortalEntity extends SquadBaseVehicleEntity {

    public static final EntityDataAccessor<Float> STEERING_ANGLE =
            SynchedEntityData.defineId(StrykerMortalEntity.class, EntityDataSerializers.FLOAT);

    public float prevSteeringAngle = 0.0F;
    public float wheelRotation = 0.0F;
    public float prevWheelRotation = 0.0F;

    public StrykerMortalEntity(EntityType<? extends StrykerMortalEntity> type, Level world) {
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
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.3F) * damage);
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
            currentAngle = Math.min(45.0F, currentAngle + 1.5F);
            this.setSteeringAngle(currentAngle);
        } else if (turningRight && !turningLeft) {
            currentAngle = Math.max(-45.0F, currentAngle - 1.5F);
            this.setSteeringAngle(currentAngle);
        } else if (isMoving && Math.abs(currentAngle) > 0.5F) {
            currentAngle *= 0.85F;
            this.setSteeringAngle(currentAngle);
        }

        if (isMoving && Math.abs(currentAngle) > 1.0F) {
            float turnAmount = currentAngle * 0.007F * (float)speed;
            this.setYRot(this.getYRot() + turnAmount);
        }

        this.prevWheelRotation = this.wheelRotation;
        this.wheelRotation += (float)(speed * 18.0F);

        if (this.level().isClientSide) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () ->
                    tech.squadmc.squadmcor.client.ClientAccess.handleStrykerMortarClientTick(this)
            );
        }

        // Дымогенератор (VehicleSmokeSystem): клиент шлёт пульс, сервер тикает генератор
        if (this.level().isClientSide) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () ->
                    tech.squadmc.squadmcor.client.ClientAccess.sendSmokePulse(this));
        } else {
            this.updateSmokeGenerator(false);
        }
    }

    @Override
    public boolean hasSmokeGenerator() { return true; }

    @Override
    public Vec3 getSmokeGeneratorOffset() { return new Vec3(0.0, 0.7, 0.0); }

    public void spawnAdditionalGrenades(TDADummyProjectile original) {
        float baseAngle = this.getYRot() - this.getTurretAngle();
        float pitch = -10.0F;

        float f = -pitch * ((float)Math.PI / 180F);
        float f1 = (float)Math.sin(f);
        float f2 = (float)Math.cos(f);

        float[] angles = new float[]{-45.0F, -15.0F, 15.0F, 45.0F};
        float speed = 0.65F;
        float inaccuracy = 0.4F;

        float yaw0 = baseAngle + angles[0];
        float f3_0 = -yaw0 * ((float)Math.PI / 180F);
        double vx0 = Math.sin(f3_0) * f2;
        double vy0 = f1;
        double vz0 = Math.cos(f3_0) * f2;
        original.shoot(vx0, vy0, vz0, speed, inaccuracy);

        for (int i = 1; i < angles.length; i++) {
            float yaw = baseAngle + angles[i];
            float f3 = -yaw * ((float)Math.PI / 180F);
            double vx = Math.sin(f3) * f2;
            double vy = f1;
            double vz = Math.cos(f3) * f2;

            TDADummyProjectile grenade = new TDADummyProjectile(ModEntities.TDA_DUMMY.get(), this.level());
            grenade.setAdditional(true);
            grenade.setPos(original.getX(), original.getY(), original.getZ());
            grenade.setOwner(original.getOwner());
            grenade.shoot(vx, vy, vz, speed, inaccuracy);
            this.level().addFreshEntity(grenade);
        }
    }

    private float getTurretAngle() {
        try {
            java.lang.reflect.Field field = com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity.class.getDeclaredField("turretYRot");
            field.setAccessible(true);
            return field.getFloat(this);
        } catch (Exception e) {
            return this.getYRot();
        }
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }
}

