package tech.squadmc.squadmcor.client.launcher;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tech.squadmc.squadmcor.item.launcher.FclAmmoItem;
import tech.squadmc.squadmcor.squadmc;

public class FclAmmoRenderer extends GeoItemRenderer<FclAmmoItem> {

    public FclAmmoRenderer(String id) {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(FclAmmoItem animatable) {
                return new ResourceLocation(squadmc.MODID, "geo/item/" + id + ".geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(FclAmmoItem animatable) {
                return new ResourceLocation(squadmc.MODID, "textures/item/" + id + ".png");
            }

            @Override
            public ResourceLocation getAnimationResource(FclAmmoItem animatable) {
                return new ResourceLocation(squadmc.MODID, "animations/item/fcl_empty.animation.json");
            }
        });
    }
}
