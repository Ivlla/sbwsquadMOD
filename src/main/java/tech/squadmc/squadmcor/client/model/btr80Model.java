package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.btr80Entity;
import org.jetbrains.annotations.Nullable;

public class btr80Model extends VehicleModel<btr80Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/btr80.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/btr_82.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    @Override
    public ResourceLocation getModelResource(btr80Entity animatable) {
        // РџСѓС‚СЊ Рє РІР°С€РµР№ РјРѕРґРµР»Рё Р‘РўР -80
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(btr80Entity animatable) {
        // РџСѓС‚СЊ Рє С‚РµРєСЃС‚СѓСЂРµ Р‘РўР -80
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(btr80Entity animatable) {
        // РџРµСЂРµРёСЃРїРѕР»СЊР·СѓРµРј С„Р°Р№Р» Р°РЅРёРјР°С†РёР№ РѕС‚ Р‘РўР -82, РµСЃР»Рё РєРѕСЃС‚Рё РєРѕР»РµСЃ Рё Р±Р°С€РЅРё РЅР°Р·С‹РІР°СЋС‚СЃСЏ С‚Р°Рє Р¶Рµ
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<btr80Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext<btr80Entity> var10000;
        switch (boneName) {
            case "WheelTurnL1":
            case "WheelTurnL2":
            case "WheelTurnR1":
            case "WheelTurnR2":
                var10000 = (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float)Math.toRadians((double)(-wheelRot)));
                    float steeringAngle = Mth.lerp(state.getPartialTick(), vehicle.getPrevSteeringAngle(), vehicle.getSteeringAngle());
                    steeringAngle = Mth.clamp(steeringAngle, -30.0F, 30.0F);
                    bone.setRotY((float)Math.toRadians((double)steeringAngle));
                };
                break;
            case "WheelL3":
            case "WheelL4":
            case "WheelR3":
            case "WheelR4":
                var10000 = (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float)Math.toRadians((double)(-wheelRot)));
                };
                break;
            default:
                var10000 = super.collectTransform(boneName);
        }

        return var10000;
    }
}
