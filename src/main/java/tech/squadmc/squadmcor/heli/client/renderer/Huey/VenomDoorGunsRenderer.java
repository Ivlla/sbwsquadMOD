// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Huey.VenomDoorGunsModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.VenomDoorGunsEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class VenomDoorGunsRenderer extends VehicleRenderer<VenomDoorGunsEntity> {
    public VenomDoorGunsRenderer(EntityRendererProvider.Context renderManager) {super(renderManager, new VenomDoorGunsModel());}

    @Override
    public ResourceLocation getTextureLocation(VenomDoorGunsEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
