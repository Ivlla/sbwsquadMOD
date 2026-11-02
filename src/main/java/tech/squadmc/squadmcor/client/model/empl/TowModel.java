package tech.squadmc.squadmcor.client.model.empl;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.performance.CachedFloatField;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.empl.TowEntity;
import org.jetbrains.annotations.Nullable;

public class TowModel extends VehicleModel<TowEntity> {
    private static final CachedFloatField YAW_FIELD = new CachedFloatField("turretYRot", "turretYaw");
    private static final CachedFloatField PREV_YAW_FIELD = new CachedFloatField("prevTurretYRot", "turretYRotO", "prevTurretYaw");
    private static final CachedFloatField PITCH_FIELD = new CachedFloatField("turretXRot", "turretPitch");
    private static final CachedFloatField PREV_PITCH_FIELD = new CachedFloatField("prevTurretXRot", "turretXRotO", "prevTurretPitch");

    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/turrets/empl_tow.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/turrets/tow.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    @Override
    public ResourceLocation getModelResource(TowEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(TowEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(TowEntity animatable) {
        return CACHED_RESOURCE_2; // Р‘Р°Р·РѕРІС‹Р№ idle
    }

    @Override
    public VehicleModel.@Nullable TransformContext<TowEntity> collectTransform(String boneName) {
        // РџРѕРІРѕСЂРѕС‚ Р±Р°С€РЅРё (РіРѕСЂРёР·РѕРЅС‚Р°Р»СЊРЅС‹Р№)
        if ("turret".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float pt = state.getPartialTick();
                float yaw = Mth.lerp(pt, getPrevTurretYRot(vehicle), getTurretYRot(vehicle));
                bone.setRotY(yaw * Mth.DEG_TO_RAD);
            };
        }

        // РџРѕРґСЉРµРј СЃС‚РІРѕР»Р° (РІРµСЂС‚РёРєР°Р»СЊРЅС‹Р№) - РїРѕРґРґРµСЂР¶РёРІР°РµС‚ РєРѕСЃС‚Рё barrel Рё barrel2
        if ("barrel".equals(boneName) || "barrel2".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float pt = state.getPartialTick();
                float pitch = Mth.lerp(pt, getPrevTurretXRot(vehicle), getTurretXRot(vehicle));
                bone.setRotX(-pitch * Mth.DEG_TO_RAD);
            };
        }

        return super.collectTransform(boneName);
    }

    // Р РµС„Р»РµРєСЃРёРІРЅРѕРµ РїРѕР»СѓС‡РµРЅРёРµ Р·РЅР°С‡РµРЅРёР№ РїРѕР»РµР№ Р±РµР· РїСЂСЏРјС‹С… РІС‹Р·РѕРІРѕРІ РјРµС‚РѕРґРѕРІ
    private float getTurretYRot(TowEntity vehicle) {
        return YAW_FIELD.get(vehicle);
    }

    private float getPrevTurretYRot(TowEntity vehicle) {
        return PREV_YAW_FIELD.get(vehicle);
    }

    private float getTurretXRot(TowEntity vehicle) {
        return PITCH_FIELD.get(vehicle);
    }

    private float getPrevTurretXRot(TowEntity vehicle) {
        return PREV_PITCH_FIELD.get(vehicle);
    }


}
