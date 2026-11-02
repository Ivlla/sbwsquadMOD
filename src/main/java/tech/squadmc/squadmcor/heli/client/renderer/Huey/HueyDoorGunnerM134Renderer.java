// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Huey.HueyDoorGunnerM134Model;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyDoorGunnerM134Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class HueyDoorGunnerM134Renderer extends VehicleRenderer<HueyDoorGunnerM134Entity> {
    public HueyDoorGunnerM134Renderer(EntityRendererProvider.Context renderManager) {super(renderManager, new HueyDoorGunnerM134Model());}

    @Override
    public ResourceLocation getTextureLocation(HueyDoorGunnerM134Entity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
