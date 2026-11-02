import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import tech.squadmc.squadmcor.client.performance.CachedVehicleMesh;
import tech.squadmc.squadmcor.client.performance.VehicleMeshCache;

import java.lang.management.ManagementFactory;
import java.util.*;

/** Compares the optimized path against GeckoLib's actual default methods. No game/GL context. */
public final class VehicleMeshRegressionTest {
    private static final Reference REFERENCE = new Reference();
    private static final int LIGHT = 0xF000F0;
    private static final int OVERLAY = 655360;
    private static volatile double sink;

    private static final class Reference implements GeoRenderer<GeoAnimatable> {
        public GeoModel<GeoAnimatable> getGeoModel() { return null; }
        public GeoAnimatable getAnimatable() { return null; }
        public void fireCompileRenderLayersEvent() {}
        public boolean firePreRenderEvent(PoseStack p, BakedGeoModel m, MultiBufferSource b, float t, int l) { return true; }
        public void firePostRenderEvent(PoseStack p, BakedGeoModel m, MultiBufferSource b, float t, int l) {}
        public void updateAnimatedTextureFrame(GeoAnimatable a) {}
    }

    private static class Consumer implements VertexConsumer {
        final List<float[]> rows;
        double checksum;
        Consumer(boolean record) { rows = record ? new ArrayList<>() : null; }
        @Override
        public void vertex(float x, float y, float z, float r, float g, float b, float a,
                           float u, float v, int overlay, int light, float nx, float ny, float nz) {
            if (rows != null) rows.add(new float[]{x,y,z,r,g,b,a,u,v,overlay,light,nx,ny,nz});
            checksum += x * .3 + y * .5 + z * .7 + u + v + nx + ny + nz + r + g + b + a + light + overlay;
        }
        public VertexConsumer vertex(double x, double y, double z) { throw new AssertionError(); }
        public VertexConsumer color(int r, int g, int b, int a) { throw new AssertionError(); }
        public VertexConsumer uv(float u, float v) { throw new AssertionError(); }
        public VertexConsumer overlayCoords(int u, int v) { throw new AssertionError(); }
        public VertexConsumer uv2(int u, int v) { throw new AssertionError(); }
        public VertexConsumer normal(float x, float y, float z) { throw new AssertionError(); }
        public void endVertex() { throw new AssertionError(); }
        public void defaultColor(int r, int g, int b, int a) {}
        public void unsetDefaultColor() {}
    }

    private static GeoCube cube(Random random, int index) {
        GeoQuad[] faces = new GeoQuad[6];
        Direction[] directions = Direction.values();
        for (int face = 0; face < 6; face++) {
            if ((index + face) % 11 == 0) continue;
            GeoVertex[] vertices = new GeoVertex[4];
            for (int vertex = 0; vertex < 4; vertex++) {
                vertices[vertex] = new GeoVertex(new Vector3f(random.nextFloat()*4-2,
                        random.nextFloat()*4-2, random.nextFloat()*4-2), random.nextFloat(), random.nextFloat());
            }
            Vector3f normal = directions[face].step();
            if (index % 2 == 0) normal.x = -normal.x;
            faces[face] = new GeoQuad(vertices, normal, directions[face]);
        }
        Vec3 size = new Vec3(index % 7 == 0 ? 0 : 2, index % 7 == 1 ? 0 : 3, index % 7 == 2 ? 0 : 4);
        return new GeoCube(faces, new Vec3(17.3, -10.2, 7.5),
                index % 4 == 0 ? Vec3.ZERO : new Vec3(random.nextDouble()*6, random.nextDouble()*6, random.nextDouble()*6),
                size, 0, index % 2 == 0);
    }

    private static PoseStack pose(Random random, int index) {
        PoseStack pose = new PoseStack();
        pose.translate(random.nextDouble()*200-100, random.nextDouble()*200-100, random.nextDouble()*200-100);
        pose.mulPose(new Quaternionf().rotationXYZ(random.nextFloat()*6,random.nextFloat()*6,random.nextFloat()*6));
        pose.scale(.5F+random.nextFloat()*2, .5F+random.nextFloat()*2, .5F+random.nextFloat()*2);
        if (index % 3 == 0) {
            // Valid mirrored affine transform. Vanilla PoseStack.scale with a
            // negative non-uniform determinant corrupts its normal matrix.
            pose.last().pose().scale(-1, 1, 1);
            pose.last().normal().scale(-1, 1, 1);
        }
        return pose;
    }

    private static void original(PoseStack pose, GeoBone bone, Consumer consumer) {
        REFERENCE.renderCubesOfBone(pose, bone, consumer, LIGHT, OVERLAY, .2F, .4F, .6F, .8F);
    }

    public static void main(String[] args) {
        Random random = new Random(719);
        int compared = 0;
        float maxError = 0;
        for (int test = 0; test < 128; test++) {
            GeoBone bone = new GeoBone(null, "test", false, 0.0, false, false);
            for (int c = 0; c < 8; c++) bone.getCubes().add(cube(random, test*8+c));
            CachedVehicleMesh mesh = new CachedVehicleMesh(bone.getCubes());
            PoseStack transform = pose(random, test);
            Consumer before = new Consumer(true), after = new Consumer(true);
            original(transform, bone, before);
            mesh.render(transform, after, LIGHT, OVERLAY, .2F, .4F, .6F, .8F);
            if (before.rows.size() != after.rows.size()) throw new AssertionError("Vertex count changed");
            for (int v = 0; v < before.rows.size(); v++) {
                float[] a = before.rows.get(v), b = after.rows.get(v);
                for (int field = 0; field < a.length; field++) {
                    float error = Math.abs(a[field]-b[field]);
                    maxError = Math.max(maxError, error);
                    float tolerance = field < 3 ? 0.0002F : field >= 11 ? 0.00002F : 0;
                    if (!Float.isFinite(b[field]) || error > tolerance) {
                        throw new AssertionError("Output mismatch test="+test+" vertex="+v+" field="+field+": "+a[field]+" / "+b[field]);
                    }
                }
                compared++;
            }
        }
        GeoBone bone = new GeoBone(null,"cache",false,0.0,false,false);
        bone.getCubes().add(cube(random, 1));
        VehicleMeshCache cache = new VehicleMeshCache();
        BakedGeoModel first = new BakedGeoModel(List.of(bone), null);
        cache.useModel(first);
        CachedVehicleMesh a = cache.get(bone);
        cache.useModel(first);
        if (cache.get(bone) != a) throw new AssertionError("Same model rebuilt");
        cache.useModel(new BakedGeoModel(List.of(bone), null));
        if (cache.get(bone) == a) throw new AssertionError("Resource reload retained old mesh");
        if (!bone.getCubes().get(0).quads()[0].normal().isFinite()) throw new AssertionError("Original model damaged");
        System.out.println("PASS: "+compared+" vertices compared with GeckoLib, max error="+maxError+"; cache reuse/reload");
        if (args.length > 0 && args[0].equals("--benchmark")) benchmark();
    }

    private static void benchmark() {
        Random random = new Random(143);
        GeoBone bone = new GeoBone(null,"benchmark",false,0.0,false,false);
        for (int i=0;i<256;i++) bone.getCubes().add(cube(random,i));
        CachedVehicleMesh cached = new CachedVehicleMesh(bone.getCubes());
        Consumer consumer = new Consumer(false);
        PoseStack pose = pose(random, 0);
        for (int i=0;i<1600;i++) {
            original(pose,bone,consumer);
            cached.render(pose,consumer,LIGHT,OVERLAY,.2F,.4F,.6F,.8F);
        }
        var bean = (com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();
        bean.setThreadAllocatedMemoryEnabled(true);
        for (int pass=0;pass<4;pass++) {
            boolean fast = pass==1 || pass==2;
            long id = Thread.currentThread().getId();
            long bytes=bean.getThreadAllocatedBytes(id), start=System.nanoTime();
            for (int frame=0;frame<1800;frame++) {
                if (fast) cached.render(pose,consumer,LIGHT,OVERLAY,.2F,.4F,.6F,.8F);
                else original(pose,bone,consumer);
            }
            long elapsed=System.nanoTime()-start, allocated=bean.getThreadAllocatedBytes(id)-bytes;
            System.out.printf(Locale.ROOT,"%s: %.3f us/pass, %.1f bytes/pass%n",fast?"cached":"original",elapsed/1800.0/1000,allocated/1800.0);
        }
        sink=consumer.checksum;
        System.out.println("Synthetic CPU/allocations only; not an in-game FPS measurement.");
    }
}
