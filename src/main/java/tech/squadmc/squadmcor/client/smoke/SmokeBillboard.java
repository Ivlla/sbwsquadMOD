package tech.squadmc.squadmcor.client.smoke;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Перенесено из aasgranate (client/SmokeBillboard) без изменений логики.
 * Два формата вершин: свой (POSITION_COLOR_TEX, восьмигранный диск в fsh)
 * и ванильный PARTICLE для режима шейдерпака.
 */
final class SmokeBillboard {
    /** true: рисуем текстурированный квад для шейдерпака (формат PARTICLE). Ставится каждый кадр. */
    static boolean shaderMode;
    /** Упакованный свет текущей сущности (для режима шейдерпака). */
    static int light = 15728880;

    private SmokeBillboard() {}

    static void draw(VertexConsumer vc, Matrix4f pose, Vector3f right, Vector3f up,
                     float x, float y, float z, float radius, int r, int g, int b, int a) {
        if (shaderMode) {
            particle(vc, pose, right, up, x, y, z, radius, -1, -1, r, g, b, a);
            particle(vc, pose, right, up, x, y, z, radius,  1, -1, r, g, b, a);
            particle(vc, pose, right, up, x, y, z, radius,  1,  1, r, g, b, a);
            particle(vc, pose, right, up, x, y, z, radius, -1,  1, r, g, b, a);
            return;
        }
        vertex(vc, pose, right, up, x, y, z, radius, -1, -1, r, g, b, a);
        vertex(vc, pose, right, up, x, y, z, radius,  1, -1, r, g, b, a);
        vertex(vc, pose, right, up, x, y, z, radius,  1,  1, r, g, b, a);
        vertex(vc, pose, right, up, x, y, z, radius, -1,  1, r, g, b, a);
    }

    private static void particle(VertexConsumer vc, Matrix4f pose, Vector3f right, Vector3f up,
                                 float x, float y, float z, float radius, float u, float v,
                                 int r, int g, int b, int a) {
        vc.vertex(pose, x + radius * (right.x * u + up.x * v),
                        y + radius * (right.y * u + up.y * v),
                        z + radius * (right.z * u + up.z * v))
                .uv((u + 1.0F) * 0.5F, (v + 1.0F) * 0.5F)
                .color(r, g, b, a)
                .uv2(light)
                .endVertex();
    }

    private static void vertex(VertexConsumer vc, Matrix4f pose, Vector3f right, Vector3f up,
                               float x, float y, float z, float radius, float u, float v,
                               int r, int g, int b, int a) {
        vc.vertex(pose, x + radius * (right.x * u + up.x * v),
                        y + radius * (right.y * u + up.y * v),
                        z + radius * (right.z * u + up.z * v))
                .color(r, g, b, a).uv(u, v).endVertex();
    }
}
