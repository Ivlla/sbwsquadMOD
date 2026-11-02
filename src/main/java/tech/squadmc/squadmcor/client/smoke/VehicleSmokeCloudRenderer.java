package tech.squadmc.squadmcor.client.smoke;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import tech.squadmc.squadmcor.smoke.VehicleSmokeEmitter;

/**
 * Перенесено из aasgranate (client/M18SmokeCloudRenderer) — алгоритм пуфов,
 * easing, цвета и альфа не изменены, чтобы дым техники визуально совпадал
 * с дымом ручных шашек. Отличия: сигнатура принимает VehicleSmokeEmitter
 * (рендерит любую сущность-эмиттер, не только конкретную), габариты берутся
 * из интерфейса (getSmokeRadius/getSmokeHeight), trail-ветка гранат не
 * переносится — она к дыму техники отношения не имеет.
 */
public final class VehicleSmokeCloudRenderer {

    private VehicleSmokeCloudRenderer() {}

    private static final int PUFF_COUNT = 260;

    private static final float PUFF_ALPHA = 0.30F;

    private static final float BASE_GREY = 0.58F;

    public static void render(VehicleSmokeEmitter emitter, float partialTick, PoseStack poseStack,
                              MultiBufferSource bufferSource, int packedLight) {

        float density = emitter.getSmokeDensity(partialTick);
        if (density <= 0.002F) return;

        float t = emitter.getSmokeTicks(partialTick);
        if (t < 0.0F) return;

        int blockLight = LightTexture.block(packedLight);
        int skyLight   = LightTexture.sky(packedLight);
        float lum = Math.max(blockLight, skyLight) / 15.0F;
        float tint = 1.0F - 0.10F * SmokeTimeOfDay.coolTint();   // было 0.25F
        float bright = (0.30F + 0.70F * lum) * SmokeTimeOfDay.ambient() * tint;

        Quaternionf camera = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        Vector3f right = camera.transform(new Vector3f(1.0F, 0.0F, 0.0F));
        Vector3f up    = camera.transform(new Vector3f(0.0F, 1.0F, 0.0F));

        VertexConsumer vc = bufferSource.getBuffer(SmokeRenderTypes.SMOKE);
        Matrix4f pose = poseStack.last().pose();

        float growth = emitter.getSmokeGrowth(partialTick);
        float inv = 1.0F - growth;
        float ease = 1.0F - inv * inv * inv;

        float radius = emitter.getSmokeRadius() * (0.12F + 0.88F * ease);
        float height = emitter.getSmokeHeight() * (0.15F + 0.85F * ease);

        SmokePuffCache.Puff[] puffs = SmokePuffCache.get((net.minecraft.world.entity.Entity) emitter, PUFF_COUNT);

        float intensity = emitter.getSmokeIntensity();
        int puffCount = Math.max(1, Math.round(PUFF_COUNT * intensity));

        for (int i = 0; i < puffCount; i++) {
            SmokePuffCache.Puff puff = puffs[i];
            float alpha = PUFF_ALPHA * puff.alphaFactor * density * intensity;
            if (alpha <= 0.002F) continue;
            float azimuth = puff.azimuth, rFrac = puff.rFrac, yFrac = puff.yFrac;
            float sizeVar = puff.sizeVar, spin = puff.spin;
            float p1 = puff.p1, p2 = puff.p2, p3 = puff.p3;
            float greyVar = puff.greyVar;

            float ang = azimuth + t * spin;
            float rr  = rFrac * radius * (0.92F + 0.08F * sin(t * 0.010F + p1));

            float px = cos(ang) * rr + sin(t * 0.017F + p2) * 0.10F * radius;
            float pz = sin(ang) * rr + cos(t * 0.013F + p3) * 0.10F * radius;
            float py = 0.10F + yFrac * height + sin(t * 0.011F + p1) * 0.06F * height;

            if (py < 0.05F) py = 0.05F;
            if (py > height) py = height;

            float puffR = sizeVar * radius * 0.62F * (0.9F + 0.1F * sin(t * 0.02F + p2));

            float grey = (BASE_GREY + greyVar) * bright;
            int cr = clamp255(grey * 255.0F);
            int cg = cr;
            int cb = cr;
            int ca = clamp255(alpha * 255.0F);
            if (ca <= 0) continue;

            SmokeBillboard.draw(vc, pose, right, up, px, py, pz, puffR, cr, cg, cb, ca);
        }
    }

    private static float sin(float a) { return (float) Math.sin(a); }
    private static float cos(float a) { return (float) Math.cos(a); }

    private static int clamp255(float v) {
        int i = (int) v;
        if (i < 0) return 0;
        return Math.min(i, 255);
    }
}
