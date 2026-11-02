// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.model.Huey;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyDoorGunnerM134Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyDoorGunnerM60Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class HueyDoorGunnerM134Model extends VehicleModel<HueyDoorGunnerM134Entity> {

    @Override
    public ResourceLocation getModelResource(HueyDoorGunnerM134Entity animatable) {
        return new ResourceLocation(squadmc.MODID, "geo/huey_minigun.geo.json");
    }

    @Override
    public @Nullable VehicleModel.TransformContext<HueyDoorGunnerM134Entity> collectTransform(String boneName) {
        return switch (boneName) {
            case "propeller" ->
                    (bone, vehicle, state) -> bone.setRotY(-Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            case "tailPropeller" ->
                    (bone, vehicle, state) -> bone.setRotX(6 * Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            case "BarrelRotationController" ->
                    (bone, vehicle, state) -> bone.setRotZ(-Mth.lerp(state.getPartialTick(), vehicle.getBarrelRot0(), vehicle.getBarrelRot()));
            default -> super.collectTransform(boneName);
        };
    }
}
