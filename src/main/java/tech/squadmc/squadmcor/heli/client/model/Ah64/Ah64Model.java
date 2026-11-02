// Based on Vintage Vehicle Pack by zxcnelti, sergeyrendo (see VVP_CREDITS.txt). Used with the authors' permission. Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.model.Ah64;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.squadmc.squadmcor.heli.client.model.FCPVehicleModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Ah64.Ah64Entity;
import tech.squadmc.squadmcor.squadmc;

public class Ah64Model extends FCPVehicleModel<Ah64Entity> {
    @Override
    public ResourceLocation getModelResource(Ah64Entity entity) {
        return new ResourceLocation(squadmc.MODID, "geo/ah_64.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Ah64Entity entity) {
        return new ResourceLocation(squadmc.MODID, "textures/entity/ah_64.png");
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Override
    public @Nullable TransformContext<Ah64Entity> collectTransform(String boneName) {
        return switch (boneName) {
            case "propeller" ->
                    (bone, vehicle, state) -> bone.setRotY(-Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            case "tailPropeller" ->
                    (bone, vehicle, state) -> bone.setRotX(6 * Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            default -> super.collectTransform(boneName);
        };
    }
}
