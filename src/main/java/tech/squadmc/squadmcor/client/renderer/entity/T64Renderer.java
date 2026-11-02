package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.T64Model;
import tech.squadmc.squadmcor.entity.T64Entity;
import tech.squadmc.squadmcor.squadmc;

public class T64Renderer extends OptimizedVehicleRenderer<T64Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation(squadmc.MODID, "textures/entity/t64.png");

    public T64Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new T64Model());
    }

    @Override
    public RenderType getRenderType(T64Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(T64Entity entity) {
        return CACHED_RESOURCE_0;
    }
}
