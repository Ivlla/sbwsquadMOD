package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.bmd4Entity;
import org.jetbrains.annotations.Nullable;

public class bmd4Model extends VehicleModel<bmd4Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/bmd_4.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/bmd4.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    private static final float TRACK_Z_OFFSET = 0.0F;
    private static final float TRACK_Y_OFFSET = 0.0F;

    private static final int TRACK_COUNT = 75;
    private static final int MAX_IDX = 75;
    private static final float[][] KEYFRAMES = new float[][]{{176.13F, 19.5F, 59.0F}, {152.55F, 18.77F, 61.87F}, {122.6F, 16.75F, 64.03F}, {92.65F, 13.95F, 64.98F}, {67.69F, 11.04F, 64.47F}, {37.74F, 8.72F, 62.62F}, {30.26F, 7.18F, 60.07F}, {30.26F, 5.67F, 57.48F}, {30.26F, 4.16F, 54.9F}, {30.26F, 2.65F, 52.32F}, {27.56F, 1.16F, 49.73F}, {0.54F, 0.5F, 46.84F}, {0.0F, 0.5F, 43.85F}, {0.0F, 0.5F, 40.86F}, {0.0F, 0.5F, 37.87F}, {0.0F, 0.5F, 34.88F}, {0.0F, 0.5F, 31.89F}, {0.0F, 0.5F, 28.9F}, {0.0F, 0.5F, 25.91F}, {0.0F, 0.5F, 22.92F}, {0.0F, 0.5F, 19.93F}, {0.0F, 0.5F, 16.94F}, {0.0F, 0.5F, 13.95F}, {0.0F, 0.5F, 10.96F}, {0.0F, 0.5F, 7.96F}, {0.0F, 0.5F, 4.97F}, {0.0F, 0.5F, 1.98F}, {0.0F, 0.5F, -1.01F}, {0.0F, 0.5F, -4.0F}, {0.0F, 0.5F, -6.99F}, {0.0F, 0.5F, -9.98F}, {0.0F, 0.5F, -12.97F}, {0.0F, 0.5F, -15.96F}, {-19.17F, 0.82F, -18.91F}, {-31.64F, 2.27F, -21.52F}, {-31.64F, 3.84F, -24.06F}, {-31.64F, 5.41F, -26.61F}, {-31.64F, 6.98F, -29.15F}, {-31.64F, 8.55F, -31.7F}, {-31.64F, 10.12F, -34.25F}, {-43.0F, 11.72F, -36.77F}, {-72.89F, 14.22F, -38.33F}, {-107.76F, 17.16F, -38.21F}, {-142.64F, 19.52F, -36.46F}, {-177.97F, 20.48F, -33.68F}, {-177.97F, 20.5F, -30.69F}, {-179.92F, 20.5F, -27.7F}, {-180.0F, 20.5F, -24.71F}, {-180.0F, 20.5F, -21.72F}, {-180.0F, 20.5F, -18.73F}, {-180.0F, 20.5F, -15.74F}, {-180.0F, 20.5F, -12.75F}, {-180.0F, 20.5F, -9.75F}, {-180.0F, 20.5F, -6.76F}, {-180.0F, 20.5F, -3.77F}, {-180.0F, 20.5F, -0.78F}, {-180.0F, 20.5F, 2.21F}, {-180.0F, 20.5F, 5.2F}, {-180.0F, 20.5F, 8.19F}, {-180.0F, 20.5F, 11.18F}, {-180.0F, 20.5F, 14.17F}, {-180.0F, 20.5F, 17.16F}, {-180.0F, 20.5F, 20.15F}, {-180.0F, 20.5F, 23.14F}, {-180.0F, 20.5F, 26.13F}, {-180.0F, 20.5F, 29.12F}, {-180.0F, 20.5F, 32.11F}, {-180.0F, 20.5F, 35.1F}, {179.25F, 20.5F, 38.09F}, {177.74F, 20.44F, 41.08F}, {176.99F, 20.29F, 44.07F}, {176.99F, 20.13F, 47.05F}, {176.99F, 19.97F, 50.04F}, {176.99F, 19.81F, 53.03F}, {176.99F, 19.66F, 56.01F}, {176.13F, 19.5F, 59.0F}};
    private static final float START_Y = 19.5F;
    private static final float START_Z = 59.0F;

    public bmd4Model() {}

    @Override
    public ResourceLocation getModelResource(bmd4Entity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(bmd4Entity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(bmd4Entity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<bmd4Entity> collectTransform(String boneName) {
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
