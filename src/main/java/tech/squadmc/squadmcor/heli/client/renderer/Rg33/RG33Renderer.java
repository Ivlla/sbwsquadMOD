// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Rg33;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Rg33.RG33Model;
import tech.squadmc.squadmcor.heli.entity.vehicle.Rg33.RG33Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class RG33Renderer extends VehicleRenderer<RG33Entity> {

    public RG33Renderer(EntityRendererProvider.Context renderManager) { super(renderManager, new RG33Model());}

    @Override
    public ResourceLocation getTextureLocation(RG33Entity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
