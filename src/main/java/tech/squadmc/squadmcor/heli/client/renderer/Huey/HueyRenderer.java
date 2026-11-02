// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Huey.HueyModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class HueyRenderer extends VehicleRenderer<HueyEntity> {
    public HueyRenderer(EntityRendererProvider.Context renderManager) {super(renderManager, new HueyModel());}

    @Override
    public ResourceLocation getTextureLocation(HueyEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
