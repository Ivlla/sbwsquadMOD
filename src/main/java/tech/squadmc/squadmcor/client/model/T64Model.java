package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.squadmc.squadmcor.entity.T64Entity;
import tech.squadmc.squadmcor.squadmc;

public class T64Model extends VehicleModel<T64Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation(squadmc.MODID, "geo/t64.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation(squadmc.MODID, "textures/entity/t64.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation(squadmc.MODID, "animations/btr82.animation.json");


    public T64Model() {}

    @Override
    public ResourceLocation getModelResource(T64Entity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(T64Entity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(T64Entity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<T64Entity> collectTransform(String boneName) {
        // Р—Р°РїСЂРµС‰Р°РµРј Superb Warfare Р°РІС‚РѕРјР°С‚РёС‡РµСЃРєРё РІСЂР°С‰Р°С‚СЊ РѕР±С‰РёРµ РіСЂСѓРїРїС‹ РєРѕР»РµСЃ Рё РіСѓСЃРµРЅРёС†
        if ("wheel".equals(boneName) || "wheelL".equals(boneName) || "wheelR".equals(boneName) ||
                "gus".equals(boneName) || "gus2".equals(boneName) || "tracks".equals(boneName)) {
            return (bone, vehicle, state) -> {
                // РћСЃС‚Р°РІР»СЏРµРј РѕСЂРёРµРЅС‚Р°С†РёСЋ СЃС‚Р°С‚РёС‡РЅРѕР№
                bone.setRotX(0.0F);
                bone.setRotY(0.0F);
                bone.setRotZ(0.0F);
            };
        }

        // Р’СЂР°С‰Р°РµРј С‚РѕР»СЊРєРѕ РѕС‚РґРµР»СЊРЅС‹Рµ РєР°С‚РєРё (РЅРѕРјРµСЂР° РѕС‚ 0 РґРѕ 7)
        if (boneName.matches("wheel[LR][0-7]")) {
            return (bone, vehicle, state) -> {
                float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                bone.setRotX((float) Math.toRadians((double) (-wheelRot)));
            };
        }

        return super.collectTransform(boneName);
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
