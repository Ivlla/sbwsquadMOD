package tech.squadmc.squadmcor.client.smoke;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.smoke.VehicleSmokeCloudEntity;
import tech.squadmc.squadmcor.smoke.VehicleSmokeEmitter;
import tech.squadmc.squadmcor.squadmc;

/**
 * Перенесено из aasgranate (client/SmokeStageRenderer) — та же структура
 * стадий и тот же пайплайн; перебирает сущности squadmc, реализующие
 * VehicleSmokeEmitter. Ветки искр/шлейфа гранат не переносятся.
 */
@Mod.EventBusSubscriber(modid = squadmc.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class VehicleSmokeStageRenderer {

    private static final Set<Entity> CANDIDATES = new LinkedHashSet<>();
    private static final List<Entity> VISIBLE = new ArrayList<>();
    private static final MultiBufferSource.BufferSource SHADER_BUFFER =
            MultiBufferSource.immediate(new BufferBuilder(256 * 1024));
    private static ClientLevel trackedLevel;

    private VehicleSmokeStageRenderer() {}

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel() == trackedLevel && isSmokeEntity(event.getEntity()))
            CANDIDATES.add(event.getEntity());
    }

    @SubscribeEvent
    public static void onLeave(EntityLeaveLevelEvent event) {
        CANDIDATES.remove(event.getEntity());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            trackLevel(Minecraft.getInstance().level);
    }

    private static void trackLevel(ClientLevel level) {
        if (level == trackedLevel) return;
        CANDIDATES.clear();
        VISIBLE.clear();
        SmokePipeline.close();
        trackedLevel = level;
        if (level != null)
            for (Entity entity : level.entitiesForRendering())
                if (isSmokeEntity(entity)) CANDIDATES.add(entity);
    }

    private static boolean isSmokeEntity(Entity entity) {
        return entity instanceof VehicleSmokeCloudEntity;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            prepare(event);
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            SmokePipeline.afterParticles();
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            SmokePipeline.afterLevel();
        }
    }

    private static void prepare(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        trackLevel(mc.level);
        if (mc.level == null || !SmokeShaders.ready()) return;

        VISIBLE.clear();
        float partial = event.getPartialTick();
        for (Entity entity : CANDIDATES) {
            if (entity.isRemoved()) continue;
            VehicleSmokeEmitter emitter = (VehicleSmokeEmitter) entity;
            boolean smoking = emitter.isSmoking() && emitter.getSmokeDensity(partial) > 0.002F;
            if (smoking && event.getFrustum().isVisible(bounds(entity, emitter, partial)))
                VISIBLE.add(entity);
        }
        if (VISIBLE.isEmpty()) return;

        PoseStack poses = event.getPoseStack();
        Vec3 camera = event.getCamera().getPosition();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

        boolean shaders = IrisCompat.shadersActive();
        SmokeBillboard.shaderMode = shaders;
        if (shaders) SmokePuffTexture.ensure();
        BufferBuilder builder = shaders ? null : SmokePipeline.begin(event.getProjectionMatrix());

        SmokeTimeOfDay.update(mc.level, partial);
        if (shaders) SmokeTimeOfDay.neutral();

        MultiBufferSource smoke = shaders
                ? type -> SHADER_BUFFER.getBuffer(SmokeRenderTypes.SMOKE_SHADERS)
                : type -> builder;

        for (Entity entity : VISIBLE) {
            poses.pushPose();
            try {
                poses.translate(
                        Mth.lerp(partial, entity.xo, entity.getX()) - camera.x,
                        Mth.lerp(partial, entity.yo, entity.getY()) - camera.y,
                        Mth.lerp(partial, entity.zo, entity.getZ()) - camera.z);
                int light = dispatcher.getPackedLightCoords(entity, partial);
                SmokeBillboard.light = light;
                VehicleSmokeCloudRenderer.render((VehicleSmokeEmitter) entity, partial, poses, smoke, light);
            } finally {
                poses.popPose();
            }
        }

        if (shaders) SHADER_BUFFER.endBatch(SmokeRenderTypes.SMOKE_SHADERS);
        else SmokePipeline.upload();
    }

    private static AABB bounds(Entity entity, VehicleSmokeEmitter emitter, float partial) {
        double x = Mth.lerp(partial, entity.xo, entity.getX());
        double y = Mth.lerp(partial, entity.yo, entity.getY());
        double z = Mth.lerp(partial, entity.zo, entity.getZ());
        double radius = emitter.getSmokeRadius();
        double height = emitter.getSmokeHeight();
        double horizontal = radius * 1.85;
        double vertical = radius * 0.70;
        return new AABB(x - horizontal, y - vertical, z - horizontal,
                        x + horizontal, y + height + vertical, z + horizontal);
    }
}
