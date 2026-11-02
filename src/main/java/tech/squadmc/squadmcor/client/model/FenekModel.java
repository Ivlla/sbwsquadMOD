package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import tech.squadmc.squadmcor.entity.FenekEntity;
import tech.squadmc.squadmcor.squadmc;

public class FenekModel extends VehicleModel<FenekEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation(squadmc.MODID, "geo/fenek.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation(squadmc.MODID, "textures/entity/fennek.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation(squadmc.MODID, "animations/btr82.animation.json");


    @Override
    public ResourceLocation getModelResource(FenekEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(FenekEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(FenekEntity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<FenekEntity> collectTransform(String boneName) {
        // РџРѕРІРѕСЂРѕС‚ РјР°С‡С‚С‹ РєРѕРјР°РЅРґРёСЂР° (РіРѕСЂРёР·РѕРЅС‚Р°Р»СЊРЅС‹Р№) Р·Р° РІР·РіР»СЏРґРѕРј РєРѕРјР°РЅРґРёСЂР° (РјРµСЃС‚Рѕ 3 / РёРЅРґРµРєСЃ 2)
        if ("passengerWeaponStation".equals(boneName) || "passengerWeaponStationYaw".equals(boneName)) {
            return (bone, vehicle, state) -> {
                for (Entity passenger : vehicle.getPassengers()) {
                    if (vehicle.getSeatIndex(passenger) == 2) {
                        float pt = state.getPartialTick();
                        float passengerYaw = Mth.lerp(pt, passenger.yRotO, passenger.getYRot());
                        float vehicleYaw = Mth.lerp(pt, vehicle.yRotO, vehicle.getYRot());
                        float diffYaw = passengerYaw - vehicleYaw;
                        bone.setRotY((float) Math.toRadians(-diffYaw));
                        break;
                    }
                }
            };
        }

        // РќР°РєР»РѕРЅ РіРѕР»РѕРІРєРё РјР°С‡С‚С‹ РєРѕРјР°РЅРґРёСЂР° (РІРµСЂС‚РёРєР°Р»СЊРЅС‹Р№)
        if ("passengerWeaponStationBarrel".equals(boneName) || "WeaponStationBarrel".equals(boneName)) {
            return (bone, vehicle, state) -> {
                for (Entity passenger : vehicle.getPassengers()) {
                    if (vehicle.getSeatIndex(passenger) == 2) {
                        float pt = state.getPartialTick();
                        float pitch = Mth.lerp(pt, passenger.xRotO, passenger.getXRot());
                        bone.setRotX((float) Math.toRadians(-pitch));
                        break;
                    }
                }
            };
        }

        switch (boneName) {
            case "wheelL0Turn":
            case "wheelR0Turn":
                return (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float) Math.toRadians((double) (-wheelRot)));
                    float steeringAngle = Mth.lerp(state.getPartialTick(), vehicle.getPrevSteeringAngle(), vehicle.getSteeringAngle());
                    steeringAngle = Mth.clamp(steeringAngle, -45.0F, 45.0F);
                    bone.setRotY((float) Math.toRadians((double) steeringAngle));
                };

            case "wheelL0":
            case "wheelR0":
                return (bone, vehicle, state) -> {
                    float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float) Math.toRadians((double) (-wheelRot)));
                };
            default:
                return super.collectTransform(boneName);
        }
    }
}
