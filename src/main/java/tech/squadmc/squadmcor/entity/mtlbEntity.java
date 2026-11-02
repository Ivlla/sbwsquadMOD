package tech.squadmc.squadmcor.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
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

    /** Анимаций у этой модели нет. */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }
}
