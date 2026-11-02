package tech.squadmc.squadmcor.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Запуск эффекта на клиенте: проверка условий MTS, создание частиц и звуков. */
@OnlyIn(Dist.CLIENT)
public final class MtsFxClient {
    /** Как далеко искать поверхность: SBW передаёт только точку взрыва, без стороны блока. */
    private static final double SURFACE_PROBE = 0.75;
    /** SoundInstance.DEFAULT_MAX_DISTANCE в MTS. */
    private static final double DEFAULT_MAX_SOUND_DISTANCE = 32;
    private static final Logger LOG = LogManager.getLogger("squadmc-fx");
    private static final Set<ResourceLocation> MISSING_SOUNDS = new HashSet<>();

    private MtsFxClient() {}

    /** Состояние попадания: то, что в MTS дают переменные bullet_hit_* и blockmaterial_*. */
    private record Hit(boolean block, boolean vehicle, Vec3 probe, Direction normal) {}

    public static void play(MtsFxPreset preset, boolean vehicleHit, Vec3 pos, Direction normal) {
        // enqueueWork молча глотает исключения, поэтому ошибку пишем в лог сами.
        try {
            playUnchecked(preset, vehicleHit, pos, normal);
        } catch (RuntimeException e) {
            LOG.error("Ошибка эффекта {}", preset, e);
        }
    }

    /** Частицы FACING летят вдоль направления выстрела, а не вдоль нормали поверхности. */
    @Nullable
    private static Quaternionf directedFrame;

    public static void playDirected(MtsFxPreset preset, Vec3 pos, Vec3 direction) {
        Vec3 unit = direction.lengthSqr() < 1.0E-6 ? new Vec3(0, 1, 0) : direction.normalize();
        directedFrame = new Quaternionf().rotationTo(new Vector3f(0, 1, 0), new Vector3f((float) unit.x, (float) unit.y, (float) unit.z));
        try {
            play(preset, false, pos, Direction.UP);
        } finally {
            directedFrame = null;
        }
    }

    /** Пыль от выстрела на земле под точкой, если земля близко. */
    public static void playGroundBlast(Vec3 pos, double maxHeight) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        Vec3 ground = groundBelow(level, pos);
        if (pos.y - ground.y <= maxHeight) play(MtsFxPreset.GROUND_BLAST, false, ground.add(0, 0.6, 0), Direction.UP);
    }

    private static void playUnchecked(MtsFxPreset preset, boolean vehicleHit, Vec3 pos, Direction normal) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        MtsFxDefs.Preset fx = MtsFxLibrary.get(preset);
        if (level == null || fx == null) return;

        Hit hit = vehicleHit ? new Hit(false, true, pos, normal) : findSurface(level, pos);
        String material = material(level, hit.probe());

        // Звук сначала: исключение в одной частице не должно оставить взрыв беззвучным.
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        double distance = cam.distanceTo(pos);
        for (MtsFxDefs.SoundDef def : fx.sounds) {
            if (active(def.conds, hit, material)) {
                playSound(mc, level.random, def, distance);
            }
        }

        for (MtsFxDefs.ParticleDef def : fx.particles) {
            if (active(def.conds, hit, material)) {
                spawn(mc, level, def, pos, hit);
            }
        }
    }

    private static Hit findSurface(ClientLevel level, Vec3 pos) {
        if (!level.isEmptyBlock(BlockPos.containing(pos.x, pos.y, pos.z))) {
            return new Hit(true, false, pos, Direction.UP);
        }
        // Вниз проверяем первым: большинство взрывов происходит на земле.
        Direction[] order = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};
        for (Direction direction : order) {
            Vec3 probe = pos.add(direction.getStepX() * SURFACE_PROBE, direction.getStepY() * SURFACE_PROBE, direction.getStepZ() * SURFACE_PROBE);
            if (!level.isEmptyBlock(BlockPos.containing(probe.x, probe.y, probe.z))) {
                return new Hit(true, false, probe, direction.getOpposite());
            }
        }
        return new Hit(false, false, pos, Direction.UP);
    }

    private static boolean active(@Nullable List<MtsFxDefs.Cond> conds, Hit hit, @Nullable String material) {
        if (conds == null) return true;
        for (MtsFxDefs.Cond cond : conds) {
            double value = value(cond.variable, hit, material);
            if (value < cond.min || value > cond.max) return false;
        }
        return true;
    }

    private static double value(String variable, Hit hit, @Nullable String material) {
        return switch (variable) {
            case "bullet_hit" -> 1;
            case "bullet_hit_block" -> hit.block() ? 1 : 0;
            case "bullet_hit_vehicle" -> hit.vehicle() ? 1 : 0;
            default -> variable.startsWith("blockmaterial_")
                    && variable.substring("blockmaterial_".length()).equals(material) ? 1 : 0;
        };
    }

    /** То же, что WrapperWorld.getBlockMaterial в MTS. null — воздух. */
    @Nullable
    private static String material(ClientLevel level, Vec3 pos) {
        BlockPos blockPos = BlockPos.containing(pos.x, pos.y, pos.z);
        if (level.isEmptyBlock(blockPos)) return null;
        BlockState s = level.getBlockState(blockPos);
        if (s.getBlock() instanceof GrassBlock) return "grass";
        if (s.is(Tags.Blocks.GRAVEL)) return "gravel";
        if (s.is(BlockTags.DIRT)) return "dirt";
        if (s.is(Tags.Blocks.GLASS)) return "glass";
        if (s.is(BlockTags.ICE)) return "ice";
        if (s.is(BlockTags.LEAVES)) return "leaves";
        if (s.is(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON) || s.is(Tags.Blocks.STORAGE_BLOCKS_IRON)
                || s.is(Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD) || s.is(Tags.Blocks.STORAGE_BLOCKS_GOLD)
                || s.is(Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER) || s.is(Tags.Blocks.STORAGE_BLOCKS_COPPER)
                || s.is(Tags.Blocks.STORAGE_BLOCKS_NETHERITE)) return "metal";
        if (s.is(BlockTags.SAND)) return "sand";
        if (s.is(BlockTags.SNOW)) return "snow";
        if (s.is(Tags.Blocks.STONE) || s.is(Tags.Blocks.COBBLESTONE)) return "stone";
        if (s.is(BlockTags.PLANKS) || s.is(BlockTags.WOODEN_FENCES)) return "wood";
        if (s.is(BlockTags.WOOL)) return "wool";
        if (s.getFluidState().is(FluidTags.LAVA)) return "lava";
        if (s.getFluidState().is(FluidTags.WATER)) return "water";
        return "normal";
    }

    private static void spawn(Minecraft mc, ClientLevel level, MtsFxDefs.ParticleDef def, Vec3 origin, Hit hit) {
        // Точка, где берутся свойства блока: под частицей или точка попадания.
        Vec3 blockCheck = def.getBlockPropertiesFromGround ? groundBelow(level, origin) : hit.probe();
        BlockPos blockPos = BlockPos.containing(blockCheck.x, blockCheck.y, blockCheck.z);
        BlockState state = level.getBlockState(blockPos);
        if (def.isBreak() && level.isEmptyBlock(blockPos)) return;

        TextureAtlasSprite[] sprites = def.isBreak()
                ? new TextureAtlasSprite[]{mc.getBlockRenderer().getBlockModelShaper().getParticleIcon(state)}
                : sprites(mc, def);
        Integer blockColor = def.useBlockColor ? Integer.valueOf(state.getMapColor(level, blockPos).col) : null;

        // Только FACING разворачивается по нормали поверхности. Ориентации снаряда у SBW здесь нет,
        // поэтому ENTITY и WORLD считаются осью Y вверх.
        Quaternionf frame = "FACING".equals(def.spawningOrientation) ? (directedFrame != null ? directedFrame : fromUp(hit.normal())) : new Quaternionf();

        Vec3 position = origin;
        if (def.pos != null) {
            position = origin.add(rotate(frame, def.pos[0], def.pos[1], def.pos[2]));
        }

        for (int i = 0; i < def.quantity; i++) {
            Vec3 motion = initialMotion(level.random, def, frame);
            Quaternionf fixed = "FIXED".equals(def.renderingOrientation) ? fixedRotation(level.random, def, frame) : null;
            mc.particleEngine.add(new MtsFxParticle(level, position, motion, def, sprites, fixed, blockColor));
        }
    }

    /** То же, что world.getHeight в MTS: первый непустой блок под точкой. */
    private static Vec3 groundBelow(ClientLevel level, Vec3 pos) {
        BlockPos at = BlockPos.containing(pos.x, pos.y, pos.z);
        while (at.getY() > level.getMinBuildHeight() && level.isEmptyBlock(at)) {
            at = at.below();
        }
        BlockState state = level.getBlockState(at);
        boolean bottomSlab = state.getBlock() instanceof SlabBlock && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
        return new Vec3(pos.x, at.getY() + (bottomSlab ? 0.25 : 0.5), pos.z);
    }

    public static TextureAtlasSprite[] spritesFor(Minecraft mc, MtsFxDefs.ParticleDef def) {
        return sprites(mc, def);
    }

    private static TextureAtlasSprite[] sprites(Minecraft mc, MtsFxDefs.ParticleDef def) {
        if (def.textures == null) return new TextureAtlasSprite[0];
        AbstractTexture texture = mc.getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES);
        if (!(texture instanceof TextureAtlas atlas)) return new TextureAtlasSprite[0];
        TextureAtlasSprite[] result = new TextureAtlasSprite[def.textures.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = atlas.getSprite(new ResourceLocation(def.textures.get(i)));
        }
        return result;
    }

    private static Vec3 initialMotion(RandomSource random, MtsFxDefs.ParticleDef def, Quaternionf frame) {
        if (def.initialVelocity == null) return Vec3.ZERO;
        double[] v = def.initialVelocity;
        double x, y, z;
        if (def.spreadRandomness != null) {
            double[] s = def.spreadRandomness;
            x = 2 * s[0] * random.nextDouble() - s[0] + v[0];
            y = 2 * s[1] * random.nextDouble() - s[1] + v[1];
            z = 2 * s[2] * random.nextDouble() - s[2] + v[2];
        } else {
            x = v[0] + 0.2 - random.nextDouble() * 0.4;
            y = v[1] + 0.2 - random.nextDouble() * 0.4;
            z = v[2] + 0.2 - random.nextDouble() * 0.4;
        }
        // В JSON MTS скорости записаны с множителем 10.
        return rotate(frame, x / 10D, y / 10D, z / 10D);
    }

    private static Quaternionf fixedRotation(RandomSource random, MtsFxDefs.ParticleDef def, Quaternionf frame) {
        Quaternionf q = new Quaternionf(frame);
        if (def.rot != null) {
            rotateAngles(q, def.rot[0], def.rot[1], def.rot[2]);
        }
        if (def.rotationRandomness != null) {
            double[] rr = def.rotationRandomness;
            rotateAngles(q, (2 * random.nextDouble() - 1) * rr[0], (2 * random.nextDouble() - 1) * rr[1], (2 * random.nextDouble() - 1) * rr[2]);
        }
        return q;
    }

    private static void rotateAngles(Quaternionf q, double xDeg, double yDeg, double zDeg) {
        q.rotateY((float) Math.toRadians(yDeg)).rotateX((float) Math.toRadians(xDeg)).rotateZ((float) Math.toRadians(zDeg));
    }

    /** Поворот, переводящий локальную ось Y (наружу из поверхности в паке) в нормаль. */
    private static Quaternionf fromUp(Direction normal) {
        return new Quaternionf().rotationTo(new Vector3f(0, 1, 0), new Vector3f(normal.getStepX(), normal.getStepY(), normal.getStepZ()));
    }

    private static Vec3 rotate(Quaternionf q, double x, double y, double z) {
        Vector3f v = new Vector3f((float) x, (float) y, (float) z).rotate(q);
        return new Vec3(v.x(), v.y(), v.z());
    }

    /** Те же правила громкости, что в AEntityD_Definable.updateSounds из MTS. */
    private static void playSound(Minecraft mc, RandomSource random, MtsFxDefs.SoundDef def, double distance) {
        if (def.variations == null || def.variations.isEmpty()) return;
        if (def.maxDistance != def.minDistance && (distance >= def.maxDistance || distance < def.minDistance)) return;

        double volume;
        if (def.minDistanceVolume == 0 && def.middleDistanceVolume == 0 && def.maxDistanceVolume == 0) {
            double max = def.maxDistance != 0 ? def.maxDistance : DEFAULT_MAX_SOUND_DISTANCE;
            volume = distance > max ? 0 : (max - distance) / max;
        } else if (def.middleDistance != 0) {
            volume = distance < def.middleDistance
                    ? def.minDistanceVolume + (distance - def.minDistance) / (def.middleDistance - def.minDistance) * (def.middleDistanceVolume - def.minDistanceVolume)
                    : def.middleDistanceVolume + (distance - def.middleDistance) / (def.maxDistance - def.middleDistance) * (def.maxDistanceVolume - def.middleDistanceVolume);
        } else {
            volume = distance > def.maxDistance ? 0
                    : def.minDistanceVolume + (distance - def.minDistance) / (def.maxDistance - def.minDistance) * (def.maxDistanceVolume - def.minDistanceVolume);
        }
        if (volume <= 0) return;

        String name = def.variations.get(random.nextInt(def.variations.size())).toLowerCase(Locale.ROOT);
        ResourceLocation id = new ResourceLocation(name);
        if (mc.getSoundManager().getSoundEvent(id) == null) {
            if (MISSING_SOUNDS.add(id)) {
                LOG.warn("Нет звука {} в sounds.json: проверь, что он есть в сборке", id);
            }
            return;
        }
        // Источник относительно слушателя, как у звуков интерфейса: затухание и громкость посчитаны выше,
        // а стерео OpenAL всё равно не размещает в пространстве.
        mc.getSoundManager().play(new SimpleSoundInstance(id, SoundSource.BLOCKS,
                (float) Math.min(volume, 1D), 1F, random, false, 0, SoundInstance.Attenuation.NONE,
                0D, 0D, 0D, true));
    }
}
