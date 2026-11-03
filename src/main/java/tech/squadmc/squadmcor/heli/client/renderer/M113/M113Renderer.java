package tech.squadmc.squadmcor.heli.client.renderer.M113;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.model.M113.M113Model;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;
import tech.squadmc.squadmcor.heli.entity.vehicle.M113.M113Entity;

public class M113Renderer extends VehicleRenderer<M113Entity> {
    public M113Renderer(EntityRendererProvider.Context context) {
        super(context, new M113Model());
    }

    @Override
    public ResourceLocation getTextureLocation(M113Entity entity) {
        return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture());
    }
}
