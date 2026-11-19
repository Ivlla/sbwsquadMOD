package tech.squadmc.squadmcor.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Shared Fenek-derived driving, steering and wheel animation for the four Dingo variants. */
public class DingoEntity extends FenekEntity {
    public DingoEntity(EntityType<? extends DingoEntity> type, Level level) { super(type, level); }
}
