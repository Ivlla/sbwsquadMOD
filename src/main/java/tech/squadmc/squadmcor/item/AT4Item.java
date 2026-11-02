package tech.squadmc.squadmcor.item;

import com.atsuishio.superbwarfare.item.gun.GunItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.squadmc.squadmcor.client.renderer.item.AT4ItemRenderer;

import java.util.function.Consumer;

public class AT4Item extends GunItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AT4Item() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // РљРѕРЅС‚СЂРѕР»Р»РµСЂС‹ Р°РЅРёРјР°С†РёР№ GeckoLib (РїСЂРё РЅРµРѕР±С…РѕРґРёРјРѕСЃС‚Рё)
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private AT4ItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new AT4ItemRenderer();
                }
                return this.renderer;
            }
        });
    }
}
