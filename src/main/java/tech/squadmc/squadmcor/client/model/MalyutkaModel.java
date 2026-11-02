package tech.squadmc.squadmcor.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.squadmc.squadmcor.entity.projectile.MalyutkaEntity;

public class MalyutkaModel extends GeoModel<MalyutkaEntity> {
    @Override
    public ResourceLocation getModelResource(MalyutkaEntity animatable) {
        return new ResourceLocation("squadmc", "geo/malyutka.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MalyutkaEntity animatable) {
        return new ResourceLocation("squadmc", "textures/entity/malyutka.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MalyutkaEntity entity) {
        return Mod.loc("animations/javelin_missile.animation.json");
    }
}
