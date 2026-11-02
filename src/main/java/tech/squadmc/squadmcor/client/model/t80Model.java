package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.t80Entity;
import org.jetbrains.annotations.Nullable;

public class t80Model extends VehicleModel<t80Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/t80_u.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/t80.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    private static final float TRACK_Z_OFFSET = 0.0F;
    private static final float TRACK_Y_OFFSET = 0.0F;

    private static final int TRACK_COUNT = 60;
    private static final int MAX_IDX = 60;
    private static final float[][] KEYFRAMES = new float[][]{{176.94F, 19.5F, 47.0F}, {143.28F, 18.21F, 50.72F}, {104.12F, 14.89F, 52.83F}, {64.95F, 10.97F, 52.44F}, {31.97F, 8.12F, 49.71F}, {31.97F, 6.0F, 46.31F}, {31.97F, 3.87F, 42.91F}, {31.67F, 1.75F, 39.5F}, {2.96F, 0.51F, 35.75F}, {0.0F, 0.5F, 31.74F}, {0.0F, 0.5F, 27.72F}, {0.0F, 0.5F, 23.71F}, {0.0F, 0.5F, 19.7F}, {0.0F, 0.5F, 15.69F}, {0.0F, 0.5F, 11.68F}, {0.0F, 0.5F, 7.67F}, {0.0F, 0.5F, 3.66F}, {0.0F, 0.5F, -0.35F}, {0.0F, 0.5F, -4.36F}, {0.0F, 0.5F, -8.37F}, {0.0F, 0.5F, -12.39F}, {0.0F, 0.5F, -16.4F}, {0.0F, 0.5F, -20.41F}, {0.0F, 0.5F, -24.42F}, {0.0F, 0.5F, -28.43F}, {0.0F, 0.5F, -32.44F}, {0.0F, 0.5F, -36.45F}, {-15.89F, 0.78F, -40.44F}, {-34.83F, 2.74F, -43.91F}, {-34.83F, 5.03F, -47.2F}, {-34.83F, 7.32F, -50.49F}, {-34.83F, 9.61F, -53.79F}, {-43.75F, 11.96F, -57.03F}, {-93.29F, 15.58F, -58.5F}, {-137.89F, 19.15F, -56.92F}, {-179.94F, 20.46F, -53.25F}, {177.31F, 20.29F, -49.24F}, {177.14F, 20.09F, -45.24F}, {177.14F, 19.89F, -41.23F}, {177.14F, 19.69F, -37.23F}, {179.28F, 19.5F, -33.22F}, {-180.0F, 19.5F, -29.21F}, {-180.0F, 19.5F, -25.2F}, {-180.0F, 19.5F, -21.19F}, {-180.0F, 19.5F, -17.18F}, {-180.0F, 19.5F, -13.16F}, {-180.0F, 19.5F, -9.15F}, {-180.0F, 19.5F, -5.14F}, {-180.0F, 19.5F, -1.13F}, {-180.0F, 19.5F, 2.88F}, {-180.0F, 19.5F, 6.89F}, {-180.0F, 19.5F, 10.9F}, {-180.0F, 19.5F, 14.91F}, {-180.0F, 19.5F, 18.92F}, {-180.0F, 19.5F, 22.93F}, {-180.0F, 19.5F, 26.95F}, {-180.0F, 19.5F, 30.96F}, {-180.0F, 19.5F, 34.97F}, {-180.0F, 19.5F, 38.98F}, {-180.0F, 19.5F, 42.99F}, {176.94F, 19.5F, 47.0F}};
    private static final float START_Y = 19.5F;
    private static final float START_Z = 47.0F;

    public t80Model() {}

    @Override
    public ResourceLocation getModelResource(t80Entity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(t80Entity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(t80Entity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<t80Entity> collectTransform(String boneName) {
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
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0F;
        int idx1 = Mth.clamp((int)normalized, 0, 50);
        int idx2 = Mth.clamp(idx1 + 1, 0, 50);
        float frac = normalized - (float)((int)normalized);

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
        return this.getKeyframeValue(t, 1) - START_Y + TRACK_Y_OFFSET;
    }

    @Override
    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - START_Z + TRACK_Z_OFFSET;
    }

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
