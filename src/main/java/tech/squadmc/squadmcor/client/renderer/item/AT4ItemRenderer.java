package tech.squadmc.squadmcor.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tech.squadmc.squadmcor.client.model.item.AT4ItemModel;
import tech.squadmc.squadmcor.item.AT4Item;

public class AT4ItemRenderer extends GeoItemRenderer<AT4Item> {

    private ItemDisplayContext currentTransformType = ItemDisplayContext.NONE;

    public AT4ItemRenderer() {
        super(new AT4ItemModel());
    }

    @Override
    public RenderType getRenderType(AT4Item animatable, net.minecraft.resources.ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.currentTransformType = transformType;
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void preRender(PoseStack poseStack, AT4Item animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        // 1. РЎРєСЂС‹РІР°РµРј СЂСѓРєРё РѕС‚ 1-РіРѕ Р»РёС†Р°, РµСЃР»Рё РјС‹ РќР• СЃРјРѕС‚СЂРёРј РёР· РіР»Р°Р·
        boolean isFirstPerson = (this.currentTransformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                this.currentTransformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND);

        this.getGeoModel().getBone("rightarm").ifPresent(bone -> bone.setHidden(!isFirstPerson));
        this.getGeoModel().getBone("leftarm").ifPresent(bone -> bone.setHidden(!isFirstPerson));

        // 2. РќР°СЃС‚СЂР°РёРІР°РµРј РјР°СЃС€С‚Р°Р± Рё РїРѕР·РёС†РёСЋ РѕСЂСѓР¶РёСЏ РІ Р·Р°РІРёСЃРёРјРѕСЃС‚Рё РѕС‚ СЂРµР¶РёРјР° РѕС‚РѕР±СЂР°Р¶РµРЅРёСЏ
        switch (this.currentTransformType) {
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                // РЈРјРµРЅСЊС€Р°РµРј РіРёРіР°РЅС‚СЃРєСѓСЋ С‚СЂСѓР±Сѓ Рё РєР»Р°РґРµРј РЅР° РїР»РµС‡Рѕ
                poseStack.scale(0.35F, 0.35F, 0.35F);
                poseStack.translate(0.05D, 0.25D, -0.2D);
                poseStack.mulPose(Axis.XP.rotationDegrees(-10.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                break;

            case GROUND:
                // РќР° Р·РµРјР»Рµ: СѓРјРµРЅСЊС€Р°РµРј Рё РєР»Р°РґРµРј РіРѕСЂРёР·РѕРЅС‚Р°Р»СЊРЅРѕ
                poseStack.scale(0.28F, 0.28F, 0.28F);
                poseStack.translate(0.0D, 0.3D, 0.0D);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                break;

            case GUI:
            case FIXED:
                // Р’ СЂР°РјРєРµ РїСЂРµРґРјРµС‚Р° / РёРЅРІРµРЅС‚Р°СЂРµ
                poseStack.scale(0.25F, 0.25F, 0.25F);
                poseStack.translate(0.0D, -0.2D, 0.0D);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
                break;

            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
            default:
                // Р’РёРґ РѕС‚ РїРµСЂРІРѕРіРѕ Р»РёС†Р° (СЂР°Р·РјРµСЂ РїРѕ СѓРјРѕР»С‡Р°РЅРёСЋ РёР· Р°РЅРёРјР°С†РёРё PB)
                poseStack.scale(0.45F, 0.45F, 0.45F);
                break;
        }
    }
}
