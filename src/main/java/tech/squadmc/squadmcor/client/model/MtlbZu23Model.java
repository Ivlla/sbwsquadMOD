package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.squadmc.squadmcor.entity.MtlbZu23Entity;

/** ЗУ в модели развёрнута на 180 градусов внутри кости turret, поэтому знак наводки обратный. */
public class MtlbZu23Model extends VehicleModel<MtlbZu23Entity> {
    private static final java.util.regex.Pattern WHEEL_BONE = java.util.regex.Pattern.compile("wheel\\d*");
    private static final ResourceLocation GEO = new ResourceLocation("squadmc", "geo/mtlb_zu23.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("squadmc", "textures/entity/mtlb_zu23.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("squadmc", "animations/btr82.animation.json");

    @Override
    public ResourceLocation getModelResource(MtlbZu23Entity animatable) {
        return GEO;
    }

    @Override
    public ResourceLocation getTextureResource(MtlbZu23Entity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MtlbZu23Entity animatable) {
        return ANIMATION;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<MtlbZu23Entity> collectTransform(String boneName) {
        if (boneName.equals("turret")) {
            return (bone, vehicle, state) -> bone.setRotY(Mth.PI
                    + Mth.lerp(state.getPartialTick(), vehicle.getTurretYRotO(), vehicle.getTurretYRot()) * Mth.DEG_TO_RAD);
        }
        if (boneName.equals("barrel")) {
            return (bone, vehicle, state) -> bone.setRotX(-Mth.clamp(
                    -Mth.lerp(state.getPartialTick(), vehicle.getTurretXRotO(), vehicle.getTurretXRot()),
                    vehicle.getTurretMinPitch(), vehicle.getTurretMaxPitch()) * Mth.DEG_TO_RAD);
        }
        if (WHEEL_BONE.matcher(boneName).matches()) {
            int number = boneName.equals("wheel") ? 1 : Integer.parseInt(boneName.substring(5));
            boolean left = number <= 8;
            // У опорных катков в модели уже есть поворот на 90 градусов, а ленивцы и звёздочки без него.
            float baked = number % 8 == 6 || number % 8 == 7 ? 0F : -Mth.HALF_PI;
            return (bone, vehicle, state) -> bone.setRotX(baked + (left ? vehicle.getLeftWheelRot() : vehicle.getRightWheelRot()) * 1.5F);
        }

        return super.collectTransform(boneName);
    }
}
