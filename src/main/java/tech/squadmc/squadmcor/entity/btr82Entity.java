package tech.squadmc.squadmcor.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import tech.squadmc.squadmcor.init.ModEntities;

public class btr82Entity extends SquadBaseVehicleEntity {
    public static final EntityDataAccessor<Float> STEERING_ANGLE =
            SynchedEntityData.defineId(btr82Entity.class, EntityDataSerializers.FLOAT);

    public float prevWheelRotation = 0.0F;
    public float wheelRotation = 0.0F;
    public float prevSteeringAngle = 0.0F;
    public float steeringAngle = 0.0F;

    public btr82Entity(EntityType<? extends btr82Entity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STEERING_ANGLE, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            float angle = this.entityData.get(STEERING_ANGLE);
            if (this.leftInputDown()) angle = Math.min(30.0F, angle + 2.0F);
            else if (this.rightInputDown()) angle = Math.max(-30.0F, angle - 2.0F);
            else angle *= 0.9F;
            this.entityData.set(STEERING_ANGLE, angle);

            if (Math.abs(angle) > 1.0F) {
                this.setYRot(this.getYRot() + (angle * 0.02F));
            }
        }

        if (this.level().isClientSide) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () ->
                    tech.squadmc.squadmcor.client.ClientAccess.handleBtr82ClientTick(this)
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

    // РЎРїР°РІРЅ РІРµРµСЂР° РґС‹РјРѕРІС‹С… РіСЂР°РЅР°С‚ СЃРёСЃС‚РµРјС‹ 902Р‘ В«РўСѓС‡Р°В»
    public void spawnAdditionalGrenades(TDADummyProjectile original) {
        float baseAngle = this.getYRot() - this.getTurretAngle();
        float pitch = -10.0F;

        float f = -pitch * ((float)Math.PI / 180F);
        float f1 = (float)Math.sin(f);
        float f2 = (float)Math.cos(f);

        float[] angles = new float[]{-35.0F, -12.0F, 12.0F, 35.0F};
        float speed = 0.6F;
        float inaccuracy = 0.5F;

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
        return false;
    }

    public float getPrevWheelRotation() { return this.prevWheelRotation; }
    public float getWheelRotation() { return this.wheelRotation; }
    public float getPrevSteeringAngle() { return this.prevSteeringAngle; }
    public float getSteeringAngle() { return this.steeringAngle; }

    private PlayState cannonFirePredicate(AnimationState<btr82Entity> event) {
        return this.getShootAnimationTimer(1, 0) > 0 ?
                event.setAndContinue(RawAnimation.begin().thenPlay("animation.lav_150.fire")) :
                event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav_150.idle"));
    }

    private PlayState machineGunFirePredicate(AnimationState<btr82Entity> event) {
        return this.getShootAnimationTimer(1, 1) > 0 ?
                event.setAndContinue(RawAnimation.begin().thenPlay("animation.lav_150.fire2")) :
                event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav_150.idle2"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "cannon", 0, this::cannonFirePredicate));
        data.add(new AnimationController<>(this, "machineGun", 0, this::machineGunFirePredicate));
    }

    // В SBW 0.8.9.2 длина цикла гусеницы задаётся техникой, а не моделью.
    @Override
    public int getTrackAnimationLength() {
        return 150;
    }
}
