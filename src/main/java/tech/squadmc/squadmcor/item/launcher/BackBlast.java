package tech.squadmc.squadmcor.item.launcher;

import com.atsuishio.superbwarfare.entity.projectile.RpgRocketStandardEntity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
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
 * Обратная струя газов (backblast): при выстреле из гранатомёта всё живое в конусе позади стрелка получает
 * урон и отбрасывание. Урон убывает с расстоянием, стены защищают (проверка луча до цели).
 *
 * <p>Параметры на каждый гранатомёт — в data/squadmc/backblast/launchers.json, ключ = id предмета:
 * <pre>
 *   "fcl_rpg7v2": { "Length": 10, "Width": 3, "Damage": 18 }
 * </pre>
 * Length — длина опасной зоны (блоки), Width — полуширина конуса на её дальнем конце, Damage — урон
 * вплотную к трубе (в половинках сердец; у игрока 20 ХП). Нет записи — у оружия нет обратной струи.
 *
 * <p>Как и {@link RocketDrag}: срабатывает только для ракеты, выпущенной с гранатомётом в руках на земле;
 * пулевой режим SMAW, техника и Spike не затрагиваются. Регистрируется само.
 */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class BackBlast {

    private record Spec(float length, float width, float damage) {
    }

    private static volatile Map<String, Spec> table = Map.of();
    private static final List<RpgRocketStandardEntity> PENDING = new ArrayList<>();

    private BackBlast() {
    }

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        event.addListener(new Loader());
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof RpgRocketStandardEntity rocket) PENDING.add(rocket);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START || PENDING.isEmpty()) return;
        if (!(event.level instanceof ServerLevel level)) return;
        Iterator<RpgRocketStandardEntity> it = PENDING.iterator();
        while (it.hasNext()) {
            RpgRocketStandardEntity rocket = it.next();
            if (!rocket.isAlive()) {
                it.remove();
                continue;
            }
            if (rocket.level() != level) continue;
            // Владелец выставляется уже после входа в мир, поэтому обрабатываем на первом тике.
            it.remove();
            blast(level, rocket);
        }
    }

    private static void blast(ServerLevel level, RpgRocketStandardEntity rocket) {
        if (!(rocket.getOwner() instanceof LivingEntity owner) || owner.getVehicle() != null) return;
        ItemStack stack = owner.getMainHandItem();
        if (!(stack.getItem() instanceof FclLauncherItem)) return;
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        Spec spec = id == null ? null : table.get(id.getPath());
        if (spec == null || spec.length() <= 0F || spec.damage() <= 0F) return;

        Vec3 forward = rocket.getDeltaMovement();
        if (forward.lengthSqr() < 1.0E-6) forward = owner.getLookAngle();
        Vec3 back = forward.normalize().scale(-1.0);
        // Казённая часть трубы: у плеча, чуть позади глаз.
        Vec3 origin = owner.getEyePosition().add(0.0, -0.25, 0.0).add(back.scale(0.4));

        double length = spec.length();
        AABB box = new AABB(origin, origin.add(back.scale(length))).inflate(spec.width() + 1.0);
        DamageSource source = level.damageSources().explosion(owner, owner);

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e != owner && e.isAlive() && !e.isSpectator())) {
            Vec3 center = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
            Vec3 toTarget = center.subtract(origin);
            double along = toTarget.dot(back);
            if (along < -0.3 || along > length) continue;
            double across = toTarget.subtract(back.scale(along)).length();
            double allowed = 0.6 + target.getBbWidth() * 0.5 + spec.width() * Math.max(along, 0.0) / length;
            if (across > allowed) continue;
            // Стены и блоки гасят струю.
            if (level.clip(new ClipContext(origin, center, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner))
                    .getType() != HitResult.Type.MISS) continue;

            double falloff = 1.0 - Math.max(along, 0.0) / length;
            float damage = (float) (spec.damage() * falloff);
            if (damage < 0.5F) continue;
            target.hurt(source, damage);
            target.push(back.x * 0.7 * falloff, 0.15 * falloff, back.z * 0.7 * falloff);
            target.hurtMarked = true; // чтобы игрок получил отбрасывание
        }

        // Визуально: огонь у казённой части и шлейф дыма по конусу.
        level.sendParticles(ParticleTypes.FLAME, origin.x, origin.y, origin.z, 10, 0.15, 0.15, 0.15, 0.08);
        for (double k : new double[]{0.25, 0.5, 0.8}) {
            Vec3 p = origin.add(back.scale(length * k));
            double spread = spec.width() * k * 0.5;
            level.sendParticles(ParticleTypes.LARGE_SMOKE, p.x, p.y, p.z, 6, spread, spread * 0.5, spread, 0.02);
        }
    }

    private static final class Loader extends SimpleJsonResourceReloadListener {
        Loader() {
            super(new Gson(), "backblast");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
            Map<String, Spec> result = new HashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> file : files.entrySet()) {
                if (!file.getKey().getNamespace().equals(squadmc.MODID) || !file.getValue().isJsonObject()) continue;
                for (Map.Entry<String, JsonElement> entry : file.getValue().getAsJsonObject().entrySet()) {
                    if (!entry.getValue().isJsonObject()) continue;
                    JsonObject o = entry.getValue().getAsJsonObject();
                    result.put(entry.getKey(), new Spec(
                            clamp(number(o, "Length"), 0F, 40F),
                            clamp(number(o, "Width"), 0F, 10F),
                            clamp(number(o, "Damage"), 0F, 200F)));
                }
            }
            table = result;
        }

        private static float number(JsonObject object, String key) {
            JsonElement value = object.get(key);
            return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber() ? value.getAsFloat() : 0F;
        }

        private static float clamp(float v, float min, float max) {
            return Math.max(min, Math.min(v, max));
        }
    }
}
