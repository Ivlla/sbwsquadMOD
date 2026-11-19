package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.squadmc.squadmcor.entity.marder1a3Entity;

public class marder1a3Model extends VehicleModel<marder1a3Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/marder1a3.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/marder1a3_1.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");

    private static final float TRACK_Z_OFFSET = 0.0F;
    private static final float TRACK_Y_OFFSET = 0.0F;

    private static final int TRACK_COUNT = 68;
    private static final float ANIMATION_LENGTH = 100.0F;
    private static final boolean REVERSED = true; // как MarderTrackPaths.MARDER.reversed()

    private static final float[][] MARDER_KEYFRAMES = {
            {-3.89f, 15.54f, 39.85f},
            {1.86f, 15.44f, 36.87f},
            {1.68f, 15.34f, 33.88f},
            {1.49f, 15.26f, 30.90f},
            {1.30f, 15.19f, 27.92f},
            {1.11f, 15.13f, 24.94f},
            {0.92f, 15.08f, 21.95f},
            {0.73f, 15.03f, 18.97f},
            {0.54f, 15.00f, 15.99f},
            {0.35f, 14.98f, 13.00f},
            {0.16f, 14.96f, 10.02f},
            {-0.03f, 14.96f, 7.04f},
            {-0.22f, 14.97f, 4.06f},
            {-0.41f, 14.98f, 1.07f},
            {-0.60f, 15.01f, -1.91f},
            {-0.79f, 15.05f, -4.89f},
            {-0.98f, 15.09f, -7.88f},
            {-1.17f, 15.15f, -10.86f},
            {-1.36f, 15.21f, -13.84f},
            {-1.55f, 15.29f, -16.82f},
            {-1.74f, 15.37f, -19.81f},
            {-1.93f, 15.47f, -22.79f},
            {-2.12f, 15.58f, -25.77f},
            {-2.31f, 15.69f, -28.75f},
            {-2.50f, 15.82f, -31.73f},
            {-2.68f, 15.95f, -34.71f},
            {-2.87f, 16.10f, -37.69f},
            {-3.06f, 16.25f, -40.67f},
            {-3.09f, 16.41f, -43.65f},
            {17.49f, 16.26f, -46.61f},
            {51.68f, 14.59f, -49.02f},
            {85.90f, 11.85f, -50.08f},
            {120.12f, 8.99f, -49.42f},
            {151.48f, 6.99f, -47.27f},
            {157.60f, 5.84f, -44.52f},
            {157.60f, 4.71f, -41.76f},
            {157.60f, 3.57f, -39.00f},
            {157.60f, 2.43f, -36.24f},
            {159.47f, 1.30f, -33.48f},
            {177.68f, 0.70f, -30.59f},
            {180.00f, 0.70f, -27.61f},
            {180.00f, 0.70f, -24.62f},
            {180.00f, 0.70f, -21.64f},
            {180.00f, 0.70f, -18.66f},
            {180.00f, 0.70f, -15.67f},
            {180.00f, 0.70f, -12.69f},
            {180.00f, 0.70f, -9.71f},
            {180.00f, 0.70f, -6.72f},
            {180.00f, 0.70f, -3.74f},
            {180.00f, 0.70f, -0.76f},
            {180.00f, 0.70f, 2.23f},
            {180.00f, 0.70f, 5.21f},
            {180.00f, 0.70f, 8.19f},
            {180.00f, 0.70f, 11.18f},
            {180.00f, 0.70f, 14.16f},
            {180.00f, 0.70f, 17.14f},
            {180.00f, 0.70f, 20.12f},
            {180.00f, 0.70f, 23.11f},
            {180.00f, 0.70f, 26.09f},
            {182.98f, 0.70f, 29.07f},
            {204.33f, 1.40f, 31.94f},
            {207.38f, 2.77f, 34.59f},
            {207.38f, 4.14f, 37.24f},
            {207.38f, 5.51f, 39.89f},
            {216.59f, 6.92f, 42.51f},
            {251.14f, 9.27f, 44.28f},
            {287.14f, 12.20f, 44.32f},
            {323.34f, 14.60f, 42.63f},
            {356.11f, 15.54f, 39.85f}
    };

    private static final int MAX_IDX = MARDER_KEYFRAMES.length - 1;
    private static final float START_Y = MARDER_KEYFRAMES[0][1]; // 15.54F
    private static final float START_Z = MARDER_KEYFRAMES[0][2]; // 39.85F

    public marder1a3Model() {}

    @Override
    public ResourceLocation getModelResource(marder1a3Entity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(marder1a3Entity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(marder1a3Entity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<marder1a3Entity> collectTransform(String boneName) {
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

        if (boneName.equals("base") || boneName.equals("TrackL") || boneName.equals("TrackR")) {
            VehicleModel.TransformContext<marder1a3Entity> baseTransform = super.collectTransform(boneName);
            return (bone, vehicle, state) -> {
                LocalPlayer player = Minecraft.getInstance().player;
                bone.setHidden(player != null && vehicle == player.getVehicle() && vehicle.getControllingPassenger() != player && (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON || ClientEventHandler.zoomVehicle));
                if (baseTransform != null) {
                    baseTransform.transform(bone, vehicle, state);
                }
            };
        }

        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        float wrapped = t % ANIMATION_LENGTH;
        if (wrapped < 0.0F) {
            wrapped += ANIMATION_LENGTH;
        }
        if (REVERSED) {
            wrapped = ANIMATION_LENGTH - wrapped;
            if (wrapped >= ANIMATION_LENGTH) {
                wrapped -= ANIMATION_LENGTH;
            }
        }

        float normalized = (wrapped / ANIMATION_LENGTH) * MAX_IDX;
        int idx1 = Mth.clamp((int) normalized, 0, MAX_IDX - 1);
        float frac = normalized - idx1;

        float p1 = MARDER_KEYFRAMES[idx1][component];
        float p2 = MARDER_KEYFRAMES[idx1 + 1][component];

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

    @Override
    public float getTrackDistance() {
        return ANIMATION_LENGTH / TRACK_COUNT;
    }

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}