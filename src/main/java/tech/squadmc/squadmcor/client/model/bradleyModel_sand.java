package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.bradley_sandEntity;
import org.jetbrains.annotations.Nullable;

public class bradleyModel_sand extends VehicleModel<bradley_sandEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/bradley.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/m3a3_texture_sand.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    private ResourceLocation lastModel = null;

    private static final int TRACK_COUNT = 47;
    private static final int MAX_IDX = 47;

    private static final float[][] KEYFRAMES = new float[][]{{177.59F, 18.48F, 42.5F}, {133.43F, 16.52F, 46.93F}, {84.41F, 11.92F, 48.47F}, {35.38F, 7.69F, 46.11F}, {23.13F, 5.57F, 41.6F}, {23.13F, 3.61F, 37.01F}, {23.13F, 1.65F, 32.42F}, {0.0F, 0.5F, 27.63F}, {0.0F, 0.5F, 22.64F}, {0.0F, 0.5F, 17.65F}, {0.0F, 0.5F, 12.66F}, {0.0F, 0.5F, 7.67F}, {0.0F, 0.5F, 2.68F}, {0.0F, 0.5F, -2.31F}, {0.0F, 0.5F, -7.3F}, {0.0F, 0.5F, -12.29F}, {0.0F, 0.5F, -17.28F}, {0.0F, 0.5F, -22.27F}, {0.0F, 0.5F, -27.26F}, {0.0F, 0.5F, -32.25F}, {0.0F, 0.5F, -37.24F}, {-24.33F, 1.11F, -42.14F}, {-24.95F, 3.22F, -46.66F}, {-24.95F, 5.32F, -51.19F}, {-24.95F, 7.43F, -55.71F}, {-56.45F, 10.37F, -59.63F}, {-104.9F, 15.17F, -60.25F}, {-153.35F, 18.87F, -57.12F}, {177.52F, 19.32F, -52.23F}, {175.88F, 18.96F, -47.25F}, {175.88F, 18.6F, -42.28F}, {175.88F, 18.24F, -37.3F}, {175.88F, 17.88F, -32.32F}, {176.91F, 17.52F, -27.34F}, {-180.0F, 17.5F, -22.35F}, {-180.0F, 17.5F, -17.36F}, {-180.0F, 17.5F, -12.37F}, {-180.0F, 17.5F, -7.38F}, {-180.0F, 17.5F, -2.39F}, {-180.0F, 17.5F, 2.6F}, {-180.0F, 17.5F, 7.59F}, {-180.0F, 17.5F, 12.58F}, {-180.0F, 17.5F, 17.57F}, {-178.04F, 17.57F, 22.56F}, {-177.39F, 17.8F, 27.54F}, {-177.39F, 18.03F, 32.53F}, {-177.39F, 18.25F, 37.51F}, {177.59F, 18.48F, 42.5F}};

    private static final float START_Y = 18.48F;
    private static final float START_Z = 42.5F;

    public bradleyModel_sand() {}

    @Override
    public ResourceLocation getModelResource(bradley_sandEntity vehicle) {
        ResourceLocation model = CACHED_RESOURCE_0;
        if (this.lastModel != null && !this.lastModel.equals(model)) {
            this.setInit(false);
            this.getTRANSFORMS().clear();
        }
        this.lastModel = model;
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(bradley_sandEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(bradley_sandEntity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<bradley_sandEntity> collectTransform(String boneName) {

        if (boneName.equals("BarrelOccilator")) {
            return (bone, vehicle, state) -> {
                bone.setPosZ(0.0F);
                float activeTimer = vehicle.getShootAnimationTimer(1, 0);

                if (activeTimer > 0) {
                    float maxAnimationTicks = 3.0F;
                    float progress = activeTimer / maxAnimationTicks;
                    float recoilDistance = (float) Math.sin(progress * Math.PI) * 0.45F;
                    bone.setPosZ(bone.getPosZ() - recoilDistance);
                }
            };
        }


        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 94;
        float normalized = t / (float)wrapRange * 47.0F;
        int idx1 = Mth.clamp((int)normalized, 0, 47);
        int idx2 = Mth.clamp(idx1 + 1, 0, 47);
        float frac = normalized - (float)((int)normalized);

        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];

        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0F) p2 -= 360.0F;
            else if (diff < -180.0F) p2 += 360.0F;
        }
        return Mth.lerp(frac, p1, p2);
    }

    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }

    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 18.48F;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 42.5F;
    }

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
