// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Huey.VenomModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.VenomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class VenomRenderer extends VehicleRenderer<VenomEntity> {
    public VenomRenderer(EntityRendererProvider.Context renderManager) {super(renderManager, new VenomModel());}

    @Override
    public ResourceLocation getTextureLocation(VenomEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
