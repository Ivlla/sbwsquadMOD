package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.UralGradModel;
import tech.squadmc.squadmcor.entity.UralGradEntity;

public class UralGradRender extends OptimizedVehicleRenderer<UralGradEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/ural.png");


    public UralGradRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new UralGradModel());
    }

    @Override
    public RenderType getRenderType(UralGradEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        // entityCutout РІР°Р¶РµРЅ РґР»СЏ РїСЂРѕР·СЂР°С‡РЅС‹С… СЃС‚РµРєРѕР» РєР°Р±РёРЅС‹ Рё С„Р°СЂ
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(UralGradEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
