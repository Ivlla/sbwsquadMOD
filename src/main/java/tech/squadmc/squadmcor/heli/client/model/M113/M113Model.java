package tech.squadmc.squadmcor.heli.client.model.M113;

import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.model.FCPVehicleModel;
import tech.squadmc.squadmcor.heli.entity.vehicle.M113.M113Entity;
import tech.squadmc.squadmcor.squadmc;

public class M113Model extends FCPVehicleModel<M113Entity> {
    @Override
    public ResourceLocation getModelResource(M113Entity entity) {
        return new ResourceLocation(squadmc.MODID, "geo/m113.geo.json");
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
}
