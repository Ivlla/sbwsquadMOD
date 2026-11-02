package tech.squadmc.squadmcor.item.launcher;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.data.gun.value.AttachmentType;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.item.gun.GunGeoItem;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tech.squadmc.squadmcor.client.launcher.FclLauncherRenderer;
import tech.squadmc.squadmcor.client.launcher.FclSounds;

import java.util.function.Supplier;

public class FclLauncherItem extends GunGeoItem {

    public final FclLauncherSpec spec;

    public FclLauncherItem(FclLauncherSpec spec) {
        super(new Item.Properties().rarity(Rarity.RARE));
        this.spec = spec;
    }

    @Override
    public Supplier<? extends GeoItemRenderer<? extends Item>> getRenderer() {
        return () -> new FclLauncherRenderer(this.spec);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<GunGeoItem> controller = new AnimationController<>(this, "animationController", 1, this::animationPredicate);
        controller.setSoundKeyframeHandler(event -> FclSounds.play(event.getKeyframeData().getSound()));
        controllers.add(controller);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected PlayState animationPredicate(AnimationState<GunGeoItem> event) {
        var player = Minecraft.getInstance().player;
        if (player != null
                && event.getData(DataTickets.ITEM_RENDER_PERSPECTIVE) == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                && player.getMainHandItem().getItem() instanceof GunItem) {
            var data = GunData.from(player.getMainHandItem());
            int ammoType = data.selectedAmmoType.get();
            if (data.reloading()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay(this.spec.reloadAnimation(ammoType, data.reload.normal())));
            }
            if (ClientEventHandler.fireRotTimer > 0) {
                return event.setAndContinue(RawAnimation.begin().thenPlay(this.spec.fireAnimation(ammoType)));
            }
        }
        return super.animationPredicate(event);
    }

    @Override
    public boolean shootBullet(@NotNull ShootParameters parameters) {
        if (!super.shootBullet(parameters)) return false;

        var shooter = parameters.shooter;
        if (shooter != null) {
            Vec3 look = shooter.getLookAngle();
            double y = shooter.getY() + shooter.getBbHeight() - 0.1;
            ParticleTool.sendParticle(parameters.level, ParticleTypes.CLOUD,
                    shooter.getX() + 1.8 * look.x, y + 1.8 * look.y, shooter.getZ() + 1.8 * look.z,
                    24, 0.3, 0.3, 0.3, 0.005, true);
            // Backblast
            ParticleTool.sendParticle(parameters.level, ParticleTypes.LARGE_SMOKE,
                    shooter.getX() - 2.2 * look.x, y - 2.2 * look.y, shooter.getZ() - 2.2 * look.z,
                    30, 0.5, 0.4, 0.5, 0.03, true);
        }
        return true;
    }

    @Override
    public int[] getValidScopes() {
        return this.spec.scopeCount() > 1 ? new int[]{0, 1} : new int[]{0};
    }

    @Override
    public boolean hasCustomScope(GunData data) {
        return this.spec.scopeCount() > 1;
    }

    @Override
    public boolean canEditAttachments(GunData data) {
        return this.spec.scopeCount() > 1;
    }

    @Override
    public double getCustomZoom(GunData data) {
        return this.spec.zoom[this.spec.clampScope(data.attachment.get(AttachmentType.SCOPE))];
    }

    @Override
    public void init(GunData data) {
        if (!isInitialized(data) && this.spec.defaultScope != 0) {
            data.attachment.set(AttachmentType.SCOPE, this.spec.defaultScope);
        }
        super.init(data);
    }
}
