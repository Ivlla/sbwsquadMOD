package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.bmp2Entity;
import org.jetbrains.annotations.Nullable;

public class bmp2Model extends VehicleModel<bmp2Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/bmp2.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/bmp2.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    private static final float TRACK_Z_OFFSET = 0.0F;
    private static final float TRACK_Y_OFFSET = 0.0F;

    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;

    private static final float[][] KEYFRAMES = new float[][]{
            {176.22F, 20.42F, 48.84F}, {128.19F, 18.55F, 52.89F}, {78.85F, 14.27F, 54.11F}, {33.07F, 10.55F, 51.65F},
            {32.61F, 8.08F, 47.78F}, {32.61F, 5.6F, 43.91F}, {32.61F, 3.12F, 40.03F}, {2.85F, 1.37F, 35.86F},
            {0.0F, 1.37F, 31.27F}, {0.0F, 1.37F, 26.67F}, {0.0F, 1.37F, 22.08F}, {0.0F, 1.37F, 17.48F},
            {0.0F, 1.37F, 12.88F}, {0.0F, 1.37F, 8.29F}, {0.0F, 1.37F, 3.69F}, {0.0F, 1.37F, -0.91F},
            {0.0F, 1.37F, -5.5F}, {0.0F, 1.37F, -10.1F}, {0.0F, 1.37F, -14.7F}, {0.0F, 1.37F, -19.29F},
            {0.0F, 1.37F, -23.89F}, {0.0F, 1.37F, -28.48F}, {-21.14F, 1.68F, -33.05F}, {-31.59F, 3.97F, -37.02F},
            {-31.59F, 6.38F, -40.93F}, {-31.59F, 8.79F, -44.85F}, {-31.59F, 11.2F, -48.76F}, {-74.57F, 14.6F, -51.67F},
            {-118.7F, 19.05F, -51.2F}, {-167.74F, 21.74F, -47.64F}, {178.42F, 21.81F, -43.06F}, {178.26F, 21.67F, -38.46F},
            {178.26F, 21.53F, -33.87F}, {178.26F, 21.39F, -29.27F}, {179.56F, 21.26F, -24.68F}, {-180.0F, 21.26F, -20.08F},
            {-180.0F, 21.26F, -15.49F}, {-180.0F, 21.26F, -10.89F}, {-180.0F, 21.26F, -6.29F}, {-180.0F, 21.26F, -1.7F},
            {-180.0F, 21.26F, 2.9F}, {-180.0F, 21.26F, 7.5F}, {-180.0F, 21.26F, 12.09F}, {-180.0F, 21.26F, 16.69F},
            {-180.0F, 21.26F, 21.28F}, {-180.0F, 21.26F, 25.88F}, {179.3F, 21.26F, 30.48F}, {177.21F, 21.09F, 35.07F},
            {177.21F, 20.86F, 39.66F}, {177.21F, 20.64F, 44.25F}, {176.22F, 20.42F, 48.84F}
    };

    private static final float START_Y = 20.42F;
    private static final float START_Z = 48.84F;

    public bmp2Model() {}

    @Override
    public ResourceLocation getModelResource(bmp2Entity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(bmp2Entity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(bmp2Entity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<bmp2Entity> collectTransform(String boneName) {
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
            VehicleModel.TransformContext<bmp2Entity> baseTransform = super.collectTransform(boneName);
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
