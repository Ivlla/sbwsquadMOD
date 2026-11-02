package tech.squadmc.squadmcor.repair;

import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Счётчик выбитых колёс. SBW хранит одно здоровье на борт, поэтому оно считается здоровьем текущего
 * колеса: опустело — колесо потеряно, а следующее колесо борта начинает с полного здоровья.
 * Одно колесо не мешает ехать, с двумя машина едет медленнее, с тремя стоит.
 */
public final class WheelLoss {
    public static final int SLOW_AT = 2;
    public static final int STOP_AT = 3;
    private static final float SLOW_POWER_FACTOR = 0.96F;

    private static final Map<VehicleEntity, float[]> REPAIR_PROGRESS = new WeakHashMap<>();

    private WheelLoss() {}

    /** Счётчик хранят только наши базовые классы техники. */
    public interface Holder {
        byte squadmc$getWheelsLost();

        void squadmc$setWheelsLost(byte packed);
    }

    public static boolean applies(VehicleEntity vehicle) {
        return vehicle instanceof Holder && vehicle.computed().getEngineType() == EngineType.WHEEL;
    }

    public static int left(VehicleEntity vehicle) {
        return vehicle instanceof Holder holder ? holder.squadmc$getWheelsLost() & 15 : vehicle.getLeftWheelDamaged() ? 1 : 0;
    }

    public static int right(VehicleEntity vehicle) {
        return vehicle instanceof Holder holder ? holder.squadmc$getWheelsLost() >> 4 & 15 : vehicle.getRightWheelDamaged() ? 1 : 0;
    }

    public static int total(VehicleEntity vehicle) {
        return left(vehicle) + right(vehicle);
    }

    private static void set(VehicleEntity vehicle, int left, int right) {
        ((Holder) vehicle).squadmc$setWheelsLost((byte) (Math.min(left, 15) | Math.min(right, 15) << 4));
    }

    /** Сервер: превращает опустевшее здоровье борта в потерянное колесо. */
    public static void update(VehicleEntity vehicle) {
        vehicle.setLeftWheelDamaged(false);
        vehicle.setRightWheelDamaged(false);
        if (vehicle.level().isClientSide || vehicle.isWreck()) return;
        if (vehicle.getLeftWheelHealth() <= 0F) {
            set(vehicle, left(vehicle) + 1, right(vehicle));
            vehicle.setLeftWheelHealth(vehicle.getLeftWheelMaxHealth());
        }
        if (vehicle.getRightWheelHealth() <= 0F) {
            set(vehicle, left(vehicle), right(vehicle) + 1);
            vehicle.setRightWheelHealth(vehicle.getRightWheelMaxHealth());
        }
    }

    /** Каждый тик из базовых классов: при двух потерянных колёсах мощность не набирается полностью. */
    public static void tick(VehicleEntity vehicle) {
        if (applies(vehicle) && total(vehicle) == SLOW_AT) vehicle.setPower(vehicle.getPower() * SLOW_POWER_FACTOR);
    }

    /** Ремонт одного потерянного колеса борта. Возвращает прогресс текущего колеса от 0 до 1. */
    public static float repair(VehicleEntity vehicle, boolean leftSide, float fraction) {
        float[] progress = REPAIR_PROGRESS.computeIfAbsent(vehicle, v -> new float[2]);
        int side = leftSide ? 0 : 1;
        progress[side] += fraction;
        if (progress[side] < 1F) return progress[side];
        progress[side] = 0F;
        if (leftSide) set(vehicle, left(vehicle) - 1, right(vehicle));
        else set(vehicle, left(vehicle), right(vehicle) - 1);
        return 1F;
    }

    public static float progress(VehicleEntity vehicle, boolean leftSide) {
        float[] progress = REPAIR_PROGRESS.get(vehicle);
        return progress == null ? 0F : progress[leftSide ? 0 : 1];
    }
}
