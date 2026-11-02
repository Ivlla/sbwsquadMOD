package tech.squadmc.squadmcor.repair;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import net.minecraft.network.chat.Component;

/** Ремонт частей техники: общие правила для горелки и станций AAS. */
public final class VehicleRepair {
    /** Горелка лечит корпус только до этой доли максимума. */
    public static final float TORCH_HULL_LIMIT = 0.5F;

    private VehicleRepair() {}

    public enum Part {
        TURRET, LEFT, RIGHT, ENGINE, SUB_ENGINE;

        public float health(VehicleEntity v) {
            return switch (this) {
                case TURRET -> v.getTurretHealth();
                case LEFT -> v.getLeftWheelHealth();
                case RIGHT -> v.getRightWheelHealth();
                case ENGINE -> v.getMainEngineHealth();
                case SUB_ENGINE -> v.getSubEngineHealth();
            };
        }

        public float max(VehicleEntity v) {
            return switch (this) {
                case TURRET -> v.getTurretMaxHealth();
                case LEFT -> v.getLeftWheelMaxHealth();
                case RIGHT -> v.getRightWheelMaxHealth();
                case ENGINE -> v.getMainEngineMaxHealth();
                case SUB_ENGINE -> v.getSubEngineMaxHealth();
            };
        }

        public void setHealth(VehicleEntity v, float value) {
            switch (this) {
                case TURRET -> v.setTurretHealth(value);
                case LEFT -> v.setLeftWheelHealth(value);
                case RIGHT -> v.setRightWheelHealth(value);
                case ENGINE -> v.setMainEngineHealth(value);
                case SUB_ENGINE -> v.setSubEngineHealth(value);
            }
        }

        public boolean needsRepair(VehicleEntity v) {
            float max = max(v);
            return max > 0F && health(v) < max;
        }

        public Component displayName(VehicleEntity v) {
            boolean tracked = isTracked(v);
            String key = switch (this) {
                case TURRET -> "turret";
                case LEFT -> tracked ? "track_left" : "wheel_left";
                case RIGHT -> tracked ? "track_right" : "wheel_right";
                case ENGINE -> "engine";
                case SUB_ENGINE -> "sub_engine";
            };
            return Component.translatable("part.squadmc." + key);
        }

        public static Part of(OBB.Part part) {
            return switch (part) {
                case TURRET -> TURRET;
                case WHEEL_LEFT -> LEFT;
                case WHEEL_RIGHT -> RIGHT;
                case MAIN_ENGINE -> ENGINE;
                case SUB_ENGINE -> SUB_ENGINE;
                default -> null;
            };
        }
    }

    public static boolean isTracked(VehicleEntity vehicle) {
        return vehicle.computed().getEngineType() == com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType.TRACK;
    }

    /** Добавляет части долю её максимума. Выбитая часть начинает с нуля. */
    public static void repairPart(VehicleEntity vehicle, Part part, float fraction) {
        float max = part.max(vehicle);
        float health = Math.max(0F, part.health(vehicle));
        part.setHealth(vehicle, Math.min(max, health + max * fraction));
    }

    public static boolean isFullyRepaired(VehicleEntity vehicle) {
        if (vehicle.getHealth() < vehicle.getMaxHealth()) return false;
        for (Part part : Part.values()) {
            if (part.needsRepair(vehicle)) return false;
        }
        return true;
    }

    /** Станции: чинит и корпус целиком, и все части. */
    public static void repairAll(VehicleEntity vehicle, float fraction) {
        vehicle.heal(vehicle.getMaxHealth() * fraction);
        for (Part part : Part.values()) {
            if (part.needsRepair(vehicle)) repairPart(vehicle, part, fraction);
        }
    }

    /** Сколько корпуса можно восстановить горелкой, чтобы не превысить предел. */
    public static float torchHullHeal(VehicleEntity vehicle, float amount) {
        return Math.min(amount, vehicle.getMaxHealth() * TORCH_HULL_LIMIT - vehicle.getHealth());
    }
}
