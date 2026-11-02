package tech.squadmc.squadmcor.client.performance;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public abstract class OptimizedVehicleRenderer<T extends VehicleEntity & GeoAnimatable> extends VehicleRenderer<T> {
    // JVM switch for comparison/fallback, without replacing source files.
    private static final boolean CACHE_ENABLED = !Boolean.getBoolean("squadmc.disableVehicleMeshCache");
    private final VehicleMeshCache meshes = new VehicleMeshCache();

    protected OptimizedVehicleRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    @Override
    public void preRender(PoseStack poseStack, T vehicle, BakedGeoModel model, MultiBufferSource buffers,
                          VertexConsumer buffer, boolean isReRender, float partialTick, int light,
                          int overlay, float red, float green, float blue, float alpha) {
        meshes.useModel(model);
        super.preRender(poseStack, vehicle, model, buffers, buffer, isReRender, partialTick,
                light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer,
                                int light, int overlay, float red, float green, float blue, float alpha) {
        if (!CACHE_ENABLED) {
            super.renderCubesOfBone(poseStack, bone, buffer, light, overlay, red, green, blue, alpha);
            return;
        }
        if (bone.isHidden() || bone.getCubes().isEmpty()) return;
        meshes.get(bone).render(poseStack, buffer, light, overlay, red, green, blue, alpha);
    }
}
