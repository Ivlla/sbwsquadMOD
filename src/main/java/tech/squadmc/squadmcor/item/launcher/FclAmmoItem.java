package tech.squadmc.squadmcor.item.launcher;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.squadmc.squadmcor.client.launcher.FclAmmoRenderer;

import java.util.function.Consumer;

/** Launcher round shown in inventories with its own 3D model. */
public class FclAmmoItem extends Item implements GeoItem {

    public final String id;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public FclAmmoItem(String id) {
        super(new Item.Properties().stacksTo(16));
        this.id = id;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private FclAmmoRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new FclAmmoRenderer(FclAmmoItem.this.id);
                }
                return this.renderer;
            }
        });
    }
}
