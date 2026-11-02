package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.entity.Bmp1Entity;

public class Bmp1Model extends VehicleModel<Bmp1Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/bmp1.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/bmp1.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");


    public Bmp1Model() {}

    @Override
    public ResourceLocation getModelResource(Bmp1Entity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(Bmp1Entity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(Bmp1Entity animatable) {
        return CACHED_RESOURCE_2;
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
