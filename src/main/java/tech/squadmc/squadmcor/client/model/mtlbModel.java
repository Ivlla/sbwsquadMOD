package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.squadmc.squadmcor.entity.mtlbEntity;

public class mtlbModel extends VehicleModel<mtlbEntity> {
    private static final java.util.regex.Pattern WHEEL_BONE = java.util.regex.Pattern.compile("wheel\\d*");
    private static final ResourceLocation GEO = new ResourceLocation("squadmc", "geo/mtlb.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("squadmc", "textures/entity/mtlb.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("squadmc", "animations/btr82.animation.json");

    @Override
    public ResourceLocation getModelResource(mtlbEntity animatable) {
        return GEO;
    }

    @Override
    public ResourceLocation getTextureResource(mtlbEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(mtlbEntity animatable) {
        return ANIMATION;
    }

    @Override
    public VehicleModel.@Nullable TransformContext<mtlbEntity> collectTransform(String boneName) {
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
