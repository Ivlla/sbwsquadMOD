package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.entity.MotorboatEntity;

public class MotorboatModel extends VehicleModel<MotorboatEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/rubber_boat.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/boat.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    @Override
    public ResourceLocation getModelResource(MotorboatEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(MotorboatEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(MotorboatEntity animatable) {
        return CACHED_RESOURCE_2; // Р‘Р°Р·РѕРІС‹Р№ idle
    }
}
