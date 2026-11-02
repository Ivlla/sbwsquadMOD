// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Uh60;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Uh60.Mh60lModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Uh60.Mh60lEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class Mh60lRenderer extends VehicleRenderer<Mh60lEntity> {

    public Mh60lRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Mh60lModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Mh60lEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
