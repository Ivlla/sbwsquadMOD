// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Ch53;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Ch53.Ch53eModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Ch53.Ch53eEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class Ch53eRenderer extends VehicleRenderer<Ch53eEntity> {

    public Ch53eRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Ch53eModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Ch53eEntity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
