package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.MotorboatModel;
import tech.squadmc.squadmcor.entity.MotorboatEntity;

public class MotorboatRender extends OptimizedVehicleRenderer<MotorboatEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/boat.png");


    public MotorboatRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MotorboatModel());
    }

    @Override
    public RenderType getRenderType(MotorboatEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(MotorboatEntity entity) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public void render(MotorboatEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // РќР°СЃС‚СЂРѕР№РєР° РјР°СЃС€С‚Р°Р±Р° РїРѕ РѕСЃСЏРј [X (С€РёСЂРёРЅР°), Y (РІС‹СЃРѕС‚Р°), Z (РґР»РёРЅР°)]
        float scaleX = 1.55F; // РЁРёСЂРёРЅР°
        float scaleY = 1.2F; // Р’С‹СЃРѕС‚Р°
        float scaleZ = 1.55F;  // Р”Р»РёРЅР°

        poseStack.scale(scaleX, scaleY, scaleZ);

        // РќРµР±РѕР»СЊС€РѕР№ РїРѕРґСЉРµРј РјРѕРґРµР»Рё РЅР°Рґ РІРѕРґРѕР№
        float yOffset = 0.45F;
        poseStack.translate(0.0D, yOffset, 0.0D);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}
