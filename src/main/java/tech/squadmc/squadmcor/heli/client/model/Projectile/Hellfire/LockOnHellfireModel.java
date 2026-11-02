// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.model.Projectile.Hellfire;

import com.atsuishio.superbwarfare.Mod;
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.heli.entity.projectile.Hellfire.LockOnHellfireEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LockOnHellfireModel extends GeoModel<LockOnHellfireEntity> {

    @Override
    public ResourceLocation getAnimationResource(LockOnHellfireEntity entity) {
        return new ResourceLocation("superbwarfare", "animations/javelin.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(LockOnHellfireEntity entity) {
        return new ResourceLocation(squadmc.MODID, "geo/hellfire.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LockOnHellfireEntity entity) {
        return new ResourceLocation(squadmc.MODID, "textures/entity/hellfire/hellfire.png");
    }
}
