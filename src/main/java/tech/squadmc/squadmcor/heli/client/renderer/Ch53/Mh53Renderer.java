// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Ch53;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Ch53.Mh53Model;
import tech.squadmc.squadmcor.heli.entity.vehicle.Ch53.Mh53Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class Mh53Renderer extends VehicleRenderer<Mh53Entity> {

    public Mh53Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Mh53Model());
    }

    @Override
    public ResourceLocation getTextureLocation(Mh53Entity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
