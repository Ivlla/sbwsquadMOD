package tech.squadmc.squadmcor.client.renderer.entity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.DingoModel;
import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import tech.squadmc.squadmcor.client.skin.VehicleSkinTexture;
import tech.squadmc.squadmcor.entity.DingoEntity;
import tech.squadmc.squadmcor.squadmc;

public class DingoRenderer extends OptimizedVehicleRenderer<DingoEntity> {
    private final ResourceLocation fallback;
    public DingoRenderer(EntityRendererProvider.Context context, String geometry, String texture) {
        super(context, new DingoModel(geometry));
        this.fallback = new ResourceLocation(squadmc.MODID, "textures/entity/" + texture + ".png");
    }
    @Override public RenderType getRenderType(DingoEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) { return RenderType.entityCutout(getTextureLocation(entity)); }
    @Override public ResourceLocation getTextureLocation(DingoEntity entity) { return VehicleSkinTexture.resolve(entity, fallback); }
}
