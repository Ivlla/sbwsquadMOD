package tech.squadmc.squadmcor.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tech.squadmc.squadmcor.entity.Bmp1Entity;

import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
public class Bmp1SoundManager {
    private static final WeakHashMap<Bmp1Entity, Bmp1EngineSoundInstance> activeSounds = new WeakHashMap<>();

    public static void updateSounds(Bmp1Entity vehicle) {
        if (vehicle.isRemoved() || !vehicle.isAlive() || !vehicle.isEngineStarted()) {
            Bmp1EngineSoundInstance sound = activeSounds.remove(vehicle);
            if (sound != null) {
                sound.startFadeOut();
            }
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        boolean isFirstPerson = mc.player != null && mc.player.getVehicle() == vehicle;
        double speedSqr = vehicle.getDeltaMovement().horizontalDistanceSqr();

        Bmp1EngineSoundInstance currentSound = activeSounds.get(vehicle);
        boolean isCurrentlyIdle = currentSound == null || currentSound.isIdle();

        boolean isIdle;
        if (currentSound == null) {
            isIdle = speedSqr < 0.005;
        } else {
            isIdle = isCurrentlyIdle ? (speedSqr < 0.008) : (speedSqr < 0.002);
        }

        if (currentSound == null || currentSound.isStopped() ||
                currentSound.isFirstPerson() != isFirstPerson || currentSound.isIdle() != isIdle) {

            if (currentSound != null && !currentSound.isFadingOut()) {
                currentSound.startFadeOut();
            }

            Bmp1EngineSoundInstance newSound = new Bmp1EngineSoundInstance(vehicle, isFirstPerson, isIdle);
            activeSounds.put(vehicle, newSound);
            mc.getSoundManager().play(newSound);
        }
    }
}
