package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.UralGradEntity;
import org.jetbrains.annotations.Nullable;

public class UralGradModel extends VehicleModel<UralGradEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/ural_grad.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/ural.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    @Override
    public ResourceLocation getModelResource(UralGradEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(UralGradEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(UralGradEntity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<UralGradEntity> collectTransform(String boneName) {
        // Р­С„С„РµРєС‚ РѕС‚РєР°С‚Р° РїСѓСЃРєРѕРІРѕР№ СѓСЃС‚Р°РЅРѕРІРєРё РїСЂРё РІС‹СЃС‚СЂРµР»Рµ (РµСЃР»Рё РІ geo РЅР°СЃС‚СЂРѕРµРЅР° РєРѕСЃС‚СЊ BarrelOccilator)
        if (boneName.equals("BarrelOccilator")) {
            return (bone, vehicle, state) -> {
                bone.setPosZ(0.0F);
                float activeTimer = vehicle.getShootAnimationTimer(1, 0);

                if (activeTimer > 0) {
                    float maxAnimationTicks = 6.0F;
                    float progress = Mth.clamp(activeTimer / maxAnimationTicks, 0.0F, 1.0F);
                    float recoilDistance = progress * progress * 0.8F;
                    bone.setPosZ(bone.getPosZ() + recoilDistance);
                }
            };
        }

        switch (boneName) {
            // РџРѕРІРѕСЂРѕС‚РЅС‹Р№ РєСѓР»Р°Рє + РІСЂР°С‰РµРЅРёРµ РїРµСЂРµРґРЅРёС… СѓРїСЂР°РІР»СЏРµРјС‹С… РєРѕР»РµСЃ (РћСЃСЊ 1) - РёР·РјРµРЅРµРЅРѕ РЅР° Р·Р°РіР»Р°РІРЅС‹Рµ Р±СѓРєРІС‹
            case "WheelL0Turn":
            case "WheelR0Turn":
                return (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float) Math.toRadians((double) (-wheelRot)));

                    float steeringAngle = Mth.lerp(state.getPartialTick(), vehicle.getPrevSteeringAngle(), vehicle.getSteeringAngle());
                    steeringAngle = Mth.clamp(steeringAngle, -45.0F, 45.0F);
                    bone.setRotY((float) Math.toRadians((double) steeringAngle));
                };

            // Р’СЂР°С‰РµРЅРёРµ Р·Р°РґРЅРёС… РІРµРґСѓС‰РёС… РєРѕР»РµСЃ (РћСЃРё 2 Рё 3) - РґРѕР±Р°РІР»РµРЅС‹ WheelL0 Рё WheelR0, РёР·РјРµРЅРµРЅ СЂРµРіРёСЃС‚СЂ
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
