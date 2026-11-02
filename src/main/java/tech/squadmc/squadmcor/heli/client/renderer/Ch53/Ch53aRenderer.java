// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Ch53;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Ch53.Ch53aModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Ch53.Ch53aEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class Ch53aRenderer extends VehicleRenderer<Ch53aEntity> {

    public Ch53aRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Ch53aModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Ch53aEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
