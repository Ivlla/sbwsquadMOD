// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client.model.Projectile.Hellfire;

import com.atsuishio.superbwarfare.Mod;
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.heli.entity.projectile.Hellfire.WireGuidedHellfireEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WireGuidedHellfireModel extends GeoModel<WireGuidedHellfireEntity> {

    @Override
    public ResourceLocation getAnimationResource(WireGuidedHellfireEntity entity) {
        return Mod.loc("animations/javelin.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(WireGuidedHellfireEntity entity) {
        return new ResourceLocation(squadmc.MODID, "geo/hellfire.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WireGuidedHellfireEntity entity) {
        return new ResourceLocation(squadmc.MODID, "textures/entity/hellfire/hellfire.png");
    }
}
