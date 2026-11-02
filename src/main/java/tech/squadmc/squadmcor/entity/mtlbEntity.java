package tech.squadmc.squadmcor.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import tech.squadmc.squadmcor.init.ModEntities;

public class mtlbEntity extends SquadBaseVehicleEntity {

    public int atgmShakeTicks = 0;

    public mtlbEntity(EntityType<? extends mtlbEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.4F) * damage);
    }

    @Override
    public void tick() {
        super.tick();

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

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 101) {
            this.atgmShakeTicks = 4;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean hasDecoy() {
        return false;
    }

    private PlayState cannonFirePredicate(AnimationState<mtlbEntity> event) {
        return this.getShootAnimationTimer(1, 0) > 0 ?
                event.setAndContinue(RawAnimation.begin().thenPlay("animation.lav_150.fire")) :
                event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav_150.idle"));
    }

    private PlayState machineGunFirePredicate(AnimationState<mtlbEntity> event) {
        return this.getShootAnimationTimer(1, 1) > 0 ?
                event.setAndContinue(RawAnimation.begin().thenPlay("animation.lav_150.fire2")) :
                event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav_150.idle2"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "cannon", 0, this::cannonFirePredicate));
        data.add(new AnimationController<>(this, "machineGun", 0, this::machineGunFirePredicate));
    }
}

