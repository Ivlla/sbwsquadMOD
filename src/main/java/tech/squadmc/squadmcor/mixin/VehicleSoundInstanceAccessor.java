package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.client.sound.VehicleSoundInstance;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VehicleSoundInstance.class, remap = false)
public interface VehicleSoundInstanceAccessor {
    @Accessor("mobileVehicle")
    VehicleEntity squadmc$getVehicle();
}
