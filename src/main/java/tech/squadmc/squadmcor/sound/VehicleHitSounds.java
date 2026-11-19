package tech.squadmc.squadmcor.sound;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.squadmc.squadmcor.init.ModSounds;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/** Внутренний звук попадания (как в Squad). Только СЕРВЕР. */
public final class VehicleHitSounds {

    private static final Logger LOG = LogManager.getLogger("squadmc-hit");

    /** true = пиши в лог каждое попадание по технике. На время теста оставь true. */
    public static final boolean DEBUG = false;

    /** true = ТЕСТ В ОДИНОЧКУ: звук слышит ещё и стрелок (владелец снаряда). Потом поставь false. */
    public static final boolean TEST_PLAY_TO_SHOOTER = false;

    private static final int COOLDOWN_TICKS = 3;

    // id снаряда -> калибр. Правь тут.
    private static final Map<ResourceLocation, HitCaliber> BY_ID = new HashMap<>();
    static {
        put("superbwarfare:cannon_shell", HitCaliber.HEAVY);
        put("superbwarfare:mortar_shell", HitCaliber.HEAVY);
        put("superbwarfare:wire_guide_missile", HitCaliber.HEAVY);
        put("squadmc:malyutka", HitCaliber.HEAVY);
        put("superbwarfare:small_cannon_shell", HitCaliber.MEDIUM);
        put("superbwarfare:gun_grenade", HitCaliber.MEDIUM);
        put("superbwarfare:projectile", HitCaliber.SMALL);
    }

    private static void put(String id, HitCaliber c) {
        BY_ID.put(new ResourceLocation(id), c);
    }

    private static final Map<Entity, long[]> LAST = new WeakHashMap<>();

    private VehicleHitSounds() {}

    @Nullable
    public static HitCaliber classify(Entity projectile) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(projectile.getType());
        if (id == null) return null;
        HitCaliber c = BY_ID.get(id);
        if (c != null) return c;
        String p = id.getPath();
        if (p.contains("missile") || p.contains("rocket")) return HitCaliber.HEAVY;
        return null;
    }

    /** Вызывается из события SBW. Сервер. */
    public static void onVehicleHit(Entity vehicle, Entity projectile) {
        if (vehicle.level().isClientSide) return;

        HitCaliber caliber = classify(projectile);
        if (DEBUG) {
            LOG.info("hit: vehicle={} projectile={} caliber={} passengers={}",
                    ForgeRegistries.ENTITY_TYPES.getKey(vehicle.getType()),
                    ForgeRegistries.ENTITY_TYPES.getKey(projectile.getType()),
                    caliber, vehicle.getPassengers().size());
        }
        if (caliber == null) return;

        boolean hasPassengers = !vehicle.getPassengers().isEmpty();
        if (!hasPassengers && !TEST_PLAY_TO_SHOOTER) return;

        long now = vehicle.level().getGameTime();
        long[] last = LAST.computeIfAbsent(vehicle, e -> new long[]{-100, -100, -100});
        if (now - last[caliber.ordinal()] < COOLDOWN_TICKS) return;
        last[caliber.ordinal()] = now;

        RegistryObject<SoundEvent> ro = switch (caliber) {
            case HEAVY -> ModSounds.HIT_HEAVY;
            case MEDIUM -> ModSounds.HIT_MEDIUM;
            case SMALL -> ModSounds.HIT_SMALL;
        };
        if (!ro.isPresent()) return;

        Holder<SoundEvent> holder = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ro.get());
        float pitch = 0.95F + vehicle.level().getRandom().nextFloat() * 0.1F;
        float volume = caliber == HitCaliber.HEAVY ? 1.0F : (caliber == HitCaliber.MEDIUM ? 0.85F : 0.7F);

        // Пассажиры (вся цепочка, вдруг турель — отдельная сущность)
        for (Entity passenger : vehicle.getIndirectPassengers()) {
            if (passenger instanceof ServerPlayer sp) send(sp, holder, volume, pitch);
        }
        // ТЕСТ: стрелок тоже слышит
        if (TEST_PLAY_TO_SHOOTER && getOwner(projectile) instanceof ServerPlayer shooter) {
            send(shooter, holder, volume, pitch);
        }
    }

    private static void send(ServerPlayer sp, Holder<SoundEvent> holder, float volume, float pitch) {
        sp.connection.send(new ClientboundSoundEntityPacket(
                holder, SoundSource.NEUTRAL, sp, volume, pitch, sp.level().getRandom().nextLong()));
    }

    @Nullable
    private static Entity getOwner(Entity projectile) {
        return projectile instanceof net.minecraft.world.entity.projectile.Projectile p ? p.getOwner() : null;
    }
}