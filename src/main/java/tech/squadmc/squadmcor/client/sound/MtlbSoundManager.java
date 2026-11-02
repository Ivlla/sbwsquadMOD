package tech.squadmc.squadmcor.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tech.squadmc.squadmcor.entity.mtlbEntity;

import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
public class MtlbSoundManager {
    private static final WeakHashMap<mtlbEntity, MtlbSoundInstance> activeSounds = new WeakHashMap<>();

    public static void updateSounds(mtlbEntity vehicle) {
        if (vehicle.isRemoved() || !vehicle.isAlive() || !vehicle.isEngineStarted()) {
            MtlbSoundInstance sound = activeSounds.remove(vehicle);
            if (sound != null) {
                sound.startFadeOut();
            }
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        boolean isFirstPerson = mc.player != null && mc.player.getVehicle() == vehicle;
        double speedSqr = vehicle.getDeltaMovement().horizontalDistanceSqr();

        MtlbSoundInstance currentSound = activeSounds.get(vehicle);
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

            MtlbSoundInstance newSound = new MtlbSoundInstance(vehicle, isFirstPerson, isIdle);
            activeSounds.put(vehicle, newSound);
            mc.getSoundManager().play(newSound);
        }
    }
}
