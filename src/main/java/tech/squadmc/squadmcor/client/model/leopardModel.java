package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.leopardEntity;
import org.jetbrains.annotations.Nullable;

public class leopardModel extends VehicleModel<leopardEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/leopard_2a7v.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/leopard_2a7v_camo.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    private static final int TRACK_COUNT = 71;
    private static final int MAX_IDX = 71;
    private static final float[][] KEYFRAMES = {
            {176.79f, 22.50f, 59.75f},
            {143.01f, 21.27f, 63.38f},
            {103.55f, 18.10f, 65.53f},
            {69.02f, 14.28f, 65.32f},
            {38.21f, 11.35f, 62.85f},
            {39.45f, 8.87f, 59.84f},
            {39.45f, 6.39f, 56.83f},
            {39.45f, 3.92f, 53.82f},
            {31.02f, 1.47f, 50.79f},
            {0.57f, 0.50f, 47.07f},
            {0.00f, 0.50f, 43.17f},
            {0.00f, 0.50f, 39.27f},
            {0.00f, 0.50f, 35.37f},
            {0.00f, 0.50f, 31.47f},
            {0.00f, 0.50f, 27.57f},
            {0.00f, 0.50f, 23.67f},
            {0.00f, 0.50f, 19.78f},
            {0.00f, 0.50f, 15.88f},
            {0.00f, 0.50f, 11.98f},
            {0.00f, 0.50f, 8.08f},
            {0.00f, 0.50f, 4.18f},
            {0.00f, 0.50f, 0.28f},
            {0.00f, 0.50f, -3.62f},
            {0.00f, 0.50f, -7.51f},
            {0.00f, 0.50f, -11.41f},
            {0.00f, 0.50f, -15.31f},
            {0.00f, 0.50f, -19.21f},
            {0.00f, 0.50f, -23.11f},
            {0.00f, 0.50f, -27.01f},
            {0.00f, 0.50f, -30.90f},
            {0.00f, 0.50f, -34.80f},
            {0.00f, 0.50f, -38.70f},
            {-17.34f, 0.74f, -42.58f},
            {-35.92f, 2.66f, -45.94f},
            {-35.92f, 4.95f, -49.09f},
            {-35.92f, 7.24f, -52.25f},
            {-35.92f, 9.52f, -55.41f},
            {-35.92f, 11.81f, -58.57f},
            {-61.23f, 14.43f, -61.40f},
            {-95.17f, 18.17f, -62.21f},
            {-133.95f, 21.64f, -60.59f},
            {-172.73f, 23.42f, -57.20f},
            {-179.76f, 23.47f, -53.30f},
            {179.51f, 23.44f, -49.41f},
            {179.51f, 23.41f, -45.51f},
            {179.51f, 23.37f, -41.61f},
            {179.51f, 23.34f, -37.71f},
            {179.51f, 23.31f, -33.81f},
            {179.51f, 23.27f, -29.91f},
            {179.51f, 23.24f, -26.01f},
            {179.51f, 23.21f, -22.12f},
            {179.51f, 23.17f, -18.22f},
            {179.51f, 23.14f, -14.32f},
            {179.51f, 23.10f, -10.42f},
            {179.51f, 23.07f, -6.52f},
            {179.51f, 23.04f, -2.62f},
            {179.51f, 23.00f, 1.27f},
            {179.51f, 22.97f, 5.17f},
            {179.51f, 22.94f, 9.07f},
            {179.51f, 22.90f, 12.97f},
            {179.51f, 22.87f, 16.87f},
            {179.51f, 22.84f, 20.77f},
            {179.51f, 22.80f, 24.66f},
            {179.51f, 22.77f, 28.56f},
            {179.51f, 22.74f, 32.46f},
            {179.51f, 22.70f, 36.36f},
            {179.51f, 22.67f, 40.26f},
            {179.51f, 22.63f, 44.16f},
            {179.51f, 22.60f, 48.05f},
            {179.51f, 22.57f, 51.95f},
            {179.51f, 22.53f, 55.85f},
            {176.79f, 22.50f, 59.75f}
    };

    private static final float START_Y = 22.50f;
    private static final float START_Z = 59.75f;

    public leopardModel() {}

    @Override
    public ResourceLocation getModelResource(leopardEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(leopardEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(leopardEntity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<leopardEntity> collectTransform(String boneName) {
        if (boneName.equals("BarrelOccilator")) {
            return (bone, vehicle, state) -> {
                bone.setPosZ(0.0F);
                float activeTimer = vehicle.getShootAnimationTimer(1, 0);

                if (activeTimer > 0) {
                    float maxAnimationTicks = 4.0F;
                    float progress = activeTimer / maxAnimationTicks;
                    float recoilDistance = (float) Math.sin(progress * Math.PI) * 0.55F;
                    bone.setPosZ(bone.getPosZ() - recoilDistance);
                }
            };
        }
        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float) wrapRange * 50.0F;
        int idx1 = Mth.clamp((int) normalized, 0, 50);
        int idx2 = Mth.clamp(idx1 + 1, 0, 50);
        float frac = normalized - (float) ((int) normalized);

        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];

        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0F) {
                p2 -= 360.0F;
            } else if (diff < -180.0F) {
                p2 += 360.0F;
            }
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

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
