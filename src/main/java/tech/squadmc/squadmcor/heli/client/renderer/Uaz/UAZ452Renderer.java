// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Uaz;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Uaz.UAZ452Model;
import tech.squadmc.squadmcor.heli.entity.vehicle.Uaz.UAZ452Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class UAZ452Renderer extends VehicleRenderer<UAZ452Entity> {

    public UAZ452Renderer(EntityRendererProvider.Context renderManager) { super(renderManager, new UAZ452Model());}

    @Override
    public ResourceLocation getTextureLocation(UAZ452Entity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
