package tech.squadmc.squadmcor.heli;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import tech.squadmc.squadmcor.squadmc;

/** Клавиша двигателя вертолёта: удерживать, чтобы запустить или заглушить. */
@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class HeliKeybinds {
    public static final KeyMapping ENGINE_TOGGLE = new KeyMapping(
            "key.squadmc.engine_toggle",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.squadmc_heli");

    private HeliKeybinds() {}

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(ENGINE_TOGGLE);
    }
}
