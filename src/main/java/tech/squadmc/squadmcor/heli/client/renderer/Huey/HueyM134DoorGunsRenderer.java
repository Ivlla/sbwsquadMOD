// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Huey.HueyM134DoorGunsModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyM134DoorGunsEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class HueyM134DoorGunsRenderer extends VehicleRenderer<HueyM134DoorGunsEntity> {
    public HueyM134DoorGunsRenderer(EntityRendererProvider.Context renderManager) {super(renderManager, new HueyM134DoorGunsModel());}

    @Override
    public ResourceLocation getTextureLocation(HueyM134DoorGunsEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
