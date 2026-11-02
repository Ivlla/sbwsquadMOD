package tech.squadmc.squadmcor.client;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.client.renderer.entity.SpikeMissileRenderer;
import tech.squadmc.squadmcor.init.SpikeContent;
import tech.squadmc.squadmcor.item.SpikeItem;
import tech.squadmc.squadmcor.network.SpikeNetwork;
import tech.squadmc.squadmcor.spike.SpikeControl;
import tech.squadmc.squadmcor.squadmc;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public final class SpikeClient {
    private static final ResourceLocation HUD = sbw("javelin_hud");
    private static final ResourceLocation TOP = sbw("top");
    private static final ResourceLocation LOADED = sbw("missile_green");
    private static final ResourceLocation EMPTY = sbw("missile_red");
    private static boolean wasHolding;
    private static boolean shotLatched;
    private static boolean seekVisible;

    private SpikeClient() {}

    private static ResourceLocation sbw(String name) {
        return new ResourceLocation("superbwarfare", "textures/overlay/javelin/" + name + ".png");
    }

    private static boolean inScope(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null || !player.isAlive() || player.isSpectator() || mc.screen != null || !mc.isWindowActive()
                || !(player.getMainHandItem().getItem() instanceof SpikeItem)
                || !mc.options.getCameraType().isFirstPerson()
                || !ClientEventHandler.zoom || ClientEventHandler.zoomPos <= 0.8
                || ClientEventHandler.isEditing) return false;
        if (player.getVehicle() instanceof VehicleEntity vehicle && vehicle.banHand(player)) return false;
        return !GunData.from(player.getMainHandItem()).reloading();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) {
            wasHolding = false;
            shotLatched = false;
            seekVisible = false;
            return;
        }
        boolean holding = mc.player.getMainHandItem().getItem() instanceof SpikeItem;
        boolean pressed = GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT)
                == GLFW.GLFW_PRESS;
        if (!pressed) shotLatched = false;
        seekVisible = holding && pressed && !shotLatched && inScope(mc)
                && ((SpikeItem) mc.player.getMainHandItem().getItem())
                .canShoot(GunData.from(mc.player.getMainHandItem()), mc.player);
        if (holding || wasHolding) {
            sendInput(mc, pressed);
        }
        wasHolding = holding;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void mouseButton(InputEvent.MouseButton.Pre event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;
        if (!(mc.player.getMainHandItem().getItem() instanceof SpikeItem) && !wasHolding) return;
        boolean pressed = event.getAction() == GLFW.GLFW_PRESS;
        if (!pressed) {
            shotLatched = false;
            seekVisible = false;
        }
        // SBW consumes this mouse event, so KeyMapping.isDown() is not reliable.
        // Send edges too: a release/repress between two ticks must reset the charge.
        sendInput(mc, pressed);
    }

    private static void sendInput(Minecraft mc, boolean pressed) {
        SpikeNetwork.CHANNEL.sendToServer(new SpikeNetwork.ScopeState(
                inScope(mc), pressed && mc.screen == null && mc.isWindowActive(), mc.player.getInventory().selected));
    }

    public static void onShotFired(int slot, UUID launcherId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.getInventory().selected != slot
                || !(mc.player.getMainHandItem().getItem() instanceof SpikeItem)) return;
        var tag = mc.player.getMainHandItem().getTag();
        if (tag != null && tag.hasUUID(SpikeControl.LAUNCHER_ID)
                && !launcherId.equals(tag.getUUID(SpikeControl.LAUNCHER_ID))) return;
        shotLatched = true;
        seekVisible = false;
        // Reuse Javelin's recoil animation without handleClientShoot(), which
        // sends a second ShootMessage and is not a receive-only acknowledgement.
        ClientEventHandler.fireRecoilTime = 10.0;
        ClientEventHandler.noSprintTicks = 7.0F;
    }

    @SubscribeEvent
    public static void hideCrosshair(RenderGuiOverlayEvent.Pre event) {
        if (inScope(Minecraft.getInstance()) && event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            event.setCanceled(true);
        }
    }

    private static void drawScope(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        if (!inScope(mc) || mc.options.hideGui) return;
        int size = Math.round(Math.min(width, height) * 1.35F);
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        if (x > 0) graphics.fill(0, 0, x, height, 0xFF000000);
        if (x + size < width) graphics.fill(x + size, 0, width, height, 0xFF000000);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        graphics.blit(HUD, x, y, 0, 0, size, size, size, size);
        graphics.blit(TOP, x, y, 0, 0, size, size, size, size);
        boolean loaded = GunData.from(mc.player.getMainHandItem()).hasEnoughAmmoToShoot(mc.player);
        graphics.blit(loaded ? LOADED : EMPTY, x, y, 0, 0, size, size, size, size);
        RenderSystem.disableBlend();
        if (seekVisible) drawSeek(graphics, mc, x, y, size);
    }

    private static void drawSeek(GuiGraphics graphics, Minecraft mc, int x, int y, int size) {
        String text = "SEEK";
        int textX = -mc.font.width(text) / 2;
        int textY = -mc.font.lineHeight / 2;

        float scale = size / 512.0F; // Надпись в 1,5 раза меньше.
        float outline = 0.75F; // Обводка вдвое тоньше исходной с учётом масштаба.

        graphics.pose().pushPose();
        graphics.pose().translate(x + size / 2.0F, y + size * 0.695F, 200);
        graphics.pose().scale(scale, scale, 1);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                graphics.pose().pushPose();
                graphics.pose().translate(dx * outline, dy * outline, 0);
                graphics.drawString(mc.font, text, textX, textY, 0xFFFFFFFF, false);
                graphics.pose().popPose();
            }
        }

        graphics.drawString(mc.font, text, textX, textY, 0xFFE0E000, false);
        graphics.pose().popPose();
    }

    @Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        @SubscribeEvent
        public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SpikeContent.SPIKE_MISSILE.get(), SpikeMissileRenderer::new);
        }

        @SubscribeEvent
        public static void overlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("spike_scope", SpikeClient::drawScope);
        }
    }
}
