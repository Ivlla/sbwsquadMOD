package tech.squadmc.squadmcor.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.bmd4Entity;
import tech.squadmc.squadmcor.init.ModSounds;

public class Bmd4EngineSoundInstance extends AbstractTickableSoundInstance {
    private final bmd4Entity vehicle;
    private final boolean isFirstPerson;
    private final boolean isIdle;

    private boolean fadingOut = false;
    private float targetVolume = 1.0F;

    public Bmd4EngineSoundInstance(bmd4Entity vehicle, boolean isFirstPerson, boolean isIdle) {
        super(isIdle ?
                        (isFirstPerson ? ModSounds.BMD4_ENGINE_IDLE_1P.get() : ModSounds.BMD4_ENGINE_IDLE_3P.get()) :
                        (isFirstPerson ? ModSounds.BMD4_ENGINE_RUNNING_1P.get() : ModSounds.BMD4_ENGINE_RUNNING_3P.get()),
                SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());

        this.vehicle = vehicle;
        this.isFirstPerson = isFirstPerson;
        this.isIdle = isIdle;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.01F;
        this.pitch = 1.0F;

        // === РќРђРЎРўР РћР™РљРђ Р”РРЎРўРђРќР¦РР РЎР›Р«РЁРРњРћРЎРўР ===
        // 1.0F = 16 Р±Р»РѕРєРѕРІ (РІРЅСѓС‚СЂРё РєР°Р±РёРЅС‹)
        // 3.0F = 48 Р±Р»РѕРєРѕРІ (РЅР° С…РѕР»РѕСЃС‚С‹С… СЃРЅР°СЂСѓР¶Рё)
        // 5.0F = 80 Р±Р»РѕРєРѕРІ (РЅР° С…РѕРґСѓ СЃРЅР°СЂСѓР¶Рё, РјРѕР¶РЅРѕ РїРѕСЃС‚Р°РІРёС‚СЊ 6.0F РґР»СЏ 96 Р±Р»РѕРєРѕРІ)
        if (isFirstPerson) {
            this.targetVolume = 1.0F;
        } else {
            this.targetVolume = isIdle ? 3.0F : 5.0F;
        }

        this.x = (float)vehicle.getX();
        this.y = (float)vehicle.getY();
        this.z = (float)vehicle.getZ();
    }

    @Override
    public void tick() {
        if (this.vehicle.isRemoved() || !this.vehicle.isAlive()) {
            this.stop();
            return;
        }

        // РћР±РЅРѕРІР»РµРЅРёРµ РєРѕРѕСЂРґРёРЅР°С‚ Р·РІСѓРєР°
        this.x = (float)this.vehicle.getX();
        this.y = (float)this.vehicle.getY();
        this.z = (float)this.vehicle.getZ();

        if (this.fadingOut) {
            // Р—Р°С‚СѓС…Р°РЅРёРµ РїСЂРѕРїРѕСЂС†РёРѕРЅР°Р»СЊРЅРѕ targetVolume, С‡С‚РѕР±С‹ РЅРµ РґР»РёР»РѕСЃСЊ СЃР»РёС€РєРѕРј РґРѕР»РіРѕ РїСЂРё Р±РѕР»СЊС€РёС… Р·РЅР°С‡РµРЅРёСЏС…
            this.volume = Math.max(0.0F, this.volume - (this.targetVolume * 0.05F));
            if (this.volume <= 0.0F) {
                this.stop();
            }
            return;
        }

        // РџР»Р°РІРЅРѕРµ РЅР°СЂР°СЃС‚Р°РЅРёРµ РіСЂРѕРјРєРѕСЃС‚Рё РґРѕ targetVolume
        if (this.volume < this.targetVolume) {
            this.volume = Mth.lerp(0.1F, this.volume, this.targetVolume);
        }

        // Р”РёРЅР°РјРёС‡РµСЃРєРѕРµ РёР·РјРµРЅРµРЅРёРµ РІС‹СЃРѕС‚С‹ Р·РІСѓРєР° (pitch)
        double speed = this.vehicle.getDeltaMovement().horizontalDistance();
        if (this.isIdle) {
            this.pitch = 0.95F + (float)Math.sin(this.vehicle.tickCount * 0.1D) * 0.02F;
        } else {
            float speedFactor = (float)(speed * 1.6D);
            this.pitch = Mth.clamp(0.85F + speedFactor, 0.85F, 1.3F);
        }
    }

    public void startFadeOut() {
        this.fadingOut = true;
    }

    public boolean isFirstPerson() { return this.isFirstPerson; }
    public boolean isIdle() { return this.isIdle; }
    public boolean isFadingOut() { return this.fadingOut; }
}
