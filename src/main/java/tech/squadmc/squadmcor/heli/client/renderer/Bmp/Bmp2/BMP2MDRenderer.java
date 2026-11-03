// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Bmp.Bmp2;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import tech.squadmc.squadmcor.heli.client.model.Bmp.Bmp2.BMP2MDModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.Bmp.Bmp2.BMP2MDEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;

public class BMP2MDRenderer extends VehicleRenderer<BMP2MDEntity> {
    public BMP2MDRenderer(EntityRendererProvider.Context c) { super(c, new BMP2MDModel()); }
    @Override public ResourceLocation getTextureLocation(BMP2MDEntity e) { return FcpVehicleTexture.resolve(e, e.getCurrentTexture()); }
}
