package tech.squadmc.squadmcor.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.squadmc.squadmcor.entity.DingoEntity;

public class DingoBaseRenderer extends DingoRenderer<DingoEntity> {
    public DingoBaseRenderer(EntityRendererProvider.Context context) { super(context, "dingo", "dingo_1"); }
}
