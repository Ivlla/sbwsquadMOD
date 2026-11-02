package tech.squadmc.squadmcor.client.smoke;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.Entity;

/**
 * Перенесено из aasgranate (client/SmokePuffCache) — пакет-приватный кэш
 * иммутабельных случайных атрибутов пуфов; слабые ключи не удерживают
 * удалённые сущности/миры.
 */
final class SmokePuffCache {

    private static final Map<Entity, Puff[]> CACHE = new WeakHashMap<>();

    private SmokePuffCache() {}

    static Puff[] get(Entity entity, int count) {
        Puff[] puffs = CACHE.get(entity);
        if (puffs == null || puffs.length != count) {
            puffs = new Puff[count];
            for (int i = 0; i < count; i++) {
                int seed = System.identityHashCode(entity) * 31 + i;
                puffs[i] = new Puff(
                        hash(seed) * (float) (Math.PI * 2.0),       // azimuth — угол вокруг центра
                        (float) Math.sqrt(hash(seed + 1)),          // rFrac — корень для равномерного диска
                        hash(seed + 2),                             // yFrac — доля высоты
                        0.55F + hash(seed + 3) * 0.90F,             // sizeVar — размер пуфа
                        (hash(seed + 4) - 0.5F) * 0.004F,           // spin — медленное вращение
                        hash(seed + 5) * 100.0F,                    // p1..p3 — фазы шумового дрейфа
                        hash(seed + 6) * 100.0F,
                        hash(seed + 7) * 100.0F,
                        (hash(seed + 8) - 0.5F) * 0.16F,            // greyVar — разброс серого
                        0.55F + hash(seed + 9) * 0.45F);            // alphaFactor — индивидуальная плотность
            }
            CACHE.put(entity, puffs);
        }
        return puffs;
    }

    static final class Puff {
        final float azimuth, rFrac, yFrac, sizeVar, spin;
        final float p1, p2, p3;
        final float greyVar, alphaFactor;

        Puff(float azimuth, float rFrac, float yFrac, float sizeVar, float spin,
             float p1, float p2, float p3, float greyVar, float alphaFactor) {
            this.azimuth = azimuth;
            this.rFrac = rFrac;
            this.yFrac = yFrac;
            this.sizeVar = sizeVar;
            this.spin = spin;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.greyVar = greyVar;
            this.alphaFactor = alphaFactor;
        }
    }

    /** Тот же целочисленный микс, что в рендерере — детерминированный разброс без Random. */
    private static float hash(int seed) {
        int h = seed * 0x27D4EB2D;
        h ^= h >>> 15;
        h *= 0x85EBCA6B;
        h ^= h >>> 13;
        h *= 0xC2B2AE35;
        h ^= h >>> 16;
        return (h >>> 8) / (float) (1 << 24);
    }
}
