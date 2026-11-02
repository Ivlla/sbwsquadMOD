// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Huey.HueyRocketsModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyRocketsEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class HueyRocketsRenderer extends VehicleRenderer<HueyRocketsEntity> {
    public HueyRocketsRenderer(EntityRendererProvider.Context renderManager) {super(renderManager, new HueyRocketsModel());}

    @Override
    public ResourceLocation getTextureLocation(HueyRocketsEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
