package tech.squadmc.squadmcor.spike;

import java.util.Objects;
import java.util.UUID;

/** A continuous press measured in server ticks, with one shot per press. */
public final class SpikeTrigger {
    public static final int CHARGE_TICKS = 40;
    private static final int HEARTBEAT_TIMEOUT = 10;
    private boolean held;
    private boolean fired;
    private int started = -1;
    private int lastInput = -1;
    private int slot = -1;
    private UUID launcher;

    public void update(boolean pressed, boolean ready, int newSlot, UUID newLauncher, int tick) {
        if (!pressed) {
            held = false;
            fired = false;
            started = -1;
        } else {
            boolean continuous = held && newSlot == slot && Objects.equals(launcher, newLauncher)
                    && tick >= lastInput && tick - lastInput <= HEARTBEAT_TIMEOUT;
            if (!ready || newLauncher == null) {
                started = -1;
            } else if (!continuous || started < 0) {
                started = tick;
            }
            held = true;
        }
        slot = newSlot;
        launcher = newLauncher;
        lastInput = tick;
    }

    public boolean poll(boolean ready, int selectedSlot, UUID currentLauncher, int tick) {
        if (!ready || !held || selectedSlot != slot || !Objects.equals(launcher, currentLauncher)
                || tick < lastInput || tick - lastInput > HEARTBEAT_TIMEOUT) {
            started = -1;
            return false;
        }
        if (fired || started < 0 || tick - started < CHARGE_TICKS) return false;
        fired = true;
        return true;
    }
}
