// Based on Vintage Vehicle Pack by zxcnelti, sergeyrendo (see VVP_CREDITS.txt). Used with the authors' permission. Modified for SquadMC.
package tech.squadmc.squadmcor.heli.entity.vehicle.Ah64;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import tech.squadmc.squadmcor.heli.HeliVehicleBase;

public class Ah64Entity extends HeliVehicleBase {
    public Ah64Entity(EntityType<Ah64Entity> type, Level world) {
        super(type, world);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }
}
