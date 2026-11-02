package tech.squadmc.squadmcor.client.launcher;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import tech.squadmc.squadmcor.squadmc;

/** Plays the sound keyframes authored into the launcher animations. */
public final class FclSounds {

    private FclSounds() {
    }

    public static void play(String name) {
        // Keyframes outside the pack's own "fcl_" set refer to base-mod sounds that were not ported.
        if (name == null || !name.startsWith("fcl_")) return;
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        SoundEvent sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(squadmc.MODID, name));
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 1f, 1f, false);
    }
}
