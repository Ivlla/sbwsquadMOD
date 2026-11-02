// Based on Vintage Vehicle Pack by zxcnelti, sergeyrendo (see VVP_CREDITS.txt). Used with the authors' permission. Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.renderer.Nh90;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.model.Nh90.Nh90Model;
import tech.squadmc.squadmcor.heli.entity.vehicle.Nh90.Nh90Entity;
import tech.squadmc.squadmcor.squadmc;

public class Nh90Renderer extends VehicleRenderer<Nh90Entity> {
    public Nh90Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Nh90Model());
    }

    @Override
    public ResourceLocation getTextureLocation(Nh90Entity entity) {
        return new ResourceLocation(squadmc.MODID, "textures/entity/nh90_default.png");
    }
}
