package tech.squadmc.squadmcor.item.launcher;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.projectile.RpgRocketStandardEntity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import tech.squadmc.squadmcor.squadmc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Баллистика ручных гранатомётов: замедление в полёте и дистанция взведения.
 *
 * <p>Оба параметра задаются в JSON оружия (data/squadmc/sbw/guns/&lt;item&gt;.json):
 * <ul>
 *   <li>"Drag" — доля скорости, теряемая за тик (0.005 = 0.5% за тик, 0 / нет ключа = без замедления);</li>
 *   <li>"ArmDistance" — сколько метров (блоков) ракета летит невзведённой. Если она во что-то попадает
 *       ближе — взрыва нет, ракета исчезает и звучит {@link #DUD_SOUND}. 0 = взведена сразу;</li>
 *   <li>"Accel" (м/с²) и "AccelTime" (секунды) — разгон маршевого двигателя после старта, как в Squad:
 *       стартовая скорость задаётся "Velocity", затем ракета набирает Accel * AccelTime м/с.</li>
 * </ul>
 * Значение на уровне оружия — по умолчанию; внутри "Override" конкретного боеприпаса (AmmoType[i])
 * оно переопределяется, так же как Velocity и Gravity. Т.е. замедление и взведение — на тип боеприпаса.
 *
 * <p>Работает только для ракет, выпущенных игроком/мобом с гранатомётом в руках на земле. Ракеты техники,
 * Spike (своя сущность) и пулевой режим SMAW не затрагиваются. Регистрируется само.
 */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class RocketDrag {

    /** Дистанция взведения, если в JSON не указана (метры). */
    private static final float DEFAULT_ARM_DISTANCE = 25F;
    private static final float MAX_ARM_DISTANCE = 200F;

    /** Звук "снаряд не взвёлся". Чтобы поставить свой, зарегистрируйте SoundEvent в ModSounds и подставьте сюда. */
    private static final SoundEvent DUD_SOUND = SoundEvents.ANVIL_LAND;
    private static final float DUD_VOLUME = 1.6F;
    private static final float DUD_PITCH = 1.7F;

    /** accel — м/с², accelTicks — сколько тиков работает двигатель. */
    private record Ballistics(float drag, float armDistance, float accel, int accelTicks) {
        static final Ballistics NONE = new Ballistics(0F, 0F, 0F, 0);
    }

    /** Параметры по id пусковой: [0] — по умолчанию для оружия, [1..] — для каждого типа боеприпаса. */
    private static volatile Map<String, Ballistics[]> table = Map.of();
    private static final List<Tracked> TRACKED = new ArrayList<>();

    private static final class Tracked {
        final RpgRocketStandardEntity rocket;
        boolean resolved;
        boolean armed;
        float drag;
        float armDistance;
        float accel;
        int accelLeft;
        Vec3 start;

        Tracked(RpgRocketStandardEntity rocket) {
            this.rocket = rocket;
        }
    }

    private RocketDrag() {
    }

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        event.addListener(new Loader());
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof RpgRocketStandardEntity rocket) TRACKED.add(new Tracked(rocket));
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide() || TRACKED.isEmpty()) return;
        if (!(event.level instanceof ServerLevel level)) return;
        boolean start = event.phase == TickEvent.Phase.START;

        Iterator<Tracked> it = TRACKED.iterator();
        while (it.hasNext()) {
            Tracked t = it.next();
            if (!t.rocket.isAlive()) {
                it.remove();
                continue;
            }
            if (t.rocket.level() != level) continue;

            // Параметры берём на первом тике, а не при входе в мир: к этому моменту владелец точно известен.
            if (!t.resolved) {
                t.resolved = true;
                Ballistics b = ballisticsOf(t.rocket);
                t.drag = b.drag();
                t.armDistance = b.armDistance();
                t.accel = b.accel();
                t.accelLeft = b.accelTicks();
                t.start = t.rocket.position();
                t.armed = t.armDistance <= 0F;
            }

            if (start) {
                // ДО того, как сущность сдвинется и сработает её собственный взрыв — проверяем, не упрётся ли
                // ракета за этот тик в препятствие, пока она ещё не взведена.
                if (!t.armed) {
                    if (t.rocket.position().distanceTo(t.start) >= t.armDistance) {
                        t.armed = true;
                    } else if (tryDud(level, t)) {
                        it.remove();
                        continue;
                    }
                }
            } else {
                Vec3 motion = t.rocket.getDeltaMovement();
                if (t.accelLeft > 0 && t.accel > 0F && motion.lengthSqr() > 1.0E-6) {
                    // м/с² -> блоков/тик за тик: делим на 20 * 20
                    motion = motion.add(motion.normalize().scale(t.accel / 400.0));
                    t.accelLeft--;
                }
                if (t.drag > 0F) motion = motion.scale(1.0 - t.drag);
                t.rocket.setDeltaMovement(motion);
                if (t.drag <= 0F && t.accelLeft <= 0 && t.armed) {
                    it.remove(); // ни замедления, ни разгона, ни взведения — следить больше не за чем
                }
            }
        }
    }

    /**
     * Если за этот тик ракета ударится в блок или сущность ближе дистанции взведения — убирает её без взрыва
     * и играет звук осечки. Возвращает true, если ракета удалена.
     */
    private static boolean tryDud(ServerLevel level, Tracked t) {
        RpgRocketStandardEntity rocket = t.rocket;
        Vec3 from = rocket.position();
        Vec3 motion = rocket.getDeltaMovement();
        if (motion.lengthSqr() < 1.0E-6) return false;
        // Небольшой запас по длине луча: собственная проверка попадания у ракеты чуть "щедрее" нашей.
        Vec3 to = from.add(motion.scale(1.15)).add(motion.normalize().scale(0.5));

        HitResult hit = null;
        double best = Double.MAX_VALUE;

        BlockHitResult block = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, rocket));
        if (block.getType() != HitResult.Type.MISS) {
            hit = block;
            best = from.distanceToSqr(block.getLocation());
        }

        Entity owner = rocket.getOwner();
        AABB area = rocket.getBoundingBox().expandTowards(motion).inflate(1.0);
        EntityHitResult entity = ProjectileUtil.getEntityHitResult(level, rocket, from, to, area,
                e -> e.isAlive() && e.isPickable() && !e.isSpectator() && e != owner && !(e instanceof Projectile));
        if (entity != null && from.distanceToSqr(entity.getLocation()) < best) {
            hit = entity;
        }
        if (hit == null) return false;

        Vec3 at = hit.getLocation();
        // Попадание дальше дистанции взведения — не осечка (ракета взведётся прямо в этом тике).
        if (at.distanceTo(t.start) >= t.armDistance) return false;

        rocket.discard();
        level.playSound(null, at.x, at.y, at.z, DUD_SOUND, SoundSource.PLAYERS, DUD_VOLUME, DUD_PITCH);
        level.sendParticles(ParticleTypes.SMOKE, at.x, at.y, at.z, 6, 0.1, 0.1, 0.1, 0.01);
        level.sendParticles(ParticleTypes.CRIT, at.x, at.y, at.z, 8, 0.15, 0.15, 0.15, 0.2);
        return true;
    }

    private static Ballistics ballisticsOf(RpgRocketStandardEntity rocket) {
        if (!(rocket.getOwner() instanceof LivingEntity owner) || owner.getVehicle() != null) return Ballistics.NONE;
        ItemStack stack = owner.getMainHandItem();
        if (!(stack.getItem() instanceof FclLauncherItem)) return Ballistics.NONE;
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        Ballistics[] values = id == null ? null : table.get(id.getPath());
        if (values == null) return Ballistics.NONE;
        int index = GunData.from(stack).selectedAmmoType.get() + 1;
        Ballistics b = index >= 0 && index < values.length ? values[index] : values[0];
        return new Ballistics(Math.max(0F, Math.min(b.drag(), 0.5F)),
                Math.max(0F, Math.min(b.armDistance(), MAX_ARM_DISTANCE)),
                Math.max(0F, Math.min(b.accel(), 2000F)),
                Math.max(0, Math.min(b.accelTicks(), 200)));
    }

    private static final class Loader extends SimpleJsonResourceReloadListener {
        Loader() {
            super(new Gson(), "sbw/guns");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
            Map<String, Ballistics[]> result = new HashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> file : files.entrySet()) {
                if (!file.getKey().getNamespace().equals(squadmc.MODID) || !file.getValue().isJsonObject()) continue;
                JsonObject gun = file.getValue().getAsJsonObject();
                Ballistics base = new Ballistics(number(gun, "Drag", 0F), number(gun, "ArmDistance", DEFAULT_ARM_DISTANCE),
                        number(gun, "Accel", 0F), Math.round(number(gun, "AccelTime", 0F) * 20F));
                List<Ballistics> values = new ArrayList<>();
                values.add(base);
                if (gun.get("AmmoType") instanceof JsonArray array) {
                    for (JsonElement entry : array) {
                        Ballistics b = base;
                        if (entry.isJsonObject() && entry.getAsJsonObject().get("Override") instanceof JsonObject override) {
                            b = new Ballistics(number(override, "Drag", base.drag()),
                                    number(override, "ArmDistance", base.armDistance()),
                                    number(override, "Accel", base.accel()),
                                    override.has("AccelTime") ? Math.round(number(override, "AccelTime", 0F) * 20F) : base.accelTicks());
                        }
                        values.add(b);
                    }
                }
                result.put(file.getKey().getPath(), values.toArray(new Ballistics[0]));
            }
            table = result;
        }

        private static float number(JsonObject object, String key, float fallback) {
            JsonElement value = object.get(key);
            return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber() ? value.getAsFloat() : fallback;
        }
    }
}
