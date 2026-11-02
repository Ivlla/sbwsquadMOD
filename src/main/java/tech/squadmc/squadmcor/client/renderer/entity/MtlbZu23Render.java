package tech.squadmc.squadmcor.client.renderer.entity;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.entity.MtlbZu23Entity;
import tech.squadmc.squadmcor.client.model.MtlbZu23Model;

public class MtlbZu23Render extends VehicleRenderer<MtlbZu23Entity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("squadmc", "textures/entity/mtlb_zu23.png");

    public MtlbZu23Render(EntityRendererProvider.Context context) {
        super(context, new MtlbZu23Model());
    }

    @Override
    public ResourceLocation getTextureLocation(MtlbZu23Entity entity) {
        return TEXTURE;
    }
}
