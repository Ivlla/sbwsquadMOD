package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.StrykerMortatModel;
import tech.squadmc.squadmcor.entity.StrykerMortalEntity;

public class StrykerMortarRender extends OptimizedVehicleRenderer<StrykerMortalEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/stryker.png");


    public StrykerMortarRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StrykerMortatModel());
    }

    @Override
    public RenderType getRenderType(StrykerMortalEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        // entityCutout РІР°Р¶РµРЅ РґР»СЏ РїСЂРѕР·СЂР°С‡РЅС‹С… СЃС‚РµРєРѕР» РєР°Р±РёРЅС‹ Рё С„Р°СЂ
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(StrykerMortalEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
