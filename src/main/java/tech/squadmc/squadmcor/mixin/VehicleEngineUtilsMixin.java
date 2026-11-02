package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleEngineUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** Без колеса машина едет медленнее и слегка тянет в сторону, но остаётся управляемой. */
@Mixin(value = VehicleEngineUtils.class, remap = false)
public abstract class VehicleEngineUtilsMixin {

    @ModifyConstant(method = "wheelEngine", constant = @Constant(floatValue = 0.975F))
    private static float squadmc$oneSidePower(float original) {
        return 0.992F;
    }

    /** Первая константа 0.93 в методе относится к дрифту, вторая к потере колёс на обоих бортах. */
    @ModifyConstant(method = "wheelEngine", constant = @Constant(floatValue = 0.93F, ordinal = 1))
    private static float squadmc$bothSidesPower(float original) {
        return 0.985F;
    }

    @ModifyConstant(method = "wheelEngine", constant = @Constant(intValue = 3), require = 0)
    private static int squadmc$leftPull(int original) {
        return 1;
    }

    @ModifyConstant(method = "wheelEngine", constant = @Constant(intValue = -3), require = 0)
    private static int squadmc$rightPull(int original) {
        return -1;
    }
}
