package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import tech.squadmc.squadmcor.entity.T72AVEntity;

import java.lang.reflect.Field;

public class T72AVModel extends VehicleModel<T72AVEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "geo/t72av.geo.json");
    private static final ResourceLocation CACHED_RESOURCE_1 = new ResourceLocation("squadmc", "textures/entity/t72av.png");
    private static final ResourceLocation CACHED_RESOURCE_2 = new ResourceLocation("squadmc", "animations/btr82.animation.json");



    private static Field cachedYawField = null;
    private static Field cachedPitchField = null;
    private static boolean fieldsInitialized = false;

    public T72AVModel() {}

    @Override
    public ResourceLocation getModelResource(T72AVEntity animatable) {
        return CACHED_RESOURCE_0;
    }

    @Override
    public ResourceLocation getTextureResource(T72AVEntity animatable) {
        return CACHED_RESOURCE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(T72AVEntity animatable) {
        return CACHED_RESOURCE_2;
    }



    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
