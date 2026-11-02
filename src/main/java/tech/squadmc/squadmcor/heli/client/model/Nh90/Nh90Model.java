// Based on Vintage Vehicle Pack by zxcnelti, sergeyrendo (see VVP_CREDITS.txt). Used with the authors' permission. Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.model.Nh90;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.squadmc.squadmcor.heli.client.model.FCPVehicleModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Nh90.Nh90Entity;
import tech.squadmc.squadmcor.squadmc;

public class Nh90Model extends FCPVehicleModel<Nh90Entity> {
    @Override
    public ResourceLocation getModelResource(Nh90Entity entity) {
        return new ResourceLocation(squadmc.MODID, "geo/nh90.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Nh90Entity entity) {
        return new ResourceLocation(squadmc.MODID, "textures/entity/nh90_default.png");
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Override
    public @Nullable TransformContext<Nh90Entity> collectTransform(String boneName) {
        return switch (boneName) {
            case "wing" ->
                    (bone, vehicle, state) -> bone.setRotY(-Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            case "tailPropeller" ->
                    (bone, vehicle, state) -> bone.setRotX(6 * Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            default -> super.collectTransform(boneName);
        };
    }
}
