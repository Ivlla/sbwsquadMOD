// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.entity.vehicle.ManHx60;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Отдельная сущность того же грузовика без тента. */
public class ManHx60NoTentEntity extends ManHx60Entity {
    public ManHx60NoTentEntity(EntityType<ManHx60NoTentEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean hasTent() {
        return false;
    }
}
