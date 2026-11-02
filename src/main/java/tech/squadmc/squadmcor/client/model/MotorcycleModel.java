package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.MotorcycleEntity;
import org.jetbrains.annotations.Nullable;

public class MotorcycleModel extends VehicleModel<MotorcycleEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/motuo.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/motuo.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    @Override
    public ResourceLocation getModelResource(MotorcycleEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(MotorcycleEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(MotorcycleEntity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<MotorcycleEntity> collectTransform(String boneName) {
        switch (boneName) {
            // РџРµСЂРµРґРЅРµРµ РїРѕРІРѕСЂРѕС‚РЅРѕРµ РєРѕР»РµСЃРѕ РјРѕС‚РѕС†РёРєР»Р° (Р СѓР»СЊ + РљР°С‡РµРЅРёРµ)
            case "wheelL1Turn":
                return (bone, vehicle, state) -> {
                    // РљР°С‡РµРЅРёРµ РєРѕР»РµСЃР° РІРїРµСЂРµРґ/РЅР°Р·Р°Рґ
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float) Math.toRadians((double) (-wheelRot)));

                    // РџРѕРІРѕСЂРѕС‚ СЂСѓР»СЏ РІР»РµРІРѕ/РІРїСЂР°РІРѕ
                    float steeringAngle = Mth.lerp(state.getPartialTick(), vehicle.getPrevSteeringAngle(), vehicle.getSteeringAngle());
                    steeringAngle = Mth.clamp(steeringAngle, -40.0F, 40.0F); // РћРіСЂР°РЅРёС‡РёРІР°РµРј СѓРіРѕР» РїРѕРІРѕСЂРѕС‚Р° СЂСѓР»СЏ
                    bone.setRotY((float) Math.toRadians((double) steeringAngle));
                };

            // Р—Р°РґРЅРµРµ РєРѕР»РµСЃРѕ (wheelL) Рё РєРѕР»РµСЃРѕ РєРѕР»СЏСЃРєРё (wheelR) вЂ” С‚РѕР»СЊРєРѕ РєР°С‡РµРЅРёРµ
            case "wheelL":
            case "wheelR":
                return (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float) Math.toRadians((double) (-wheelRot)));
                };

            default:
                return super.collectTransform(boneName);
        }
    }
}
