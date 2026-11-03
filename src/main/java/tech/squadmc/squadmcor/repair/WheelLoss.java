package tech.squadmc.squadmcor.repair;

import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Учёт каждого колеса. Колёса борта нумеруются спереди назад по хитбоксам техники, поэтому выбивается
 * то колесо, в которое попали. SBW хранит одно здоровье на борт: оно считается здоровьем следующего
 * колеса и восстанавливается после каждой потери.
 *
 * Машина встаёт, если на одном борту выбито больше половины колёс или если у двухосной машины
 * выбита целая ось. При двух и более выбитых колёсах она едет медленнее.
 */
public final class WheelLoss {
    private static final int SLOW_AT = 2;
    private static final float SLOW_POWER_FACTOR = 0.96F;
    private static final int RIGHT_SHIFT = 16;
    private static final double HIT_TRACE_DISTANCE = 64.0;

    private static final Map<VehicleEntity, float[]> REPAIR_PROGRESS = new WeakHashMap<>();

    private WheelLoss() {}

    /** Маска хранится в базовых классах техники: младшие 16 бит левый борт, старшие правый. */
    public interface Holder {
        int squadmc$getWheelsLost();

        void squadmc$setWheelsLost(int mask);
    }

    public static boolean applies(VehicleEntity vehicle) {
        return vehicle instanceof Holder && vehicle.computed().getEngineType() == EngineType.WHEEL;
    }

    /** Биты выбитых колёс борта, бит 0 — переднее колесо. */
    public static int mask(VehicleEntity vehicle, boolean left) {
        if (vehicle instanceof Holder holder) {
            int all = holder.squadmc$getWheelsLost();
            return left ? all & 0xFFFF : all >>> RIGHT_SHIFT;
        }
        return (left ? vehicle.getLeftWheelDamaged() : vehicle.getRightWheelDamaged()) ? 1 : 0;
    }

    public static int lost(VehicleEntity vehicle, boolean left) {
        return Integer.bitCount(mask(vehicle, left));
    }

    private static void setMask(VehicleEntity vehicle, boolean left, int mask) {
        Holder holder = (Holder) vehicle;
        int other = mask(vehicle, !left);
        holder.squadmc$setWheelsLost(left ? (mask & 0xFFFF) | other << RIGHT_SHIFT : other | mask << RIGHT_SHIFT);
    }

    private static OBB.Part part(boolean left) {
        return left ? OBB.Part.WHEEL_LEFT : OBB.Part.WHEEL_RIGHT;
    }

    /** Число колёс на борту. Если хитбокс один на весь борт, считается, что колеса два. */
    public static int wheelsOnSide(VehicleEntity vehicle, boolean left) {
        int count = 0;
        for (OBB obb : vehicle.getOBBs()) {
            if (obb.part == part(left)) count++;
        }
        return Math.max(2, count);
    }

    /** Центры хитбоксов колёс борта спереди назад. Пусто, если хитбоксов на каждое колесо нет. */
    private static List<Vec3> centers(VehicleEntity vehicle, boolean left) {
        List<Vec3> centers = new ArrayList<>();
        for (OBB obb : vehicle.getOBBs()) {
            if (obb.part == part(left)) centers.add(new Vec3(obb.center.x, obb.center.y, obb.center.z));
        }
        if (centers.size() < 2) return List.of();
        Vec3 origin = vehicle.position();
        Vec3 forward = vehicle.getViewVector(1F);
        centers.sort(Comparator.comparingDouble((Vec3 c) -> -c.subtract(origin).dot(forward)));
        return centers;
    }

    /** Ближайшее к точке колесо борта из разрешённых битов. */
    private static int nearest(VehicleEntity vehicle, boolean left, int allowed, @Nullable Vec3 point) {
        int first = Integer.numberOfTrailingZeros(allowed);
        List<Vec3> centers = centers(vehicle, left);
        if (point == null || centers.isEmpty()) return first;
        int best = first;
        double bestDistance = Double.MAX_VALUE;
        for (int i = 0; i < centers.size(); i++) {
            if ((allowed >> i & 1) == 0) continue;
            double distance = centers.get(i).distanceToSqr(point);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return best;
    }

    /** Сервер: опустевшее здоровье борта выбивает колесо, ближайшее к попаданию. */
    public static void update(VehicleEntity vehicle) {
        vehicle.setLeftWheelDamaged(false);
        vehicle.setRightWheelDamaged(false);
        if (vehicle.level().isClientSide || vehicle.isWreck()) return;
        if (vehicle.getLeftWheelHealth() <= 0F) {
            breakWheel(vehicle, true);
            vehicle.setLeftWheelHealth(vehicle.getLeftWheelMaxHealth());
        }
        if (vehicle.getRightWheelHealth() <= 0F) {
            breakWheel(vehicle, false);
            vehicle.setRightWheelHealth(vehicle.getRightWheelMaxHealth());
        }
    }

    private static void breakWheel(VehicleEntity vehicle, boolean left) {
        int all = (1 << wheelsOnSide(vehicle, left)) - 1;
        int mask = mask(vehicle, left);
        int alive = all & ~mask;
        if (alive == 0) return;
        Entity hitter = vehicle.getLastDamageSource() == null ? null : vehicle.getLastDamageSource().getDirectEntity();
        Vec3 point = hitter == null ? null : hitPoint(vehicle, left, hitter);
        setMask(vehicle, left, mask | 1 << nearest(vehicle, left, alive, point));
    }

    /**
     * Where a projectile struck this side's wheels. Its position alone is unreliable: a fast round
     * can still be many blocks from the point of impact, so its flight line is traced instead.
     */
    private static Vec3 hitPoint(VehicleEntity vehicle, boolean left, Entity hitter) {
        Vec3 position = hitter.position();
        Vec3 motion = hitter.getDeltaMovement();
        if (motion.lengthSqr() < 1.0E-6) return position;
        Vec3 reach = motion.normalize().scale(HIT_TRACE_DISTANCE);
        Vector3d from = new Vector3d(position.x - reach.x, position.y - reach.y, position.z - reach.z);
        Vector3d to = new Vector3d(position.x + reach.x, position.y + reach.y, position.z + reach.z);
        Vector3d first = null;
        double firstDistance = Double.MAX_VALUE;
        for (OBB obb : vehicle.getOBBs()) {
            if (obb.part != part(left)) continue;
            Optional<Vector3d> hit = obb.clip(new Vector3d(from), new Vector3d(to));
            if (hit.isEmpty()) continue;
            double distance = from.distanceSquared(hit.get());
            if (distance < firstDistance) {
                firstDistance = distance;
                first = hit.get();
            }
        }
        return first == null ? position : new Vec3(first.x, first.y, first.z);
    }

    private static boolean sideDisabled(VehicleEntity vehicle, boolean left) {
        return lost(vehicle, left) > wheelsOnSide(vehicle, left) / 2;
    }

    private static boolean axleLost(VehicleEntity vehicle) {
        return wheelsOnSide(vehicle, true) <= 2 && wheelsOnSide(vehicle, false) <= 2
                && (mask(vehicle, true) & mask(vehicle, false)) != 0;
    }

    public static boolean immobilized(VehicleEntity vehicle) {
        if (!applies(vehicle) || mask(vehicle, true) + mask(vehicle, false) == 0) return false;
        return sideDisabled(vehicle, true) || sideDisabled(vehicle, false) || axleLost(vehicle);
    }

    /** Каждый тик из базовых классов: при двух и более потерянных колёсах мощность не набирается полностью. */
    public static void tick(VehicleEntity vehicle) {
        if (applies(vehicle) && lost(vehicle, true) + lost(vehicle, false) >= SLOW_AT) {
            vehicle.setPower(vehicle.getPower() * SLOW_POWER_FACTOR);
        }
    }

    /** Чинит выбитое колесо борта: ближайшее к точке ремонта или первое, если точки нет. */
    public static void repair(VehicleEntity vehicle, boolean left, float fraction, @Nullable Vec3 point) {
        float[] progress = REPAIR_PROGRESS.computeIfAbsent(vehicle, v -> new float[2]);
        int side = left ? 0 : 1;
        progress[side] += fraction;
        if (progress[side] < 1F) return;
        progress[side] = 0F;
        int mask = mask(vehicle, left);
        if (mask != 0) setMask(vehicle, left, mask & ~(1 << nearest(vehicle, left, mask, point)));
    }

    public static float progress(VehicleEntity vehicle, boolean left) {
        float[] progress = REPAIR_PROGRESS.get(vehicle);
        return progress == null ? 0F : progress[left ? 0 : 1];
    }
}
