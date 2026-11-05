package tech.squadmc.squadmcor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.squadmc.squadmcor.entity.projectile.SpikeMissileEntity;
import tech.squadmc.squadmcor.squadmc;

public final class SpikeMissileRenderer extends GeoEntityRenderer<SpikeMissileEntity> {
    public SpikeMissileRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
    }

    @Override
    public RenderType getRenderType(SpikeMissileEntity entity, ResourceLocation texture,
                                   MultiBufferSource buffers, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void render(SpikeMissileEntity entity, float yaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(-Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot())));
        pose.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        // The model's nose points towards -Z.
        pose.mulPose(Axis.YP.rotationDegrees(180F));
        super.render(entity, yaw, partialTick, pose, buffers, light);
        pose.popPose();
    }

    private static final class Model extends GeoModel<SpikeMissileEntity> {
        @Override
        public ResourceLocation getModelResource(SpikeMissileEntity entity) {
            return new ResourceLocation(squadmc.MODID, "geo/spike_missile.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(SpikeMissileEntity entity) {
            return new ResourceLocation("superbwarfare", "textures/bedrock/projectile/javelin_missile.png");
        }

        @Override
        public ResourceLocation getAnimationResource(SpikeMissileEntity entity) {
            // SBW no longer ships a missile animation; the entity has no controllers, so this is never read.
            return new ResourceLocation("superbwarfare", "animations/javelin.animation.json");
        }
    }
}
