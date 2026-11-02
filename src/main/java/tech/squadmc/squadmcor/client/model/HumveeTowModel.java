package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.squadmc.squadmcor.entity.HumveeTowEntity;

public class HumveeTowModel extends VehicleModel<HumveeTowEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/humvees/humvee_tow.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/humvees/humvee_tow_green.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    @Override
    public ResourceLocation getModelResource(HumveeTowEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(HumveeTowEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(HumveeTowEntity animatable) {
        return CACHED_RESOURCE_2;
    }


    @Override
    public VehicleModel.@Nullable TransformContext<HumveeTowEntity> collectTransform(String boneName) {
        if (boneName.equals("BarrelOccilator")) {
            return (bone, vehicle, state) -> {
                bone.setPosZ(0.0F);
                float activeTimer = vehicle.getShootAnimationTimer(1, 0);

                if (activeTimer > 0) {
                    float maxAnimationTicks = 6.0F;
                    float progress = Mth.clamp(activeTimer / maxAnimationTicks, 0.0F, 1.0F);
                    float recoilDistance = progress * progress * 1.4F;
                    bone.setPosZ(bone.getPosZ() + recoilDistance);
                }
            };
        }

        switch (boneName) {
            case "turret": // РљРѕСЃС‚СЊ С‚СѓСЂРµР»Рё Рё РїСѓСЃРєРѕРІРѕР№ СѓСЃС‚Р°РЅРѕРІРєРё TOW
                return (bone, vehicle, state) -> {
                    var superTransform = super.collectTransform(boneName);
                    if (superTransform != null) {
                        superTransform.transform(bone, vehicle, state);
                    }

                    Minecraft mc = Minecraft.getInstance();
                    // РЎРєСЂС‹РІР°РµРј С‚СѓСЂРµР»СЊ С‚РѕР»СЊРєРѕ РґР»СЏ Р»РѕРєР°Р»СЊРЅРѕРіРѕ РёРіСЂРѕРєР°, РµСЃР»Рё РѕРЅ СЃРёРґРёС‚ РЅР° РјРµСЃС‚Рµ РЅР°РІРѕРґС‡РёРєР° Рё С†РµР»РёС‚СЃСЏ
                    if (mc.player != null && mc.player.getVehicle() == vehicle) {
                        int seatIndex = vehicle.getSeatIndex(mc.player);
                        boolean isZooming = com.atsuishio.superbwarfare.event.ClientEventHandler.zoomVehicle;

                        if (seatIndex == 1 && isZooming) {
                            bone.setScaleX(0.0F);
                            bone.setScaleY(0.0F);
                            bone.setScaleZ(0.0F);
                        }
                    }
                };

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
