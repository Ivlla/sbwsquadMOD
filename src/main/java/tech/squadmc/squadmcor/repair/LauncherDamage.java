package tech.squadmc.squadmcor.repair;

import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.entity.projectile.RpgRocketStandardEntity;
import com.atsuishio.superbwarfare.entity.projectile.RpgRocketTBGEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import tech.squadmc.squadmcor.entity.projectile.SpikeMissileEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.squadmc;

/**
 * Launcher rounds against this mod's vehicles deal a fixed share of maximum health, set by how many
 * hits should destroy each class of vehicle from each side. A round's "Damage" value in its gun JSON
 * picks its place in the table: tandem from {@link #TANDEM_DAMAGE}, and weaker rounds need more hits.
 */
public final class LauncherDamage {

    private enum VehicleClass {TANK, APC, TRUCK}

    private enum Side {FRONT, SIDE, REAR}

    private static final String HIT_MARK = "squadmc_launcher_hit";

    private static final float TANDEM_DAMAGE = 520F;
    private static final float NORMAL_WEAK = 300F;
    private static final float NORMAL_STRONG = 450F;
    private static final float TANDEM_WEAK = 550F;
    private static final float TANDEM_STRONG = 700F;
    /** A tandem round to a tank's rear leaves this share of health; the second hit finishes it. */
    private static final float TANK_REAR_TANDEM_SHARE = 0.85F;
    /** Rounding margin so the last planned hit always destroys the vehicle. */
    private static final float MARGIN = 1.002F;

    /** Hits to destroy, {weak round, strong round}, indexed by vehicle class then side. */
    private static final float[][][] NORMAL = {
            {{8, 7}, {6, 5}, {6, 5}},
            {{5, 4}, {3, 3}, {2, 2}},
            {{2, 2}, {2, 2}, {1, 1}}};
    private static final float[][][] TANDEM = {
            {{5, 4}, {3, 3}, {2, 2}},
            {{3, 2}, {2, 2}, {1, 1}},
            {{1, 1}, {1, 1}, {1, 1}}};

    private static final String[] TANKS = {"m1a2", "leopard", "t64", "t72", "t80", "sprut"};
    private static final String[] TRUCKS = {"fmtv", "kamaz", "m939", "man_hx", "ural", "grad",
            "humvee", "toyota", "uaz", "fenek", "gaz_tigr", "motuo", "motorboat", "rg_33"};

    private LauncherDamage() {
    }

    /** @return damage for a launcher round's direct hit or follow-up blast, or a negative value if not one. */
    public static float compute(VehicleEntity vehicle, DamageSource source) {
        Entity round = source.getDirectEntity();
        boolean spike = round instanceof SpikeMissileEntity;
        if (!spike && !(round instanceof RpgRocketStandardEntity) && !(round instanceof RpgRocketTBGEntity)) return -1F;
        VehicleClass vehicleClass = classOf(vehicle);
        if (vehicleClass == null) return -1F;

        boolean directHit = spike || source.is(ModDamageTypes.PROJECTILE_HIT) || source.is(ModDamageTypes.PROJECTILE_HIT_HEADSHOT);
        if (!directHit) {
            // The blast that follows a direct hit is already part of that hit; a near miss stays as it was.
            return round.getPersistentData().getInt(HIT_MARK) == vehicle.getId() + 1 ? 0F : -1F;
        }
        round.getPersistentData().putInt(HIT_MARK, vehicle.getId() + 1);

        Side side = sideOf(vehicle, round);
        float damage = spike ? TANDEM_STRONG : ((FastThrowableProjectile) round).getDamage();
        return vehicle.getMaxHealth() * share(vehicleClass, side, damage, spike) * MARGIN;
    }

    private static float share(VehicleClass vehicleClass, Side side, float damage, boolean spike) {
        if (vehicleClass == VehicleClass.TANK && side == Side.REAR && damage >= TANDEM_DAMAGE) {
            return spike ? 1F : TANK_REAR_TANDEM_SHARE;
        }
        float[] hits;
        float strength;
        if (damage >= TANDEM_DAMAGE) {
            hits = TANDEM[vehicleClass.ordinal()][side.ordinal()];
            strength = (damage - TANDEM_WEAK) / (TANDEM_STRONG - TANDEM_WEAK);
        } else {
            hits = NORMAL[vehicleClass.ordinal()][side.ordinal()];
            strength = (damage - NORMAL_WEAK) / (NORMAL_STRONG - NORMAL_WEAK);
        }
        float shots = Math.round(Mth.lerp(Mth.clamp(strength, 0F, 1F), hits[0], hits[1]));
        // Blast and fragmentation rounds, weaker than any anti-armour round, need more hits still.
        if (damage < NORMAL_WEAK) shots *= NORMAL_WEAK / Math.max(damage, 1F);
        return 1F / shots;
    }

    private static Side sideOf(VehicleEntity vehicle, Entity round) {
        Vec3 motion = round.getDeltaMovement().multiply(1, 0, 1);
        Vec3 forward = vehicle.getViewVector(1F).multiply(1, 0, 1);
        if (motion.lengthSqr() < 1.0E-6 || forward.lengthSqr() < 1.0E-6) return Side.SIDE;
        double facing = -motion.normalize().dot(forward.normalize());
        if (facing > Mth.SQRT_OF_TWO / 2) return Side.FRONT;
        if (facing < -Mth.SQRT_OF_TWO / 2) return Side.REAR;
        return Side.SIDE;
    }

    /** @return the class, or null for vehicles outside the table: helicopters, emplacements, other mods. */
    private static VehicleClass classOf(VehicleEntity vehicle) {
        var key = EntityType.getKey(vehicle.getType());
        if (!squadmc.MODID.equals(key.getNamespace()) || EngineControl.isHelicopter(vehicle)) return null;
        if (vehicle.computed().getEngineType() == EngineType.FIXED) return null;
        String id = key.getPath();
        if (matches(id, TANKS)) return VehicleClass.TANK;
        if (matches(id, TRUCKS)) return VehicleClass.TRUCK;
        return VehicleClass.APC;
    }

    private static boolean matches(String id, String[] prefixes) {
        for (String prefix : prefixes) {
            if (id.startsWith(prefix)) return true;
        }
        return false;
    }
}
