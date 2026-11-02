// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.entity.vehicle.engine;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import tech.squadmc.squadmcor.network.ModNetworking;
import tech.squadmc.squadmcor.heli.network.SquadFirePacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.Set;

/**
 * Server side of the Squad weapon sounds: every shot of a listed vehicle is forwarded to the
 * clients tracking it, which play the layered Squad fire cue (SquadWeaponSounds). The vehicle's
 * own SBW fire sounds are pointed at {@code fcp:squad.silent} in its JSON so they don't double up.
 *
 * Hooked from playShootSound3p(LivingEntity, GunData, Vec3) in the FCP vehicle bases: SBW calls
 * it once per shot, server-side, for every weapon.
 */
public final class SquadWeaponFire {

    /** Vehicles whose weapons use Squad fire sounds (generated with their squad_weapons profiles). */
    public static final Set<String> VEHICLES = Set.of(
            "squadmc:rg_33",
                        "squadmc:huey_door_gunner_m134",
            "squadmc:huey_m134_door_guns",
            "squadmc:huey_m134_gunship",
            "squadmc:huey_m60_gunship",
            "squadmc:huey_rockets",
            "squadmc:littlebird_armed",
            "squadmc:littlebird_heavy",
            "squadmc:mh60l",
            "squadmc:mi17_door_guns",
            "squadmc:mi8_amtsh",
            "squadmc:mi8_door_guns",
            "squadmc:mi8_mtv",
            "squadmc:venom_door_guns",
            "squadmc:venom_gunship"
);

    /** Vehicles with a Squad turret traverse loop; SBW's own turret sound is skipped on these. */
    public static final Set<String> TURRETS = Set.of(
            "squadmc:rg_33",
                        "squadmc:huey_m134_door_guns",
            "squadmc:huey_m134_gunship",
            "squadmc:huey_m60_door_guns",
            "squadmc:huey_m60_gunship",
            "squadmc:mi17_door_guns",
            "squadmc:mi8_door_guns",
            "squadmc:venom_door_guns",
            "squadmc:venom_gunship"
);

    private SquadWeaponFire() {
    }

    public static void onShoot(VehicleEntity vehicle, LivingEntity shooter, GunData data) {
        if (vehicle.level().isClientSide() || !VEHICLES.contains(EngineControl.id(vehicle))) return;
        String weapon = null;
        for (Map.Entry<String, GunData> e : vehicle.getGunDataMap().entrySet()) {
            if (e.getValue() == data) {
                weapon = e.getKey();
                break;
            }
            if (weapon == null && data != null && data.equals(e.getValue())) weapon = e.getKey();
        }
        if (weapon == null) return;
        // A gun firing a second ammo type with its own cue (e.g. the BMP-3's 100 mm firing its 9M117) is
        // keyed "weapon#index"; clients fall back to the plain weapon when the profile has no such entry.
        if (data != null && data.selectedAmmoType.get() > 0) weapon = weapon + "#" + data.selectedAmmoType.get();
        final String key = weapon;
        ModNetworking.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> vehicle),
                new SquadFirePacket(vehicle.getId(), key, shooter == null ? -1 : shooter.getId()));
    }
}
