// Based on Vintage Vehicle Pack by zxcnelti, sergeyrendo (see VVP_CREDITS.txt). Used with the authors' permission. Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Ah64;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.model.Ah64.Ah64Model;
import tech.squadmc.squadmcor.heli.entity.vehicle.Ah64.Ah64Entity;
import tech.squadmc.squadmcor.squadmc;

public class Ah64Renderer extends VehicleRenderer<Ah64Entity> {
    public Ah64Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Ah64Model());
    }

    @Override
    public ResourceLocation getTextureLocation(Ah64Entity entity) {
        return new ResourceLocation(squadmc.MODID, "textures/entity/ah_64.png");
    }
}
