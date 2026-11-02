package tech.squadmc.squadmcor.client.repair;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tech.squadmc.squadmcor.repair.RepairTorchItem;

/** Модель и текстура Repair Tool из SBW. */
public class RepairTorchRenderer extends GeoItemRenderer<RepairTorchItem> {
    public RepairTorchRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(RepairTorchItem item) {
                return new ResourceLocation("superbwarfare", "geo/repair_tool.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(RepairTorchItem item) {
                return new ResourceLocation("superbwarfare", "textures/item/repair_tool.png");
            }

            @Override
            public ResourceLocation getAnimationResource(RepairTorchItem item) {
                return new ResourceLocation("superbwarfare", "animations/repair_tool.animation.json");
            }
        });
    }
}
