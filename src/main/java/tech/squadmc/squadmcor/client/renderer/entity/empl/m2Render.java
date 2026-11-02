package tech.squadmc.squadmcor.client.renderer.entity.empl;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.empl.M2Model;
import tech.squadmc.squadmcor.entity.empl.M2Entity;



public class m2Render extends OptimizedVehicleRenderer<M2Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/turrets/m2.png");

    public m2Render(EntityRendererProvider.Context renderManager) {
        super(renderManager, new M2Model());
    }

    @Override
    public RenderType getRenderType(M2Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(M2Entity entity) {
        return CACHED_RESOURCE_0;
    }
}

