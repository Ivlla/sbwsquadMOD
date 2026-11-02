// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.entity.vehicle.Ch53;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import tech.squadmc.squadmcor.heli.HeliVehicleBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Mh53Entity extends HeliVehicleBase {

    public Mh53Entity(EntityType<Mh53Entity> type, Level world) {
        super(type, world);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((entity, source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

}
