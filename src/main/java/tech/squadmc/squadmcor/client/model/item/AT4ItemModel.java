package tech.squadmc.squadmcor.client.model.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.squadmc.squadmcor.item.AT4Item;
import tech.squadmc.squadmcor.squadmc;

public class AT4ItemModel extends GeoModel<AT4Item> {

    @Override
    public ResourceLocation getModelResource(AT4Item animatable) {
        return new ResourceLocation(squadmc.MODID, "geo/item/at4.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AT4Item animatable) {
        return new ResourceLocation(squadmc.MODID, "textures/item/at4.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AT4Item animatable) {
        return new ResourceLocation(squadmc.MODID, "animations/at4.animation.json");
    }
}
