package tech.squadmc.squadmcor.heli.entity.vehicle.M113;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import tech.squadmc.squadmcor.heli.HeliVehicleBase;

public class M113Entity extends HeliVehicleBase {
    public M113Entity(EntityType<M113Entity> type, Level world) {
        super(type, world);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }
}
