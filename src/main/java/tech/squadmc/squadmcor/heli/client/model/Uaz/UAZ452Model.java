// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.model.Uaz;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.heli.client.model.Util.WheelRotationTransforms;
import tech.squadmc.squadmcor.heli.entity.vehicle.Uaz.UAZ452Entity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class UAZ452Model extends VehicleModel<UAZ452Entity> {

    @Override
    public ResourceLocation getModelResource(UAZ452Entity animatable) {
        return new ResourceLocation(squadmc.MODID, "geo/uaz_452.geo.json");
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    @Override
    public @Nullable VehicleModel.TransformContext<UAZ452Entity> collectTransform(String boneName) {

        // Front wheels (whell/whell2) steer; the geo has no separate "...Turn" sub-bone, so the
        // wheel bone itself is what rolls + pivots, exactly like the original UAZ's WheelL0Turn.
        VehicleModel.TransformContext<UAZ452Entity> turn =
                WheelRotationTransforms.matchAnyTurn(boneName, 0.375, 30f,
                        "whell", "whell2");
        if (turn != null) return turn;

        // Rear wheels (whell3/whell4) just roll.
        VehicleModel.TransformContext<UAZ452Entity> wheels =
                WheelRotationTransforms.matchAny(boneName, 0.375,
                        "whell3", "whell4");
        if (wheels != null) return wheels;

        return super.collectTransform(boneName);
    }
}
