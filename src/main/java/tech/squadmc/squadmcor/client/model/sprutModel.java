package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.sprutEntity;
import org.jetbrains.annotations.Nullable;

public class sprutModel extends VehicleModel<sprutEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/sprut.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/sprut.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    private static final float TRACK_Z_OFFSET = 0.0F;
    private static final float TRACK_Y_OFFSET = 0.0F;

    private static final int TRACK_COUNT = 89;
    private static final int MAX_IDX = 89;
    private static final float[][] KEYFRAMES = new float[][]{{176.17F, 19.5F, 66.0F}, {152.55F, 18.76F, 68.87F}, {122.6F, 16.75F, 71.04F}, {92.65F, 13.94F, 71.98F}, {67.69F, 11.02F, 71.46F}, {37.74F, 8.71F, 69.61F}, {30.26F, 7.17F, 67.05F}, {30.26F, 5.66F, 64.46F}, {30.26F, 4.15F, 61.88F}, {30.26F, 2.64F, 59.29F}, {27.56F, 1.14F, 56.7F}, {0.54F, 0.5F, 53.81F}, {0.0F, 0.5F, 50.81F}, {0.0F, 0.5F, 47.82F}, {0.0F, 0.5F, 44.82F}, {0.0F, 0.5F, 41.83F}, {0.0F, 0.5F, 38.84F}, {0.0F, 0.5F, 35.84F}, {0.0F, 0.5F, 32.85F}, {0.0F, 0.5F, 29.86F}, {0.0F, 0.5F, 26.86F}, {0.0F, 0.5F, 23.87F}, {0.0F, 0.5F, 20.87F}, {0.0F, 0.5F, 17.88F}, {0.0F, 0.5F, 14.89F}, {0.0F, 0.5F, 11.89F}, {0.0F, 0.5F, 8.9F}, {0.0F, 0.5F, 5.91F}, {0.0F, 0.5F, 2.91F}, {0.0F, 0.5F, -0.08F}, {0.0F, 0.5F, -3.08F}, {0.0F, 0.5F, -6.07F}, {0.0F, 0.5F, -9.06F}, {0.0F, 0.5F, -12.06F}, {0.0F, 0.5F, -15.05F}, {0.0F, 0.5F, -18.04F}, {0.0F, 0.5F, -21.04F}, {0.0F, 0.5F, -24.03F}, {0.0F, 0.5F, -27.03F}, {0.0F, 0.5F, -30.02F}, {-11.23F, 0.59F, -33.01F}, {-33.32F, 1.8F, -35.71F}, {-33.32F, 3.45F, -38.21F}, {-33.32F, 5.09F, -40.71F}, {-33.32F, 6.74F, -43.21F}, {-33.32F, 8.38F, -45.72F}, {-33.32F, 10.02F, -48.22F}, {-39.29F, 11.67F, -50.71F}, {-73.85F, 14.15F, -52.31F}, {-108.41F, 17.1F, -52.23F}, {-142.97F, 19.49F, -50.51F}, {-177.98F, 20.48F, -47.74F}, {-179.92F, 20.5F, -44.74F}, {-180.0F, 20.5F, -41.75F}, {-180.0F, 20.5F, -38.76F}, {-180.0F, 20.5F, -35.76F}, {-180.0F, 20.5F, -32.77F}, {-180.0F, 20.5F, -29.77F}, {-180.0F, 20.5F, -26.78F}, {-180.0F, 20.5F, -23.79F}, {-180.0F, 20.5F, -20.79F}, {-180.0F, 20.5F, -17.8F}, {-180.0F, 20.5F, -14.81F}, {-180.0F, 20.5F, -11.81F}, {-180.0F, 20.5F, -8.82F}, {-180.0F, 20.5F, -5.82F}, {-180.0F, 20.5F, -2.83F}, {-180.0F, 20.5F, 0.16F}, {-180.0F, 20.5F, 3.16F}, {-180.0F, 20.5F, 6.15F}, {-180.0F, 20.5F, 9.14F}, {-180.0F, 20.5F, 12.14F}, {-180.0F, 20.5F, 15.13F}, {-180.0F, 20.5F, 18.13F}, {-180.0F, 20.5F, 21.12F}, {-180.0F, 20.5F, 24.11F}, {-180.0F, 20.5F, 27.11F}, {-180.0F, 20.5F, 30.1F}, {-180.0F, 20.5F, 33.09F}, {-180.0F, 20.5F, 36.09F}, {-180.0F, 20.5F, 39.08F}, {-180.0F, 20.5F, 42.08F}, {179.28F, 20.5F, 45.07F}, {177.85F, 20.4F, 48.06F}, {177.14F, 20.25F, 51.05F}, {177.14F, 20.1F, 54.04F}, {177.14F, 19.95F, 57.03F}, {177.14F, 19.8F, 60.02F}, {177.14F, 19.65F, 63.01F}, {176.17F, 19.5F, 66.0F}};
    private static final float START_Y = 19.5F;
    private static final float START_Z = 66.0F;
    public sprutModel() {}

    @Override
    public ResourceLocation getModelResource(sprutEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(sprutEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(sprutEntity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<sprutEntity> collectTransform(String boneName) {
        if (boneName.equals("BarrelOccilator")) {
            return (bone, vehicle, state) -> {
                bone.setPosZ(0.0F);
                float activeTimer = vehicle.getShootAnimationTimer(1, 0);

                if (activeTimer > 0) {
                    float maxAnimationTicks = 3.0F;
                    float progress = activeTimer / maxAnimationTicks;
                    float recoilDistance = (float) Math.sin(progress * Math.PI) * 0.65F; // Р—Р°РјРµС‚РЅС‹Р№ С…РѕРґ СЃС‚РІРѕР»Р° РїСЂРё РІС‹СЃС‚СЂРµР»Рµ
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
