package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.mtlbModel;
import tech.squadmc.squadmcor.entity.mtlbEntity;

public class mtlbRender extends OptimizedVehicleRenderer<mtlbEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/mtlb.png");

    public mtlbRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new mtlbModel());
    }

    @Override
    public RenderType getRenderType(mtlbEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }


    @Override
    public ResourceLocation getTextureLocation(mtlbEntity entity) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public void render(mtlbEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        float scaleX = 1.2F; // РЁРёСЂРёРЅР°
        float scaleY = 1.2F; // Р’С‹СЃРѕС‚Р°
        float scaleZ = 1.2F;  // Р”Р»РёРЅР°

        poseStack.scale(scaleX, scaleY, scaleZ);


        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}

