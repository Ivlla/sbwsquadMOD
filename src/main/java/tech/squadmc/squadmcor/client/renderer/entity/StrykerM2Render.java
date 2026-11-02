package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.StrykerM2Model;
import tech.squadmc.squadmcor.entity.StrykerM2Entity;

public class StrykerM2Render extends OptimizedVehicleRenderer<StrykerM2Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/stryker.png");


    public StrykerM2Render(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StrykerM2Model());
    }

    @Override
    public RenderType getRenderType(StrykerM2Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        // entityCutout РІР°Р¶РµРЅ РґР»СЏ РїСЂРѕР·СЂР°С‡РЅС‹С… СЃС‚РµРєРѕР» РєР°Р±РёРЅС‹ Рё С„Р°СЂ
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(StrykerM2Entity entity) {
        return CACHED_RESOURCE_0;
    }
}
