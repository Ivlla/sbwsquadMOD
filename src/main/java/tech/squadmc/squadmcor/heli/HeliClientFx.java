package tech.squadmc.squadmcor.heli;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.fx.MtsFxClient;
import tech.squadmc.squadmcor.fx.MtsFxDefs;
import tech.squadmc.squadmcor.fx.MtsFxParticle;
import tech.squadmc.squadmcor.fx.MtsFxPreset;
import tech.squadmc.squadmcor.heli.client.EngineKeyHandler;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.squadmc;

import java.util.ArrayList;
import java.util.List;

/** Клиентские эффекты: дымный след НУРСа, пыль и брызги от винта, подсказка запуска двигателя. */
@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public final class HeliClientFx {
    private static final double DOWNWASH_HEIGHT = 16.0;
    private static final double DOWNWASH_RANGE = 128.0;
    private static final int TRAIL_MAX_PER_TICK = 5;
    private static final double TRAIL_STEP = 3.0;

    private static final MtsFxDefs.ParticleDef DUST = downwashDef(0.32F, 2.2F, 7.0F);
    private static final MtsFxDefs.ParticleDef SPRAY = downwashDef(0.45F, 1.4F, 5.0F);

    private HeliClientFx() {}

    private static MtsFxDefs.ParticleDef downwashDef(float alpha, float scale, float toScale) {
        MtsFxDefs.ParticleDef def = new MtsFxDefs.ParticleDef();
        def.type = "GENERIC";
        def.spawningOrientation = "WORLD";
        def.renderingOrientation = "PLAYER";
        def.ignoreCollision = true;
        def.randomTexture = true;
        def.quantity = 1;
        def.duration = 32;
        def.durationRandomness = 10;
        def.transparency = alpha;
        def.toTransparency = 0.001F;
        def.scale = scale;
        def.toScale = toScale;
        def.hitboxSize = 0.2F;
        List<String> textures = new ArrayList<>();
        for (int i = 0; i <= 11; i++) textures.add("squadmc:mts/core/big_smoke_" + i);
        def.textures = textures;
        return def;
    }

    /** Дымный след одной ракеты: точки вдоль пройденного за тик пути. */
    public static void rocketTrail(Entity rocket) {
        if (rocket.tickCount < 2) return;
        Vec3 from = new Vec3(rocket.xo, rocket.yo, rocket.zo);
        Vec3 to = rocket.position();
        double length = from.distanceTo(to);
        int points = Mth.clamp((int) (length / TRAIL_STEP), 1, TRAIL_MAX_PER_TICK);
        for (int i = 0; i < points; i++) {
            MtsFxClient.play(MtsFxPreset.ROCKET_TRAIL, false, from.lerp(to, (double) i / points), Direction.UP);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof HeliVehicleBase heli && EngineControl.isHelicopter(heli) && heli.distanceToSqr(mc.player) < DOWNWASH_RANGE * DOWNWASH_RANGE) {
                downwash(mc, mc.level, heli);
            }
        }
        engineHint(mc);
    }

    /** Пыль или брызги под винтом. Чем ниже вертолёт и выше обороты, тем их больше. */
    private static void downwash(Minecraft mc, ClientLevel level, HeliVehicleBase heli) {
        ParticleStatus status = mc.options.particles().get();
        if (status == ParticleStatus.MINIMAL) return;
        float rotor = Mth.clamp(Math.abs(heli.getPower()) / 0.6F, 0F, 1F);
        if (rotor < 0.15F) return;

        BlockPos.MutableBlockPos cursor = BlockPos.containing(heli.getX(), heli.getY(), heli.getZ()).mutable();
        double height = -1;
        for (int i = 0; i <= DOWNWASH_HEIGHT; i++) {
            if (!level.isEmptyBlock(cursor)) {
                height = heli.getY() - (cursor.getY() + 1);
                break;
            }
            cursor.move(Direction.DOWN);
        }
        if (height < 0) return;

        double strength = rotor * (1.0 - Math.max(0, height) / DOWNWASH_HEIGHT);
        double radius = heli.rotorRadius();
        int count = Mth.ceil(strength * radius * (status == ParticleStatus.ALL ? 0.8 : 0.35));
        BlockState ground = level.getBlockState(cursor);
        boolean water = ground.getFluidState().is(FluidTags.WATER);
        int color = water ? 0xE8F0F4 : lighten(ground.getMapColor(level, cursor).col);
        MtsFxDefs.ParticleDef def = water ? SPRAY : DUST;
        TextureAtlasSprite[] sprites = MtsFxClient.spritesFor(mc, def);

        for (int i = 0; i < count; i++) {
            double angle = level.random.nextDouble() * Mth.TWO_PI;
            double distance = radius * (0.35 + 0.65 * level.random.nextDouble());
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            Vec3 pos = new Vec3(heli.getX() + dx * distance, cursor.getY() + 1.2, heli.getZ() + dz * distance);
            double push = 0.18 + 0.32 * strength;
            Vec3 motion = new Vec3(dx * push, 0.02 + 0.05 * level.random.nextDouble(), dz * push);
            mc.particleEngine.add(new MtsFxParticle(level, pos, motion, def, sprites, null, color));
        }
    }

    private static int lighten(int rgb) {
        int r = (rgb >> 16 & 255) + 255 >> 1;
        int g = (rgb >> 8 & 255) + 255 >> 1;
        int b = (rgb & 255) + 255 >> 1;
        return r << 16 | g << 8 | b;
    }

    private static void engineHint(Minecraft mc) {
        if (mc.screen != null || !(mc.player.getVehicle() instanceof com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity heli)
                || heli.getFirstPassenger() != mc.player || !EngineControl.appliesTo(heli)) return;
        byte state = EngineControl.state(heli);
        if (state == EngineControl.OFF) {
            mc.gui.setOverlayMessage(Component.translatable("message.squadmc.heli_engine_hold",
                    HeliKeybinds.ENGINE_TOGGLE.getTranslatedKeyMessage()), false);
        } else if (state == EngineControl.STARTING) {
            int percent = Math.round(EngineKeyHandler.startProgress(heli, 0F) * 100F);
            mc.gui.setOverlayMessage(Component.translatable("message.squadmc.heli_engine_starting", percent), false);
        }
    }
}
