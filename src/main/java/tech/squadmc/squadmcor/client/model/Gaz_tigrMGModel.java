package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.Gaz_tigrMGEntity;
import org.jetbrains.annotations.Nullable;

public class Gaz_tigrMGModel extends VehicleModel<Gaz_tigrMGEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/gaz_tigrs/gaz_tigr_mg.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/gaz_tigrs/gaz_tigr.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    @Override
    public ResourceLocation getModelResource(Gaz_tigrMGEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(Gaz_tigrMGEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(Gaz_tigrMGEntity animatable) {
        return CACHED_RESOURCE_2;
    }


    @Override
    public VehicleModel.@Nullable TransformContext<Gaz_tigrMGEntity> collectTransform(String boneName) {
        if (boneName.equals("BarrelOccilator")) {
            return (bone, vehicle, state) -> {
                bone.setPosZ(0.0F);
                float activeTimer = vehicle.getShootAnimationTimer(1, 0); // 0 - РёРЅРґРµРєСЃ РѕР±СЉРµРґРёРЅРµРЅРЅРѕРіРѕ РѕСЂСѓРґРёСЏ

                if (activeTimer > 0) {
                    float maxAnimationTicks = 6.0F;
                    float progress = Mth.clamp(activeTimer / maxAnimationTicks, 0.0F, 1.0F);
                    float recoilDistance = progress * progress * 1.4F;
                    bone.setPosZ(bone.getPosZ() + recoilDistance);
                }
            };
        }


        switch (boneName) {
            case "wheelL0Turn":
            case "wheelR0Turn":
                return (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float)Math.toRadians((double)(-wheelRot)));
                    float steeringAngle = Mth.lerp(state.getPartialTick(), vehicle.getPrevSteeringAngle(), vehicle.getSteeringAngle());
                    steeringAngle = Mth.clamp(steeringAngle, -45.0F, 45.0F);
                    bone.setRotY((float)Math.toRadians((double)steeringAngle));
                };

            case "wheelL0":
            case "wheelR0":
                return (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float)Math.toRadians((double)(-wheelRot)));
                };
            default:
                return super.collectTransform(boneName);
        }
    }
}
