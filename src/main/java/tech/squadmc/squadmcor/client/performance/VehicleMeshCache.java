package tech.squadmc.squadmcor.client.performance;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.IdentityHashMap;

/** One model per renderer; resource reloads/LOD changes release the previous cache. */
public final class VehicleMeshCache {
    private BakedGeoModel model;
    private final IdentityHashMap<GeoBone, CachedVehicleMesh> bones = new IdentityHashMap<>();

    public void useModel(BakedGeoModel current) {
        if (model != current) {
            bones.clear();
            model = current;
        }
    }

    public CachedVehicleMesh get(GeoBone bone) {
        CachedVehicleMesh mesh = bones.get(bone);
        if (mesh == null) {
            mesh = new CachedVehicleMesh(bone.getCubes());
            bones.put(bone, mesh);
        }
        return mesh;
    }
}
