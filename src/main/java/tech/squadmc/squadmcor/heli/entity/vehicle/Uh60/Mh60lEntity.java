// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.entity.vehicle.Uh60;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import tech.squadmc.squadmcor.heli.HeliVehicleBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Mh60lEntity extends HeliVehicleBase {

    public Mh60lEntity(EntityType<Mh60lEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((entity, source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    // Same "hide the missile once it's been fired" mechanism as Viper: each
    // Hellfire bone is toggled off individually as its bank's ammo count
    // counts down from Magazine-1 to 0.
    public boolean GetWeaponState(String WeaponName, int Count) {
        if (getAmmoCount(WeaponName) == Count)
            return true;
        else if (getAmmoCount(WeaponName) < Count)
            return true;
        else
            return false;
    }

}
