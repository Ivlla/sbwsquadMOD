// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.model.Kamaz;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.heli.client.model.FCPVehicleModel;
import tech.squadmc.squadmcor.heli.client.model.Util.WheelRotationTransforms;
import tech.squadmc.squadmcor.heli.entity.vehicle.Kamaz.KamazEntity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class KamazModel extends FCPVehicleModel<KamazEntity> {

    @Override
    public ResourceLocation getModelResource(KamazEntity animatable) {
        return new ResourceLocation(squadmc.MODID, "geo/kamaz.geo.json");
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
    @Override
    public void setCustomAnimations(KamazEntity vehicle, long instanceId, AnimationState<KamazEntity> animationState) {
        super.setCustomAnimations(vehicle, instanceId, animationState);
        this.getBone("pehota2").ifPresent(b -> setHiddenDeep(b, !vehicle.hasTent()));
    }

    private static void setHiddenDeep(GeoBone bone, boolean hidden) {
        bone.setHidden(hidden);
        for (GeoBone child : bone.getChildBones()) setHiddenDeep(child, hidden);
    }


    @Override
    public @Nullable TransformContext<KamazEntity> collectTransform(String boneName) {

        VehicleModel.TransformContext<KamazEntity> turn =
                WheelRotationTransforms.matchAnyTurn(boneName, 0.494, 30f,
                        "WheelL0Turn", "WheelR0Turn", "WheelL1Turn", "WheelR1Turn");
        if (turn != null) return turn;

        VehicleModel.TransformContext<KamazEntity> wheels =
                WheelRotationTransforms.matchAny(boneName, 0.494,
                        "WheelL0", "WheelR0", "WheelL1", "WheelR1");
        if (wheels != null) return wheels;

        return super.collectTransform(boneName);
    }
}
