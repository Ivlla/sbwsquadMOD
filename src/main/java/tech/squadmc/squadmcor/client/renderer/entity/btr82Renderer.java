package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.btr82Model; // РСЃРїСЂР°РІР»РµРЅ РїСѓС‚СЊ РёРјРїРѕСЂС‚Р°
import tech.squadmc.squadmcor.entity.btr82Entity;

public class btr82Renderer extends OptimizedVehicleRenderer<btr82Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/btr_82.png");

    public btr82Renderer(EntityRendererProvider.Context renderManager) { // РРјСЏ РєРѕРЅСЃС‚СЂСѓРєС‚РѕСЂР° РґРѕР»Р¶РЅРѕ Р±С‹С‚СЊ btr82Renderer
        super(renderManager, new btr82Model());
    }

    // РњРµС‚РѕРґ getRenderType РІ SuperbWarfare РѕР±С‹С‡РЅРѕ РёСЃРїРѕР»СЊР·СѓРµС‚ СЃСѓС‰РµСЃС‚РІСѓСЋС‰РёРµ РјРµС‚РѕРґС‹ СЂРµРЅРґРµСЂР°
    public RenderType getRenderType(btr82Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable)); // РСЃРїРѕР»СЊР·СѓР№С‚Рµ СЃС‚Р°РЅРґР°СЂС‚РЅС‹Р№ РјРµС‚РѕРґ РїРѕР»СѓС‡РµРЅРёСЏ С‚РµРєСЃС‚СѓСЂС‹
    }
    @Override
    public ResourceLocation getTextureLocation(btr82Entity entity) {

        return CACHED_RESOURCE_0;
    }


}
