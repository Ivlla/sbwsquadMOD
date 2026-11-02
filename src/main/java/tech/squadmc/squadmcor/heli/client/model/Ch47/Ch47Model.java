// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.model.Ch47;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.heli.entity.vehicle.Ch47.Ch47Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class Ch47Model extends VehicleModel<Ch47Entity> {

    @Override
    public ResourceLocation getModelResource(Ch47Entity animatable) {
        return new ResourceLocation(squadmc.MODID, "geo/ch47.geo.json");
    }

    @Override
    public @Nullable VehicleModel.TransformContext<Ch47Entity> collectTransform(String boneName) {
        // Tandem rotors, no tail rotor: they turn in opposite directions (that is what cancels the
        // torque). Both bones pivot on the middle of their collective and rest at zero rotation - the
        // modelled blade angles are baked into the geo.
        return switch (boneName) {
            case "rotorfront" -> (bone, vehicle, state) ->
                    bone.setRotY(-Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            case "rotorback" -> (bone, vehicle, state) ->
                    bone.setRotY(Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            default -> super.collectTransform(boneName);
        };
    }
}
