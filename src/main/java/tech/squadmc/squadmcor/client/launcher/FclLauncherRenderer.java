package tech.squadmc.squadmcor.client.launcher;

import com.atsuishio.superbwarfare.client.animation.AnimationHelper;
import com.atsuishio.superbwarfare.client.renderer.CustomGunRenderer;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.value.AttachmentType;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import tech.squadmc.squadmcor.item.launcher.FclLauncherItem;
import tech.squadmc.squadmcor.item.launcher.FclLauncherSpec;

public class FclLauncherRenderer extends CustomGunRenderer<FclLauncherItem> {

    // The renderer is built inside Item's constructor, before the item's spec is assigned,
    // so the spec is always read from the item at render time.
    public FclLauncherRenderer() {
        super(new FclLauncherModel());
    }

    @Override
    public void renderRecursively(PoseStack stack, FclLauncherItem animatable, GeoBone bone, RenderType type, MultiBufferSource buffer, VertexConsumer bufferIn,
                                  boolean isReRender, float partialTick, int packedLightIn, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        FclLauncherSpec spec = animatable.spec;
        String name = bone.getName();
        boolean arm = name.equals("Lefthand") || name.equals("Righthand");
        ItemStack partStack = this.currentItemStack != null ? this.currentItemStack : player.getMainHandItem();
        bone.setHidden(arm || isPartHidden(spec, name, partStack));

        ItemStack held = player.getMainHandItem();
        if (held.getItem() instanceof GunItem && GeoItem.getId(held) == this.getInstanceId(animatable)
                && (this.renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || this.renderPerspective == ItemDisplayContext.FIXED)) {
            AnimationHelper.handleShootFlare(name, stack, held, bone, buffer, packedLightIn);
        }

        if (arm) {
            // The pack's arm cubes are larger than the player model's; the bone scale makes up the difference.
            bone.setScaleX(spec.armScale);
            bone.setScaleY(spec.armScale);
            bone.setScaleZ(spec.armScale);
            AnimationHelper.renderArms(player, this.renderPerspective, stack, name, bone, buffer, type, packedLightIn, false);
        }

        super.renderRecursively(stack, animatable, bone, type, buffer, bufferIn, isReRender, partialTick, packedLightIn, packedOverlay, red, green, blue, alpha);
    }

    private static boolean isPartHidden(FclLauncherSpec spec, String name, ItemStack stack) {
        boolean isRocket = name.equals("rocket");
        Integer ammoMask = spec.ammoParts.get(name);
        Integer scope = spec.scopeParts.get(name);
        if (!isRocket && ammoMask == null && scope == null) return false;
        if (!(stack.getItem() instanceof GunItem)) return scope != null && scope != 0;

        GunData data = GunData.from(stack);
        if (isRocket) return data.ammo.get() <= 0 && !data.reloading();
        if (ammoMask != null && (ammoMask & (1 << data.selectedAmmoType.get())) == 0) return true;
        return scope != null && scope != spec.clampScope(data.attachment.get(AttachmentType.SCOPE));
    }
}
