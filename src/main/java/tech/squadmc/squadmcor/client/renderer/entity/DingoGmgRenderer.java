package tech.squadmc.squadmcor.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.squadmc.squadmcor.entity.DingoArmedEntity;

public class DingoGmgRenderer extends DingoRenderer<DingoArmedEntity> {
    public DingoGmgRenderer(EntityRendererProvider.Context context) { super(context, "dingo_gmg", "dingo_2"); }
}
