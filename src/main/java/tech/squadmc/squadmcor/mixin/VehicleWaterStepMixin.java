package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import tech.squadmc.squadmcor.repair.VehicleWaterDamage;

/**
 * Lets floating vehicles drive out onto a bank. Afloat they ride too deep for SBW's step-up, which
 * also only works on the ground, so a vehicle pushing against a bank is lifted until it clears it.
 * Banks taller than the vehicle's step height above the water stay impassable.
 */
@Mixin(value = VehicleEntity.class, remap = false)
public abstract class VehicleWaterStepMixin {

    private static final double LIFT_PER_TICK = 0.15;
    private static final double PUSH_PER_TICK = 0.08;
    private static final float MIN_POWER = 0.01F;
    private static final int MAX_WATER_SCAN = 8;

    @ModifyVariable(method = {"move", "m_6478_"}, at = @At("HEAD"), argsOnly = true)
    private Vec3 squadmc$climbBank(Vec3 movement, MoverType type, Vec3 original) {
        VehicleEntity vehicle = (VehicleEntity) (Object) this;
        if (type != MoverType.SELF || !vehicle.horizontalCollision) return movement;
        float power = vehicle.getPower();
        if (Math.abs(power) < MIN_POWER || !VehicleWaterDamage.canFloat(vehicle)) return movement;

        Double surface = squadmc$waterSurface(vehicle);
        if (surface == null || vehicle.getY() >= surface + vehicle.getStepHeight()) return movement;

        Vec3 motion = vehicle.getDeltaMovement();
        if (motion.y < 0) vehicle.setDeltaMovement(motion.x, 0, motion.z);
        Vec3 push = vehicle.getViewVector(1F).multiply(1, 0, 1).normalize().scale(power > 0 ? PUSH_PER_TICK : -PUSH_PER_TICK);
        return new Vec3(movement.x + push.x, Math.max(movement.y, LIFT_PER_TICK), movement.z + push.z);
    }

    /** Height of the water surface around the vehicle's base, or null if there is no water there. */
    private static Double squadmc$waterSurface(VehicleEntity vehicle) {
        Level level = vehicle.level();
        BlockPos.MutableBlockPos pos = vehicle.blockPosition().mutable();
        // Already lifted clear: the water is just below.
        for (int i = 0; i < 3 && !level.getFluidState(pos).is(FluidTags.WATER); i++) pos.move(0, -1, 0);
        if (!level.getFluidState(pos).is(FluidTags.WATER)) return null;
        for (int i = 0; i < MAX_WATER_SCAN && level.getFluidState(pos.above()).is(FluidTags.WATER); i++) pos.move(0, 1, 0);
        return pos.getY() + (double) level.getFluidState(pos).getHeight(level, pos);
    }
}
