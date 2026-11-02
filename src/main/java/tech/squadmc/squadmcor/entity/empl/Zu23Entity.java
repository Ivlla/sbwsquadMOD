package tech.squadmc.squadmcor.entity.empl;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Стационарная ЗУ-23-2. */
public class Zu23Entity extends GeoVehicleEntity {
    public Zu23Entity(EntityType<? extends Zu23Entity> type, Level world) {
        super(type, world);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.4F) * damage);
    }
}
