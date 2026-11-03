package tech.squadmc.squadmcor.client.launcher;

import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.item.launcher.FclLauncherItem;
import tech.squadmc.squadmcor.squadmc;

/** Optical sights: the pack's reticles shown through a round eyepiece while fully aimed. */
@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public final class FclScopeClient {

    private static final ResourceLocation MASK = new ResourceLocation(squadmc.MODID, "textures/gui/fcl/scope_mask.png");
    private static final double SCOPE_IN = 0.85;

    private FclScopeClient() {
    }

    private static String activeReticle(Minecraft mc) {
        if (mc.player == null || mc.options.getCameraType() != CameraType.FIRST_PERSON) return null;
        var stack = mc.player.getMainHandItem();
        if (!(stack.getItem() instanceof FclLauncherItem item)) return null;
        if (ClientEventHandler.zoomTime < SCOPE_IN) return null;
        return item.spec.reticle();
    }

    @SubscribeEvent
    public static void hideLauncherInScope(RenderHandEvent event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && activeReticle(Minecraft.getInstance()) != null) {
            event.setCanceled(true);
        }
    }

    private static void draw(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        String reticle = activeReticle(mc);
        if (reticle == null || mc.options.hideGui) return;

        int size = height;
        int x = (width - size) / 2;
        if (x > 0) {
            graphics.fill(0, 0, x, height, 0xFF000000);
            graphics.fill(x + size, 0, width, height, 0xFF000000);
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        graphics.blit(MASK, x, 0, 0, 0, size, size, size, size);
        graphics.blit(new ResourceLocation(squadmc.MODID, "textures/gui/fcl/" + reticle + ".png"), x, 0, 0, 0, size, size, size, size);
        RenderSystem.disableBlend();
    }

    @Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        @SubscribeEvent
        public static void overlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("fcl_scope", FclScopeClient::draw);
        }
    }
}
