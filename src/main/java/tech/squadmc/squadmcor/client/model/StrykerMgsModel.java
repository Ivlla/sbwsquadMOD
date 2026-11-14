package tech.squadmc.squadmcor.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.StrykerMgsEntity;

public class StrykerMgsModel extends StrykerM2Model<StrykerMgsEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation("squadmc", "geo/stryker_mgs.geo.json");

    @Override
    public ResourceLocation getModelResource(StrykerMgsEntity vehicle) {
        return MODEL;
    }

    @Override
    public TransformContext<StrykerMgsEntity> collectTransform(String boneName) {
        if (boneName.equals("BarrelOccilator")) {
            return (bone, vehicle, state) -> bone.setPosZ(
                    4F * Mth.clamp(vehicle.getShootAnimationTimer(1, 0) / 10F, 0F, 1F));
        }
        return super.collectTransform(boneName);
    }
}
