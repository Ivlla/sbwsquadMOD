// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Huey.VenomGunshipModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.VenomGunshipEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class VenomGunshipRenderer extends VehicleRenderer<VenomGunshipEntity> {
    public VenomGunshipRenderer(EntityRendererProvider.Context renderManager) {super(renderManager, new VenomGunshipModel());}

    @Override
    public ResourceLocation getTextureLocation(VenomGunshipEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
