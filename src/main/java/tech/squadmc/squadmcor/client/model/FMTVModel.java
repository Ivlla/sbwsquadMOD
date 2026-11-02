package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.FMTVEntity;
import org.jetbrains.annotations.Nullable;

public class FMTVModel extends VehicleModel<FMTVEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/fmtv.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/fmtv_iraq.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    @Override
    public ResourceLocation getModelResource(FMTVEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(FMTVEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(FMTVEntity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<FMTVEntity> collectTransform(String boneName) {
        switch (boneName) {
            case "WheelL0Turn":
            case "WheelR0Turn":
                return (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float) Math.toRadians((double) (-wheelRot)));

                    float steeringAngle = Mth.lerp(state.getPartialTick(), vehicle.getPrevSteeringAngle(), vehicle.getSteeringAngle());
                    steeringAngle = Mth.clamp(steeringAngle, -45.0F, 45.0F);
                    bone.setRotY((float) Math.toRadians((double) steeringAngle));
                };

            case "WheelL0":
            case "WheelR0":
            case "WheelL1":
            case "WheelR1":
                return (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float) Math.toRadians((double) (-wheelRot)));
                };

            default:
                return super.collectTransform(boneName);
        }
    }
}
