package tech.squadmc.squadmcor.client.skin;

import com.atsuishio.superbwarfare.data.vehicle_skin.SkinInfo;
import com.atsuishio.superbwarfare.data.vehicle_skin.VehicleSkin;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.resources.ResourceLocation;

/** Lets renderers with a fixed texture follow the skin chosen with SBW's skin spray. */
public final class VehicleSkinTexture {

    private VehicleSkinTexture() {
    }

    public static ResourceLocation resolve(VehicleEntity vehicle, ResourceLocation base) {
        SkinInfo skin = VehicleSkin.getSkin(vehicle);
        if (skin == null) return base;
        ResourceLocation texture = ResourceLocation.tryParse(skin.getTexture());
        return texture != null ? texture : base;
    }
}
