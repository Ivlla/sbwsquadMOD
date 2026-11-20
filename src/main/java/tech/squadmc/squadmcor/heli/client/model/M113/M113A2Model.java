package tech.squadmc.squadmcor.heli.client.model.M113;

import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.heli.client.model.FCPVehicleModel;
import tech.squadmc.squadmcor.heli.client.model.Util.FCPTrackPath;
import tech.squadmc.squadmcor.heli.entity.vehicle.M113.M113A2Entity;
import tech.squadmc.squadmcor.squadmc;

public class M113A2Model extends FCPVehicleModel<M113A2Entity> {
    @Override
    public ResourceLocation getModelResource(M113A2Entity entity) {
        return new ResourceLocation(squadmc.MODID, "geo/m113a2.geo.json");
    }

    @Override
    protected FCPTrackPath getTrackPath() {
        return M113TrackPaths.M113;
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }
}
