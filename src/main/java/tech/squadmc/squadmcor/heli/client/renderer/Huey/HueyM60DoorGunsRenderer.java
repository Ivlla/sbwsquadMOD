// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Huey.HueyM60DoorGunsModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyM60DoorGunsEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class HueyM60DoorGunsRenderer extends VehicleRenderer<HueyM60DoorGunsEntity> {
    public HueyM60DoorGunsRenderer(EntityRendererProvider.Context renderManager) {super(renderManager, new HueyM60DoorGunsModel());}

    @Override
    public ResourceLocation getTextureLocation(HueyM60DoorGunsEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
