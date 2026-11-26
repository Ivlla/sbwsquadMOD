package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.squadmc.squadmcor.repair.WheelLoss;

/**
 * Гарантированно выводит из строя ходовую часть техники в радиусе поражения
 * наземной мины Superb Warfare. Работает на сервере до обычного расчёта взрыва.
 */
@Mixin(targets = "com.atsuishio.superbwarfare.tools.CustomExplosion", remap = false)
public abstract class MineVehicleDamageMixin {
    @Shadow @Final private Level level;
    @Shadow @Final private Entity entity;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Shadow @Final private float radius;

    private static final TagKey<EntityType<?>> SQUADMC$MINES = TagKey.create(
            Registries.ENTITY_TYPE, new ResourceLocation("superbwarfare", "mine"));

    @Inject(method = "explode", at = @At("HEAD"))
    private void squadmc$breakVehicleMobility(CallbackInfo ci) {
        if (level.isClientSide || entity == null || !entity.getType().is(SQUADMC$MINES)) return;

        // SBW наносит урон сущностям в пределах двух радиусов взрыва.
        double range = Math.max(0.0D, radius * 2.0D);
        if (range <= 0.0D) return;

        Vec3 center = new Vec3(x, y, z);
        AABB area = new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
        for (VehicleEntity vehicle : level.getEntitiesOfClass(VehicleEntity.class, area,
                candidate -> !candidate.isWreck()
                        && candidate.distanceToSqr(center) <= range * range)) {
            EngineType engineType = vehicle.computed().getEngineType();
            if (engineType == EngineType.WHEEL) {
                // WheelLoss выбивает по одному колесу за вызов; повторяем для обеих сторон.
                int leftCount = WheelLoss.wheelsOnSide(vehicle, true);
                int rightCount = WheelLoss.wheelsOnSide(vehicle, false);
                for (int i = 0; i < leftCount; i++) {
                    if (vehicle.getLeftWheelMaxHealth() <= 0.0F) break;
                    vehicle.setLeftWheelHealth(0.0F);
                    WheelLoss.update(vehicle);
                }
                for (int i = 0; i < rightCount; i++) {
                    if (vehicle.getRightWheelMaxHealth() <= 0.0F) break;
                    vehicle.setRightWheelHealth(0.0F);
                    WheelLoss.update(vehicle);
                }
            } else if (engineType == EngineType.TRACK) {
                // Для гусеничной техники SBW использует те же два слота здоровья,
                // но отображает их и ремонтирует как левую и правую гусеницы.
                if (vehicle.getLeftWheelMaxHealth() > 0.0F) {
                    vehicle.setLeftWheelHealth(0.0F);
                    vehicle.setLeftWheelDamaged(true);
                }
                if (vehicle.getRightWheelMaxHealth() > 0.0F) {
                    vehicle.setRightWheelHealth(0.0F);
                    vehicle.setRightWheelDamaged(true);
                }
            }
        }
    }
}
