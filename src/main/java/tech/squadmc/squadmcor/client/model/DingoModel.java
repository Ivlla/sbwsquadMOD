package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.DingoEntity;
import tech.squadmc.squadmcor.squadmc;

public class DingoModel<T extends DingoEntity> extends VehicleModel<T> {
    private final String geometry;
    public DingoModel(String geometry) { this.geometry = geometry; }
    @Override public ResourceLocation getModelResource(T entity) { return new ResourceLocation(squadmc.MODID, "geo/" + geometry + ".geo.json"); }
    @Override public ResourceLocation getTextureResource(T entity) { return new ResourceLocation(squadmc.MODID, "textures/entity/dingo_1.png"); }
    @Override public ResourceLocation getAnimationResource(T entity) { return new ResourceLocation(squadmc.MODID, "animations/btr82.animation.json"); }
    @Override public VehicleModel.@org.jetbrains.annotations.Nullable TransformContext<T> collectTransform(String boneName) {
        switch (boneName) {
            case "wheel3": case "wheel4": case "wheel2": case "wheel5":
                return (bone, vehicle, state) -> {
                    float rotation = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                    bone.setRotX((float)Math.toRadians(-rotation));
                    if ("wheel3".equals(boneName) || "wheel2".equals(boneName)) {
                        float steer = Mth.lerp(state.getPartialTick(), vehicle.getPrevSteeringAngle(), vehicle.getSteeringAngle());
                        bone.setRotY((float)Math.toRadians(Mth.clamp(steer, -45F, 45F)));
                    }
                };
            default: return super.collectTransform(boneName);
        }
    }
}
