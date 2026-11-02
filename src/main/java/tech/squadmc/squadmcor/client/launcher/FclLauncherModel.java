package tech.squadmc.squadmcor.client.launcher;

import com.atsuishio.superbwarfare.client.animation.AnimationHelper;
import com.atsuishio.superbwarfare.client.model.item.CustomGunModel;
import com.atsuishio.superbwarfare.client.overlay.CrossHairOverlay;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.value.AttachmentType;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import tech.squadmc.squadmcor.item.launcher.FclLauncherItem;
import tech.squadmc.squadmcor.item.launcher.FclLauncherSpec;

public class FclLauncherModel extends CustomGunModel<FclLauncherItem> {

    /** The launchers render at scale 1 while SBW's procedural motion is tuned for its scale 4 guns. */
    private static final float MOTION_SCALE = 4f;

    @Override
    public void setCustomAnimations(FclLauncherItem animatable, long instanceId, AnimationState<FclLauncherItem> animationState) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack stack = player.getMainHandItem();
        if (shouldCancelRender(stack, animationState)) return;

        GunData data = GunData.from(stack);
        CoreGeoBone gun = getAnimationProcessor().getBone("bone");
        CoreGeoBone recoil = getAnimationProcessor().getBone("shen");
        CoreGeoBone root = getAnimationProcessor().getBone("root");
        CoreGeoBone camera = getAnimationProcessor().getBone("camera");
        CoreGeoBone main = getAnimationProcessor().getBone("0");

        double zt = ClientEventHandler.zoomTime;
        float zp = (float) ClientEventHandler.zoomPos;
        float zpz = (float) ClientEventHandler.zoomPosZ;

        ClientEventHandler.handleShootAnimation(recoil, 1, -0.4f, 1.2f, 1.3f, 1, 1, 0.5f, 0.7f);
        recoil.setPosX(recoil.getPosX() * MOTION_SCALE);
        recoil.setPosY(recoil.getPosY() * MOTION_SCALE);
        recoil.setPosZ(recoil.getPosZ() * MOTION_SCALE);
        CrossHairOverlay.gunRot = recoil.getRotZ();

        FclLauncherSpec spec = animatable.spec;
        float[] aim = spec.aim[spec.clampScope(data.attachment.get(AttachmentType.SCOPE))];
        gun.setPosX(aim[0] * zp);
        gun.setPosY(aim[1] * zp - 0.2f * MOTION_SCALE * zpz);
        gun.setPosZ(aim[2] * zp + 0.15f * MOTION_SCALE * zpz);
        gun.setRotZ(-spec.displayRoll * Mth.DEG_TO_RAD * zp);

        ClientEventHandler.gunRootMove(getAnimationProcessor(), 0, 0, 0, true);
        root.setPosX(root.getPosX() * MOTION_SCALE);
        root.setPosY(root.getPosY() * MOTION_SCALE);
        root.setPosZ(root.getPosZ() * MOTION_SCALE);

        float numR = (float) (1 - 0.82 * zt);
        float numP = (float) (1 - 0.78 * zt);
        AnimationHelper.handleReloadShakeAnimation(stack, main, camera, numR, numP);
        ClientEventHandler.handleReloadShake(Mth.RAD_TO_DEG * camera.getRotX(), Mth.RAD_TO_DEG * camera.getRotY(), Mth.RAD_TO_DEG * camera.getRotZ());
    }

    @Override
    public ResourceLocation getLODModelResource(FclLauncherItem animatable) {
        return getModelResource(animatable);
    }

    @Override
    public ResourceLocation getLODTextureResource(FclLauncherItem animatable) {
        return getTextureResource(animatable);
    }
}
