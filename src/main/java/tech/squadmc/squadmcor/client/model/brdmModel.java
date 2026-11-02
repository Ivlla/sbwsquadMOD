package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.brdmEntity;
import org.jetbrains.annotations.Nullable;

public class brdmModel extends VehicleModel<brdmEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/brdm2.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/brdm2.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    @Override
    public ResourceLocation getModelResource(brdmEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(brdmEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(brdmEntity animatable) {
        return CACHED_RESOURCE_2;
    }


    @Override
    public VehicleModel.@Nullable TransformContext<brdmEntity> collectTransform(String boneName) {
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
            // РџРѕРІРѕСЂРѕС‚РЅС‹Рµ СѓРїСЂР°РІР»СЏРµРјС‹Рµ РєРѕР»РµСЃР° (РїРµСЂРµРґРЅРёРµ РѕСЃРё)
            case "WheelTurnR3":
            case "WheelTurnR2":
                return (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float)Math.toRadians((double)(-wheelRot)));
                    float steeringAngle = Mth.lerp(state.getPartialTick(), vehicle.getPrevSteeringAngle(), vehicle.getSteeringAngle());
                    steeringAngle = Mth.clamp(steeringAngle, -45.0F, 45.0F);
                    bone.setRotY((float)Math.toRadians((double)steeringAngle));
                };

            // Р’СЂР°С‰Р°СЋС‰РёРµСЃСЏ РєРѕР»РµСЃР°
            case "WheelTurnR4":
            case "WheelTurnR1":
                return (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float)Math.toRadians((double)(-wheelRot)));
                };
            default:
                return super.collectTransform(boneName);
        }
    }
}
