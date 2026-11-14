package tech.squadmc.squadmcor.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** M1128: shared Stryker chassis; armament, seats and collision are data-driven. */
public class StrykerMgsEntity extends StrykerM2Entity {
    public StrykerMgsEntity(EntityType<? extends StrykerMgsEntity> type, Level level) {
        super(type, level);
    }
}
