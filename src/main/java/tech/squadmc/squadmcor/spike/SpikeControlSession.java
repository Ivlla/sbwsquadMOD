package tech.squadmc.squadmcor.spike;

/** Pure server session logic, independent of Minecraft classes. */
public final class SpikeControlSession {
    private static final int HEARTBEAT_TIMEOUT = 10;
    private long revision;
    private int lastTick;
    private int slot = -1;
    private boolean active;

    public void update(boolean valid, int newSlot, int tick) {
        if (!valid || newSlot != slot || tick - lastTick > HEARTBEAT_TIMEOUT || tick < lastTick) {
            revision++;
        }
        active = valid;
        slot = newSlot;
        lastTick = tick;
    }

    public boolean isActive(int selectedSlot, int tick) {
        return active && selectedSlot == slot && tick >= lastTick && tick - lastTick <= HEARTBEAT_TIMEOUT;
    }

    public long revision() { return revision; }
}
