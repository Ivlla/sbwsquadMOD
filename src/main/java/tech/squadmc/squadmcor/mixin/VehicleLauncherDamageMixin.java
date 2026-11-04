package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tech.squadmc.squadmcor.repair.LauncherDamage;

/** Swaps SBW's armour calculation for the launcher table, keeping the rest of its damage handling. */
@Mixin(value = VehicleEntity.class, remap = false)
public abstract class VehicleLauncherDamageMixin {

    @Redirect(method = {"hurt", "m_6469_"}, at = @At(value = "INVOKE",
            target = "Lcom/atsuishio/superbwarfare/entity/vehicle/damage/DamageModifier;compute(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float squadmc$launcherDamage(DamageModifier modifier, Entity entity, DamageSource source, float damage) {
        float launcher = LauncherDamage.compute((VehicleEntity) (Object) this, source);
        return launcher >= 0F ? launcher : modifier.compute(entity, source, damage);
    }
}
