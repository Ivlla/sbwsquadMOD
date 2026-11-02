package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.PumaEntity;
import org.jetbrains.annotations.Nullable;

public class PumaModel extends VehicleModel<PumaEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/puma.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/puma.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    private static final int TRACK_COUNT = 61;
    private static final int MAX_IDX = 61;
    private static final float[][] KEYFRAMES = {
            {176.89f, 22.52f, 52.45f},
            {138.80f, 21.19f, 56.14f},
            {100.02f, 17.82f, 58.14f},
            {61.24f, 13.95f, 57.54f},
            {39.42f, 11.13f, 54.75f},
            {39.42f, 8.59f, 51.66f},
            {39.42f, 6.05f, 48.57f},
            {39.42f, 3.51f, 45.48f},
            {7.41f, 1.96f, 41.87f},
            {0.00f, 1.93f, 37.87f},
            {0.00f, 1.93f, 33.87f},
            {0.00f, 1.93f, 29.87f},
            {0.00f, 1.93f, 25.87f},
            {0.00f, 1.93f, 21.87f},
            {0.00f, 1.93f, 17.87f},
            {0.00f, 1.93f, 13.87f},
            {0.00f, 1.93f, 9.87f},
            {0.00f, 1.93f, 5.86f},
            {0.00f, 1.93f, 1.86f},
            {0.00f, 1.93f, -2.14f},
            {0.00f, 1.93f, -6.14f},
            {0.00f, 1.93f, -10.14f},
            {0.00f, 1.93f, -14.14f},
            {0.00f, 1.93f, -18.14f},
            {0.00f, 1.93f, -22.14f},
            {0.00f, 1.93f, -26.14f},
            {0.00f, 1.93f, -30.14f},
            {-7.15f, 2.02f, -34.13f},
            {-32.43f, 3.71f, -37.72f},
            {-32.43f, 5.85f, -41.10f},
            {-32.43f, 8.00f, -44.47f},
            {-32.43f, 10.14f, -47.85f},
            {-32.61f, 12.29f, -51.23f},
            {-70.12f, 15.27f, -53.77f},
            {-109.19f, 19.19f, -53.84f},
            {-148.26f, 22.25f, -51.40f},
            {-178.23f, 23.10f, -47.56f},
            {179.76f, 23.09f, -43.56f},
            {179.66f, 23.07f, -39.56f},
            {179.66f, 23.05f, -35.56f},
            {179.66f, 23.02f, -31.56f},
            {179.66f, 23.00f, -27.56f},
            {179.66f, 22.97f, -23.56f},
            {179.66f, 22.95f, -19.56f},
            {179.66f, 22.93f, -15.56f},
            {179.66f, 22.90f, -11.56f},
            {179.66f, 22.88f, -7.56f},
            {179.66f, 22.86f, -3.56f},
            {179.66f, 22.83f, 0.44f},
            {179.66f, 22.81f, 4.44f},
            {179.66f, 22.78f, 8.44f},
            {179.66f, 22.76f, 12.44f},
            {179.66f, 22.74f, 16.45f},
            {179.66f, 22.71f, 20.45f},
            {179.66f, 22.69f, 24.45f},
            {179.66f, 22.66f, 28.45f},
            {179.66f, 22.64f, 32.45f},
            {179.66f, 22.62f, 36.45f},
            {179.66f, 22.59f, 40.45f},
            {179.66f, 22.57f, 44.45f},
            {179.66f, 22.55f, 48.45f},
            {176.89f, 22.52f, 52.45f}
    };

    private static final float START_Y = 22.52f;
    private static final float START_Z = 52.45f;

    public PumaModel() {}

    @Override
    public ResourceLocation getModelResource(PumaEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(PumaEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(PumaEntity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<PumaEntity> collectTransform(String boneName) {
        if (boneName.equals("BarrelOccilator")) {
            return (bone, vehicle, state) -> {
                bone.setPosZ(0.0F);
                float activeTimer = vehicle.getShootAnimationTimer(1, 0);

                if (activeTimer > 0) {
                    float maxAnimationTicks = 3.0F;
                    float progress = activeTimer / maxAnimationTicks;
                    float recoilDistance = (float) Math.sin(progress * Math.PI) * 0.5F;
                    bone.setPosZ(bone.getPosZ() - recoilDistance);
                }
            };
        }
        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0F;
        int idx1 = Mth.clamp((int)normalized, 0, 50);
        int idx2 = Mth.clamp(idx1 + 1, 0, 50);
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

    @Override
    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }

    @Override
    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - START_Y;
    }

    @Override
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - START_Z;
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
