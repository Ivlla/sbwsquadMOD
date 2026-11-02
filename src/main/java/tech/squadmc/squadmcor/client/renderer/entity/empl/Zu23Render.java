package tech.squadmc.squadmcor.client.renderer.entity.empl;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.entity.empl.Zu23Entity;
import tech.squadmc.squadmcor.client.model.empl.Zu23Model;

public class Zu23Render extends VehicleRenderer<Zu23Entity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("squadmc", "textures/entity/zu23.png");

    public Zu23Render(EntityRendererProvider.Context context) {
        super(context, new Zu23Model());
    }

    @Override
    public ResourceLocation getTextureLocation(Zu23Entity entity) {
        return TEXTURE;
    }
}
