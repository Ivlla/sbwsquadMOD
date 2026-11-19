package tech.squadmc.squadmcor.heli.client.renderer.M113;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.model.M113.M113A2Model;
import tech.squadmc.squadmcor.heli.client.renderer.FcpVehicleTexture;
import tech.squadmc.squadmcor.heli.entity.vehicle.M113.M113A2Entity;

public class M113A2Renderer extends VehicleRenderer<M113A2Entity> {
    public M113A2Renderer(EntityRendererProvider.Context context) { super(context, new M113A2Model()); }
    @Override public ResourceLocation getTextureLocation(M113A2Entity entity) { return FcpVehicleTexture.resolve(entity, entity.getCurrentTexture()); }
}
