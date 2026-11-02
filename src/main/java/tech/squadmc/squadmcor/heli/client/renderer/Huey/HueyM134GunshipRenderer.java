// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Huey.HueyM134GunshipModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyM134GunshipEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class HueyM134GunshipRenderer extends VehicleRenderer<HueyM134GunshipEntity> {
    public HueyM134GunshipRenderer(EntityRendererProvider.Context renderManager) {super(renderManager, new HueyM134GunshipModel());}

    @Override
    public ResourceLocation getTextureLocation(HueyM134GunshipEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
