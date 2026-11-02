package tech.squadmc.squadmcor.client.model.empl;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.squadmc.squadmcor.entity.empl.Zu23Entity;

/** ЗУ в модели развёрнута на 180 градусов внутри кости turret, поэтому знак наводки обратный. */
public class Zu23Model extends VehicleModel<Zu23Entity> {
    private static final ResourceLocation GEO = new ResourceLocation("squadmc", "geo/zu23.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("squadmc", "textures/entity/zu23.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("squadmc", "animations/btr82.animation.json");

    @Override
    public ResourceLocation getModelResource(Zu23Entity animatable) {
        return GEO;
    }

    @Override
    public ResourceLocation getTextureResource(Zu23Entity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Zu23Entity animatable) {
        return ANIMATION;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<Zu23Entity> collectTransform(String boneName) {
        if (boneName.equals("turret")) {
            return (bone, vehicle, state) -> bone.setRotY(Mth.PI
                    + Mth.lerp(state.getPartialTick(), vehicle.getTurretYRotO(), vehicle.getTurretYRot()) * Mth.DEG_TO_RAD);
        }
        if (boneName.equals("barrel")) {
            return (bone, vehicle, state) -> bone.setRotX(-Mth.clamp(
                    -Mth.lerp(state.getPartialTick(), vehicle.getTurretXRotO(), vehicle.getTurretXRot()),
                    vehicle.getTurretMinPitch(), vehicle.getTurretMaxPitch()) * Mth.DEG_TO_RAD);
        }
        return super.collectTransform(boneName);
    }
}
