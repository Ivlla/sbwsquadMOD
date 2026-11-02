package tech.squadmc.squadmcor.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;

/** МТ-ЛБ с ЗУ-23-2 на крыше. */
public class MtlbZu23Entity extends mtlbEntity {
    public MtlbZu23Entity(EntityType<? extends MtlbZu23Entity> type, Level world) {
        super(type, world);
    }

    /** Анимаций выстрела у этой модели нет. */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }
}
