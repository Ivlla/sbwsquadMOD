package tech.squadmc.squadmcor.client.wreck;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.core.state.BoneSnapshot;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;
import tech.squadmc.squadmcor.entity.VehicleWreckEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Draws a wreck with its original vehicle's model, blackened and without wheels or tracks. Bones are
 * posed from their rest state because live vehicles of the same type share these bone objects.
 */
public class VehicleWreckRenderer extends EntityRenderer<VehicleWreckEntity> {

    private static final Pattern REMOVED_BONES = Pattern.compile(
            "wheel|whell|^wheR|track|gus|^obb|flare|watermask|laser|_dogtag", Pattern.CASE_INSENSITIVE);
    private static final float BURNT_SHADE = 0.1F;

    // Renderer instances are rebuilt on resource reload: do not retain old baked models globally.
    private final Map<String, Optional<Source>> sources = new HashMap<>();
    private final Vector3f quadNormal = new Vector3f();
    private final Vector4f quadPosition = new Vector4f();

    private record Source(BakedGeoModel model, ResourceLocation texture, float groundOffset) {
    }

    private interface QuadVisitor {
        void visit(GeoQuad quad, PoseStack.Pose pose);
    }

    public VehicleWreckRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(VehicleWreckEntity wreck, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
        Source source = sources.computeIfAbsent(wreck.source(), VehicleWreckRenderer::load).orElse(null);
        if (source != null) {
            poseStack.pushPose();
            // Same facing GeoEntityRenderer gives a vehicle: its models point along -Z.
            poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
            poseStack.mulPose(Axis.YP.rotationDegrees(180F));
            poseStack.translate(0F, source.groundOffset(), 0F);
            VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutoutNoCull(source.texture()));
            walk(source.model(), wreck.turretYaw(), wreck.barrelPitch(), wreck.turretLost(), poseStack,
                    (quad, pose) -> drawQuad(quad, pose, buffer, light));
            poseStack.popPose();
        }
        super.render(wreck, yaw, partialTick, poseStack, buffers, light);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Optional<Source> load(String id) {
        Minecraft mc = Minecraft.getInstance();
        EntityType<?> type = EntityType.byString(id).orElse(null);
        if (type == null || mc.level == null) return Optional.empty();
        Entity vehicle = type.create(mc.level);
        if (vehicle == null || !(mc.getEntityRenderDispatcher().getRenderer(vehicle) instanceof GeoEntityRenderer renderer)) {
            return Optional.empty();
        }
        GeoModel model = renderer.getGeoModel();
        BakedGeoModel baked = model.getBakedModel(model.getModelResource((software.bernie.geckolib.core.animatable.GeoAnimatable) vehicle));
        return Optional.of(new Source(baked, renderer.getTextureLocation(vehicle), -lowestPoint(baked)));
    }

    /** With the wheels gone, the hull's underside is what has to rest on the ground. */
    private static float lowestPoint(BakedGeoModel model) {
        float[] lowest = {Float.MAX_VALUE};
        Vector4f position = new Vector4f();
        walk(model, 0F, 0F, false, new PoseStack(), (quad, pose) -> {
            for (GeoVertex vertex : quad.vertices()) {
                pose.pose().transform(position.set(vertex.position(), 1F));
                lowest[0] = Math.min(lowest[0], position.y());
            }
        });
        return lowest[0] == Float.MAX_VALUE ? 0F : lowest[0];
    }

    private static void walk(BakedGeoModel model, float turretYaw, float barrelPitch, boolean turretLost, PoseStack stack, QuadVisitor visitor) {
        for (GeoBone bone : model.topLevelBones()) walkBone(bone, turretYaw, barrelPitch, turretLost, stack, visitor);
    }

    private static void walkBone(GeoBone bone, float turretYaw, float barrelPitch, boolean turretLost, PoseStack stack, QuadVisitor visitor) {
        String name = bone.getName();
        if (REMOVED_BONES.matcher(name).find() || (turretLost && name.equals("turret"))) return;

        BoneSnapshot rest = bone.getInitialSnapshot();
        float rotX = rest != null ? rest.getRotX() : bone.getRotX();
        float rotY = rest != null ? rest.getRotY() : bone.getRotY();
        float rotZ = rest != null ? rest.getRotZ() : bone.getRotZ();
        if (name.equals("turret")) rotY = turretYaw * Mth.DEG_TO_RAD;
        if (name.equals("barrel")) rotX = -barrelPitch * Mth.DEG_TO_RAD;

        stack.pushPose();
        RenderUtils.translateToPivotPoint(stack, bone);
        stack.mulPose(Axis.ZP.rotation(rotZ));
        stack.mulPose(Axis.YP.rotation(rotY));
        stack.mulPose(Axis.XP.rotation(rotX));
        RenderUtils.translateAwayFromPivotPoint(stack, bone);

        for (GeoCube cube : bone.getCubes()) {
            stack.pushPose();
            RenderUtils.translateToPivotPoint(stack, cube);
            RenderUtils.rotateMatrixAroundCube(stack, cube);
            RenderUtils.translateAwayFromPivotPoint(stack, cube);
            for (GeoQuad quad : cube.quads()) {
                if (quad != null) visitor.visit(quad, stack.last());
            }
            stack.popPose();
        }
        for (GeoBone child : bone.getChildBones()) walkBone(child, turretYaw, barrelPitch, turretLost, stack, visitor);
        stack.popPose();
    }

    private void drawQuad(GeoQuad quad, PoseStack.Pose pose, VertexConsumer buffer, int light) {
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        Vector3f normal = normalMatrix.transform(quadNormal.set(quad.normal()));
        Vector4f position = quadPosition;
        for (GeoVertex vertex : quad.vertices()) {
            matrix.transform(position.set(vertex.position(), 1F));
            buffer.vertex(position.x(), position.y(), position.z(), BURNT_SHADE, BURNT_SHADE, BURNT_SHADE, 1F,
                    vertex.texU(), vertex.texV(), OverlayTexture.NO_OVERLAY, light, normal.x(), normal.y(), normal.z());
        }
    }

    @Override
    public ResourceLocation getTextureLocation(VehicleWreckEntity wreck) {
        Source source = sources.computeIfAbsent(wreck.source(), VehicleWreckRenderer::load).orElse(null);
        return source != null ? source.texture() : new ResourceLocation("minecraft", "textures/misc/white.png");
    }
}
