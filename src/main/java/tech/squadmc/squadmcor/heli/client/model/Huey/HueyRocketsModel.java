// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.model.Huey;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyRocketsEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class HueyRocketsModel extends VehicleModel<HueyRocketsEntity> {

    @Override
    public ResourceLocation getModelResource(HueyRocketsEntity animatable) {
        return new ResourceLocation(squadmc.MODID, "geo/huey_rockets.geo.json");
    }

    @Override
    public @Nullable VehicleModel.TransformContext<HueyRocketsEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "propeller" ->
                    (bone, vehicle, state) -> bone.setRotY(-Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            case "tailPropeller" ->
                    (bone, vehicle, state) -> bone.setRotX(6 * Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            default -> super.collectTransform(boneName);
        };
    }
}
