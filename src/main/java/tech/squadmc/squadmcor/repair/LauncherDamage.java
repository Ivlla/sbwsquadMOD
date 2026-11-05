package tech.squadmc.squadmcor.repair;

import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.entity.projectile.CannonShellEntity;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.entity.projectile.RpgRocketStandardEntity;
import com.atsuishio.superbwarfare.entity.projectile.RpgRocketTBGEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;
import com.atsuishio.superbwarfare.entity.projectile.WireGuideMissileEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import tech.squadmc.squadmcor.entity.projectile.MalyutkaEntity;
import tech.squadmc.squadmcor.entity.projectile.SpikeMissileEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.squadmc;

/**
 * Hits to kill for launchers, tank guns, vehicle ATGMs and APC autocannons. Launcher rounds against this mod's vehicles deal a fixed share of maximum health, set by how many
 * hits should destroy each class of vehicle from each side. A round's "Damage" value in its gun JSON
 * picks its place in the table: tandem from {@link #TANDEM_DAMAGE}, and weaker rounds need more hits.
 */
public final class LauncherDamage {

    private enum VehicleClass {TANK, APC, TRUCK, LIGHT, OTHER}

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
    private static final String[] TRUCKS = {"fmtv", "kamaz", "m939", "man_hx", "ural", "grad"};
    private static final String[] LIGHT = {"humvee", "toyota", "uaz", "fenek", "gaz_tigr", "motuo", "motorboat", "rg_33"};

    /** Hits by target (tank, APC) and side (front, side, rear). */
    private static final float[][] TANK_GUN_HITS = {{4, 3, 2}, {2, 1, 1}};
    private static final float[][] ATGM_HITS = {{2, 2, 1}, {1, 1, 1}};
    /** Seconds of sustained fire by target (tank, APC, truck) and side (front, side, rear). */
    private static final float[][] AUTOCANNON_SECONDS = {{17.5F, 10, 7.5F}, {12.5F, 7.5F, 5}, {7.5F, 5, 5}};
    private static final float AUTOCANNON_OTHER_SECONDS = 5F;
    private static final double DEFAULT_AUTOCANNON_RPM = 300;

    private LauncherDamage() {
    }

    /** @return damage replacing SBW's armour calculation, or a negative value to leave it in charge. */
    public static float compute(VehicleEntity vehicle, DamageSource source) {
        Entity round = source.getDirectEntity();
        if (round == null) return -1F;
        VehicleClass target = classOf(vehicle);
        if (target == null) return -1F;
        VehicleEntity firedFrom = source.getEntity() != null && source.getEntity().getVehicle() instanceof VehicleEntity v ? v : null;
        VehicleClass shooter = firedFrom == null ? null : classOf(firedFrom);

        boolean spike = round instanceof SpikeMissileEntity;
        boolean launcher = spike || round instanceof RpgRocketStandardEntity || round instanceof RpgRocketTBGEntity;
        boolean tankGun = round instanceof CannonShellEntity && shooter == VehicleClass.TANK;
        boolean atgm = (round instanceof WireGuideMissileEntity || round instanceof MalyutkaEntity) && firedFrom != null
                && (shooter == VehicleClass.APC || EntityType.getKey(firedFrom.getType()).getPath().equals("humvee_tow"));
        boolean autocannon = round instanceof SmallCannonShellEntity && shooter == VehicleClass.APC;
        if (!launcher && !tankGun && !atgm && !autocannon) return -1F;
        if (launcher && target == VehicleClass.OTHER) return -1F;

        boolean alreadyHit = round.getPersistentData().getInt(HIT_MARK) == vehicle.getId() + 1;
        if (isBlast(source)) {
            // The blast that follows a direct hit is already part of that hit; a near miss stays as it was.
            return alreadyHit ? 0F : -1F;
        }
        if (alreadyHit) return 0F;
        round.getPersistentData().putInt(HIT_MARK, vehicle.getId() + 1);

        Side side = sideOf(vehicle, round);
        float max = vehicle.getMaxHealth();
        if (tankGun) return max * MARGIN / gunHits(TANK_GUN_HITS, target, side);
        if (atgm) return max * MARGIN / gunHits(ATGM_HITS, target, side);
        if (autocannon) return (float) (max / (autocannonSeconds(target, side) * shotsPerSecond(firedFrom, source.getEntity()) * spread(vehicle, firedFrom)));
        float damage = spike ? TANDEM_STRONG : ((FastThrowableProjectile) round).getDamage();
        return max * share(target == VehicleClass.LIGHT ? VehicleClass.TRUCK : target, side, damage, spike) * MARGIN;
    }

    private static boolean isBlast(DamageSource source) {
        return source.is(ModDamageTypes.PROJECTILE_EXPLOSION) || source.is(ModDamageTypes.CUSTOM_EXPLOSION) || source.is(DamageTypeTags.IS_EXPLOSION);
    }

    /** Tanks and APCs per the table; anything else dies to one hit. */
    private static float gunHits(float[][] table, VehicleClass target, Side side) {
        return target == VehicleClass.TANK || target == VehicleClass.APC ? table[target.ordinal()][side.ordinal()] : 1F;
    }

    private static float autocannonSeconds(VehicleClass target, Side side) {
        return switch (target) {
            case TANK, APC, TRUCK -> AUTOCANNON_SECONDS[target.ordinal()][side.ordinal()];
            default -> AUTOCANNON_OTHER_SECONDS;
        };
    }

    private static double shotsPerSecond(VehicleEntity vehicle, Entity shooter) {
        GunData data = vehicle.getGunData(shooter);
        double rpm = data == null ? 0 : ((Number) data.get(GunProp.RPM)).doubleValue();
        return (rpm > 0 ? rpm : DEFAULT_AUTOCANNON_RPM) / 60.0;
    }

    /** A steady difference per gun, plus a little per shot, so kill times are not exact. */
    private static float spread(VehicleEntity target, VehicleEntity shooter) {
        int perGun = Math.floorMod(EntityType.getKey(shooter.getType()).hashCode(), 21) - 10;
        return (1F + perGun / 100F) * (0.95F + target.level().random.nextFloat() * 0.1F);
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

    /** @return the class, or null for vehicles from other mods, which keep SBW's own damage. */
    private static VehicleClass classOf(VehicleEntity vehicle) {
        var key = EntityType.getKey(vehicle.getType());
        if (!squadmc.MODID.equals(key.getNamespace())) return null;
        if (EngineControl.isHelicopter(vehicle) || vehicle.computed().getEngineType() == EngineType.FIXED) return VehicleClass.OTHER;
        String id = key.getPath();
        if (matches(id, TANKS)) return VehicleClass.TANK;
        if (matches(id, TRUCKS)) return VehicleClass.TRUCK;
        if (matches(id, LIGHT)) return VehicleClass.LIGHT;
        return VehicleClass.APC;
    }

    private static boolean matches(String id, String[] prefixes) {
        for (String prefix : prefixes) {
            if (id.startsWith(prefix)) return true;
        }
        return false;
    }
}
