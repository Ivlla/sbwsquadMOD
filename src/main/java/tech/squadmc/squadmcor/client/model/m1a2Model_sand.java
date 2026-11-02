package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.m1a2Entity_sand;
import org.jetbrains.annotations.Nullable;

public class m1a2Model_sand extends VehicleModel<m1a2Entity_sand> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/m1a2_sep.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/m1a2_sand.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    private static final float TRACK_Z_OFFSET = 0.0F;
    private static final float TRACK_Y_OFFSET = 0.0F;
    private static final int TRACK_COUNT = 55;
    private static final int MAX_IDX = 55;

    // РСЃРїРѕР»СЊР·СѓРµРј С‚Рµ Р¶Рµ РєРµР№С„СЂРµР№РјС‹ Р°РЅРёРјР°С†РёРё РіСѓСЃРµРЅРёС†, С‡С‚Рѕ Рё Сѓ Рў-80, Р»РёР±Рѕ Р°РґР°РїС‚РёСЂРѕРІР°РЅРЅС‹Рµ РїРѕРґ РєР°С‚РєРё РђР±СЂР°РјСЃР°
    private static final float[][] KEYFRAMES = new float[][]{{176.94F, 20.5F, 63.5F}, {133.44F, 18.55F, 67.92F}, {84.43F, 13.98F, 69.47F}, {35.42F, 9.75F, 67.16F}, {28.07F, 7.34F, 62.82F}, {28.07F, 5.0F, 58.43F}, {28.07F, 2.66F, 54.05F}, {11.7F, 0.6F, 49.55F}, {0.0F, 0.5F, 44.59F}, {0.0F, 0.5F, 39.62F}, {0.0F, 0.5F, 34.65F}, {0.0F, 0.5F, 29.68F}, {0.0F, 0.5F, 24.71F}, {0.0F, 0.5F, 19.74F}, {0.0F, 0.5F, 14.78F}, {0.0F, 0.5F, 9.81F}, {0.0F, 0.5F, 4.84F}, {0.0F, 0.5F, -0.13F}, {0.0F, 0.5F, -5.1F}, {0.0F, 0.5F, -10.07F}, {0.0F, 0.5F, -15.04F}, {0.0F, 0.5F, -20.01F}, {0.0F, 0.5F, -24.98F}, {0.0F, 0.5F, -29.95F}, {-11.42F, 0.67F, -34.9F}, {-22.83F, 2.52F, -39.51F}, {-22.83F, 4.45F, -44.09F}, {-22.83F, 6.38F, -48.67F}, {-22.83F, 8.31F, -53.25F}, {-54.76F, 10.97F, -57.35F}, {-98.96F, 15.69F, -58.38F}, {-148.08F, 19.63F, -55.6F}, {-179.39F, 20.5F, -50.79F}, {-180.0F, 20.5F, -45.82F}, {-180.0F, 20.5F, -40.85F}, {-180.0F, 20.5F, -35.88F}, {-180.0F, 20.5F, -30.91F}, {-180.0F, 20.5F, -25.94F}, {-180.0F, 20.5F, -20.98F}, {-180.0F, 20.5F, -16.01F}, {-180.0F, 20.5F, -11.04F}, {-180.0F, 20.5F, -6.07F}, {-180.0F, 20.5F, -1.1F}, {-180.0F, 20.5F, 3.87F}, {-180.0F, 20.5F, 8.84F}, {-180.0F, 20.5F, 13.81F}, {-180.0F, 20.5F, 18.78F}, {-180.0F, 20.5F, 23.75F}, {-180.0F, 20.5F, 28.72F}, {-180.0F, 20.5F, 33.69F}, {-180.0F, 20.5F, 38.65F}, {-180.0F, 20.5F, 43.62F}, {-180.0F, 20.5F, 48.59F}, {-180.0F, 20.5F, 53.56F}, {-180.0F, 20.5F, 58.53F}, {176.94F, 20.5F, 63.5F}};
    private static final float START_Y = 20.5F;
    private static final float START_Z = 63.5F;

    public m1a2Model_sand() {}

    @Override
    public ResourceLocation getModelResource(m1a2Entity_sand animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(m1a2Entity_sand animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(m1a2Entity_sand animatable) {
        return CACHED_RESOURCE_2; // РР»Рё РєР°СЃС‚РѕРјРЅР°СЏ Р°РЅРёРјР°С†РёСЏ, РµСЃР»Рё РµСЃС‚СЊ
    }

    @Override
    public VehicleModel.@Nullable TransformContext<m1a2Entity_sand> collectTransform(String boneName) {
        if (boneName.equals("BarrelOccilator")) {
            return (bone, vehicle, state) -> {
                bone.setPosZ(0.0F);
                float activeTimer = vehicle.getShootAnimationTimer(1, 0);

                if (activeTimer > 0) {
                    float maxAnimationTicks = 4.0F; // Р§СѓС‚СЊ Р±РѕР»РµРµ Р·Р°С‚СЏР¶РЅР°СЏ РѕС‚РґР°С‡Р° Сѓ РїСѓС€РєРё РђР±СЂР°РјСЃР°
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
