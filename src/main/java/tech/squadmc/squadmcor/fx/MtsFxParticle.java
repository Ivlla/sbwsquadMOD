package tech.squadmc.squadmcor.fx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Перенос EntityParticle из MTS: те же формулы движения, масштаба, прозрачности, цвета и смены текстур.
 * Отличие одно: вместо движка рендера MTS — обычная частица Minecraft. КЛИЕНТ.
 */
@OnlyIn(Dist.CLIENT)
public class MtsFxParticle extends Particle {
    private final MtsFxDefs.ParticleDef def;
    private final TextureAtlasSprite[] sprites;
    @Nullable private final float[][] model;
    /** null = смотрит на камеру (PLAYER); иначе постоянный поворот (FIXED). */
    @Nullable private final Quaternionf fixedRotation;
    private final double initX, initY, initZ;
    private final boolean breakParticle;
    private final float breakU, breakV;

    private int textureIndex;
    private int textureDelayIndex;
    private float timeOfNextTexture;

    @Nullable private final int[] colors;
    private int colorIndex;
    private int colorDelayIndex;
    private int timeOfCurrentColor;
    private int timeOfNextColor;
    private float r0, g0, b0, r1, g1, b1;
    private boolean colorAnimated;
    private boolean touching;

    public MtsFxParticle(ClientLevel level, Vec3 pos, Vec3 motion, MtsFxDefs.ParticleDef def,
                         TextureAtlasSprite[] sprites, @Nullable Quaternionf fixedRotation,
                         @Nullable Integer blockColor) {
        super(level, pos.x, pos.y, pos.z);
        this.def = def;
        this.sprites = sprites;
        this.fixedRotation = fixedRotation;
        this.model = def.model != null ? MtsFxLibrary.model(def.model) : null;
        this.breakParticle = def.isBreak();
        this.breakU = this.random.nextFloat() * 3F;
        this.breakV = this.random.nextFloat() * 3F;
        this.lifetime = maxAge();
        this.xd = motion.x;
        this.yd = motion.y;
        this.zd = motion.z;
        this.initX = motion.x;
        this.initY = motion.y;
        this.initZ = motion.z;
        this.gravity = 0F;
        this.hasPhysics = !def.ignoreCollision;
        setSize(def.hitboxSize, def.hitboxSize);
        setPos(pos.x, pos.y, pos.z);
        this.xo = pos.x;
        this.yo = pos.y;
        this.zo = pos.z;

        if (def.randomTexture && sprites.length > 0) {
            this.textureIndex = this.random.nextInt(sprites.length);
        }
        this.timeOfNextTexture = hasDelays(def.textureDelays) ? def.textureDelays.get(0) : this.lifetime;

        if (blockColor != null) {
            setStatic(blockColor);
            this.colors = null;
        } else if (def.color != null) {
            this.colors = null;
            if (def.toColor != null) {
                setFade(parse(def.color), parse(def.toColor));
                this.timeOfNextColor = this.lifetime;
            } else {
                setStatic(parse(def.color));
            }
        } else if (def.colorList != null && !def.colorList.isEmpty()) {
            this.colors = def.colorList.stream().mapToInt(MtsFxParticle::parse).toArray();
            if (def.randomColor) {
                this.colorIndex = this.random.nextInt(this.colors.length);
            }
            setFade(this.colors[this.colorIndex], this.colors[(this.colorIndex + 1) % this.colors.length]);
            this.timeOfNextColor = hasDelays(def.colorDelays) ? def.colorDelays.get(0) : this.lifetime;
        } else {
            this.colors = null;
            setStatic(0xFFFFFF);
        }
    }

    private static boolean hasDelays(@Nullable List<Integer> delays) {
        return delays != null && !delays.isEmpty();
    }

    private static int parse(String hex) {
        return Integer.parseInt(hex, 16);
    }

    private int maxAge() {
        if (def.duration != 0) {
            if (def.durationRandomness != 0) {
                return Math.max(1, def.duration + (int) (def.durationRandomness * 2 * this.random.nextFloat() - def.durationRandomness));
            }
            return def.duration;
        }
        if (breakParticle) {
            return (int) (4.0D / (Math.random() * 0.9D + 0.1D));
        }
        return (int) (8.0D / (Math.random() * 0.8D + 0.2D));
    }

    private void setStatic(int rgb) {
        setFade(rgb, rgb);
        this.colorAnimated = false;
    }

    private void setFade(int from, int to) {
        this.r0 = (from >> 16 & 255) / 255F;
        this.g0 = (from >> 8 & 255) / 255F;
        this.b0 = (from & 255) / 255F;
        this.r1 = (to >> 16 & 255) / 255F;
        this.g1 = (to >> 8 & 255) / 255F;
        this.b1 = (to & 255) / 255F;
        this.colorAnimated = true;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime - 1) {
            remove();
            return;
        }

        if (!def.stopsOnGround || !this.touching) {
            if (def.movementDuration != 0 && this.age <= def.movementDuration) {
                float factor = (def.movementDuration - this.age) / (float) def.movementDuration;
                this.xd = limit(this.xd, Math.abs(initX * factor));
                this.yd = limit(this.yd, Math.abs(initY * factor));
                this.zd = limit(this.zd, Math.abs(initZ * factor));
            }

            if (def.movementVelocity != null) {
                this.xd += def.movementVelocity[0];
                this.yd += def.movementVelocity[1];
                this.zd += def.movementVelocity[2];
            } else if (breakParticle) {
                if (this.touching) {
                    this.xd = this.yd = this.zd = 0;
                } else {
                    this.xd *= 0.98;
                    this.yd = this.yd * 0.98 - 0.04;
                    this.zd *= 0.98;
                }
            }

            if (def.terminalVelocity != null) {
                this.xd = limit(this.xd, def.terminalVelocity[0]);
                this.yd = limit(this.yd, def.terminalVelocity[1]);
                this.zd = limit(this.zd, def.terminalVelocity[2]);
            }

            if (this.hasPhysics) {
                move(this.xd, this.yd, this.zd);
                this.touching = this.onGround;
            } else {
                setPos(this.x + this.xd, this.y + this.yd, this.z + this.zd);
            }
        }

        if (sprites.length > 1 && this.timeOfNextTexture <= this.age) {
            this.textureIndex = (this.textureIndex + 1) % sprites.length;
            if (hasDelays(def.textureDelays)) {
                this.textureDelayIndex = (this.textureDelayIndex + 1) % def.textureDelays.size();
                this.timeOfNextTexture += def.textureDelays.get(this.textureDelayIndex);
            } else {
                this.timeOfNextTexture += this.lifetime;
            }
        }

        if (this.colors != null && hasDelays(def.colorDelays) && this.timeOfNextColor == this.age) {
            this.colorIndex = (this.colorIndex + 1) % this.colors.length;
            setFade(this.colors[this.colorIndex], this.colors[(this.colorIndex + 1) % this.colors.length]);
            this.colorDelayIndex = (this.colorDelayIndex + 1) % def.colorDelays.size();
            this.timeOfCurrentColor = this.timeOfNextColor;
            this.timeOfNextColor += def.colorDelays.get(this.colorDelayIndex);
        }
    }

    private static double limit(double value, double max) {
        return Math.abs(value) > max ? Math.signum(value) * max : value;
    }

    private float currentAlpha(float partialTicks) {
        float a;
        if (def.toTransparency != 0) {
            a = Mth.clamp(def.transparency + (def.toTransparency - def.transparency) * (this.age + partialTicks) / this.lifetime, 0F, 1F);
        } else {
            a = def.transparency != 0 ? def.transparency : 1F;
        }
        if (def.fadeInTransparencyTime > 0 && def.fadeInTransparencyTime >= this.age) {
            a *= this.age / (float) def.fadeInTransparencyTime;
        }
        if (def.fadeOutTransparencyTime > this.lifetime - this.age) {
            a *= (this.lifetime - this.age) / (float) def.fadeOutTransparencyTime;
        }
        if (def.daytimeReductionFactor != 0) {
            a *= 1F - def.daytimeReductionFactor * brightness();
        }
        return Mth.clamp(a, 0F, 1F);
    }

    /** То же, что WrapperWorld.getLightBrightness в MTS. */
    private float brightness() {
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        float darken = 15F * this.level.getSkyDarken() / 11F;
        float sun = (this.level.getBrightness(LightLayer.SKY, pos) - darken) / 15F;
        float block = this.level.getBrightness(LightLayer.BLOCK, pos) / 15F;
        return Math.max(sun, block);
    }

    private float currentScale(float partialTicks) {
        float s;
        if (def.toScale != 0) {
            s = def.scale + (def.toScale - def.scale) * (this.age + partialTicks) / this.lifetime;
        } else {
            s = def.scale != 0 ? def.scale : 1F;
        }
        if (def.fadeInScaleTime > 0 && def.fadeInScaleTime >= this.age) {
            s *= 1F - (def.fadeInScaleTime - this.age) / (float) def.fadeInScaleTime;
        }
        if (def.fadeOutScaleTime > this.lifetime - this.age) {
            s *= (this.lifetime - this.age) / (float) def.fadeOutScaleTime;
        }
        return s;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (sprites.length == 0) return;
        TextureAtlasSprite sprite = sprites[Math.min(this.textureIndex, sprites.length - 1)];

        float t = 0F;
        if (this.colorAnimated && this.timeOfNextColor != this.timeOfCurrentColor) {
            t = Mth.clamp((this.age + partialTicks - this.timeOfCurrentColor) / (this.timeOfNextColor - this.timeOfCurrentColor), 0F, 1F);
        }
        float r = Mth.lerp(t, r0, r1);
        float g = Mth.lerp(t, g0, g1);
        float b = Mth.lerp(t, b0, b1);
        float a = breakParticle ? 1F : currentAlpha(partialTicks);
        float scale = currentScale(partialTicks);
        int light = getLightColor(partialTicks);

        Vec3 cam = camera.getPosition();
        float px = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cam.x);
        float py = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cam.y);
        float pz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cam.z);
        Quaternionf rotation = this.fixedRotation != null ? this.fixedRotation : camera.rotation();

        if (this.model != null) {
            for (float[] face : this.model) {
                for (int i = 0; i < 4; i++) {
                    int o = i * 5;
                    vertex(buffer, rotation, px, py, pz, face[o] * scale, face[o + 1] * scale, face[o + 2] * scale,
                            sprite.getU(face[o + 3] * 16F), sprite.getV(face[o + 4] * 16F), r, g, b, a, light);
                }
            }
            return;
        }

        // Спрайт MTS занимает от -0.5 до 0.5, то есть scale — это полная ширина.
        float half = scale * 0.5F;
        float u0, u1, v0, v1;
        if (breakParticle) {
            // Кусочек текстуры блока, как у ванильной TerrainParticle.
            u0 = sprite.getU((this.breakU + 1F) / 4F * 16F);
            u1 = sprite.getU(this.breakU / 4F * 16F);
            v0 = sprite.getV(this.breakV / 4F * 16F);
            v1 = sprite.getV((this.breakV + 1F) / 4F * 16F);
        } else {
            u0 = sprite.getU0();
            u1 = sprite.getU1();
            v0 = sprite.getV0();
            v1 = sprite.getV1();
        }
        vertex(buffer, rotation, px, py, pz, -half, -half, 0F, u1, v1, r, g, b, a, light);
        vertex(buffer, rotation, px, py, pz, -half, half, 0F, u1, v0, r, g, b, a, light);
        vertex(buffer, rotation, px, py, pz, half, half, 0F, u0, v0, r, g, b, a, light);
        vertex(buffer, rotation, px, py, pz, half, -half, 0F, u0, v1, r, g, b, a, light);
    }

    private static void vertex(VertexConsumer buffer, Quaternionf rotation, float px, float py, float pz,
                               float x, float y, float z, float u, float v,
                               float r, float g, float b, float a, int light) {
        Vector3f corner = new Vector3f(x, y, z).rotate(rotation);
        buffer.vertex(px + corner.x(), py + corner.y(), pz + corner.z())
                .uv(u, v).color(r, g, b, a).uv2(light).endVertex();
    }

    @Override
    protected int getLightColor(float partialTicks) {
        return def.isBright ? 0xF000F0 : super.getLightColor(partialTicks);
    }

    // Вспышка шириной в сотни блоков не должна пропадать, когда её центр вне экрана.
    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        if (breakParticle) return ParticleRenderType.TERRAIN_SHEET;
        return def.isBlended ? MtsFxRenderTypes.ADDITIVE : MtsFxRenderTypes.TRANSLUCENT;
    }
}
