// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Huey.HueyDoorGunnerM60Model;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyDoorGunnerM60Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class HueyDoorGunnerM60Renderer extends VehicleRenderer<HueyDoorGunnerM60Entity> {
    public HueyDoorGunnerM60Renderer(EntityRendererProvider.Context renderManager) {super(renderManager, new HueyDoorGunnerM60Model());}

    @Override
    public ResourceLocation getTextureLocation(HueyDoorGunnerM60Entity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
