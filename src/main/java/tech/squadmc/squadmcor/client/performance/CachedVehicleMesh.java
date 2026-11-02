package tech.squadmc.squadmcor.client.performance;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.util.RenderUtils;

import java.util.List;

/** Static cube geometry in bone space. Bone animation is still applied every frame. */
public final class CachedVehicleMesh {
    private final float[] data;
    private final int[] vertexCounts;
    private final byte[] flatCubeMasks;
    private final Vector3f position = new Vector3f();
    private final Vector3f normal = new Vector3f();

    public CachedVehicleMesh(List<GeoCube> cubes) {
        int faces = 0;
        int values = 0;
        for (GeoCube cube : cubes) {
            for (GeoQuad quad : cube.quads()) {
                if (quad == null) continue;
                faces++;
                values += 3 + 5 * quad.vertices().length;
            }
        }
        data = new float[values];
        vertexCounts = new int[faces];
        flatCubeMasks = new byte[faces];
        PoseStack local = new PoseStack();
        Vector4f point = new Vector4f();
        int offset = 0;
        int face = 0;
        for (GeoCube cube : cubes) {
            local.pushPose();
            RenderUtils.translateToPivotPoint(local, cube);
            RenderUtils.rotateMatrixAroundCube(local, cube);
            RenderUtils.translateAwayFromPivotPoint(local, cube);
            Matrix4f transform = local.last().pose();
            Matrix3f normals = local.last().normal();
            int mask = 0;
            if (cube.size().y == 0 || cube.size().z == 0) mask |= 1;
            if (cube.size().x == 0 || cube.size().z == 0) mask |= 2;
            if (cube.size().x == 0 || cube.size().y == 0) mask |= 4;
            for (GeoQuad quad : cube.quads()) {
                if (quad == null) continue;
                vertexCounts[face] = quad.vertices().length;
                flatCubeMasks[face++] = (byte) mask;
                normals.transform(normal.set(quad.normal()));
                data[offset++] = normal.x;
                data[offset++] = normal.y;
                data[offset++] = normal.z;
                for (GeoVertex vertex : quad.vertices()) {
                    Vector3f source = vertex.position();
                    transform.transform(point.set(source.x, source.y, source.z, 1));
                    data[offset++] = point.x;
                    data[offset++] = point.y;
                    data[offset++] = point.z;
                    data[offset++] = vertex.texU();
                    data[offset++] = vertex.texV();
                }
            }
            local.popPose();
        }
    }

    /** Called on the render thread; scratch vectors never escape this method. */
    public void render(PoseStack poseStack, VertexConsumer buffer, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        Matrix4f transform = poseStack.last().pose();
        Matrix3f normals = poseStack.last().normal();
        int offset = 0;
        for (int face = 0; face < vertexCounts.length; face++) {
            normals.transform(data[offset], data[offset + 1], data[offset + 2], normal);
            offset += 3;
            int mask = flatCubeMasks[face];
            // GeckoLib fixes flat-cube normals AFTER the full world transform.
            if ((mask & 1) != 0 && normal.x < 0) normal.x = -normal.x;
            if ((mask & 2) != 0 && normal.y < 0) normal.y = -normal.y;
            if ((mask & 4) != 0 && normal.z < 0) normal.z = -normal.z;
            for (int vertex = 0; vertex < vertexCounts[face]; vertex++) {
                transform.transformPosition(data[offset], data[offset + 1], data[offset + 2], position);
                buffer.vertex(position.x, position.y, position.z, red, green, blue, alpha,
                        data[offset + 3], data[offset + 4], overlay, light, normal.x, normal.y, normal.z);
                offset += 5;
            }
        }
    }
}
