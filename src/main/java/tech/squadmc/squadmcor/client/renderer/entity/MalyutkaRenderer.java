package tech.squadmc.squadmcor.client.renderer.entity;

import com.atsuishio.superbwarfare.Mod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.squadmc.squadmcor.client.model.MalyutkaModel;
import tech.squadmc.squadmcor.entity.projectile.MalyutkaEntity;

public class MalyutkaRenderer extends GeoEntityRenderer<MalyutkaEntity> {

    public MalyutkaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MalyutkaModel());
    }


    @Override
    public RenderType getRenderType(MalyutkaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(MalyutkaEntity entity) {
        return new ResourceLocation("squadmc", "textures/entity/malyutka.png");
    }

    @Override
    public void preRender(PoseStack poseStack, MalyutkaEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
    }


}
