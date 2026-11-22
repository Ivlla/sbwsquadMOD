package tech.squadmc.squadmcor.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Shared entity type for the weapon-equipped Dingo variants.
 * Driving, steering, wheel animation and damage behavior are inherited from DingoEntity.
 */
public class DingoArmedEntity extends DingoEntity {
    public DingoArmedEntity(EntityType<? extends DingoArmedEntity> type, Level level) {
        super(type, level);
    }
}
