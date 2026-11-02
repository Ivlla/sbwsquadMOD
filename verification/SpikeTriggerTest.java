import tech.squadmc.squadmcor.spike.SpikeTrigger;

import java.util.UUID;

public final class SpikeTriggerTest {
    private static final UUID A = new UUID(0, 1);
    private static final UUID B = new UUID(0, 2);
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void holdWithoutShot(SpikeTrigger trigger, int first, int last, int slot, UUID launcher) {
        for (int tick = first; tick <= last; tick++) {
            trigger.update(true, true, slot, launcher, tick);
            check(!trigger.poll(true, slot, launcher, tick), "Early/repeated shot at tick " + tick);
        }
    }

    public static void main(String[] args) {
        SpikeTrigger normal = new SpikeTrigger();
        holdWithoutShot(normal, 100, 139, 0, A);
        normal.update(true, true, 0, A, 140);
        check(normal.poll(true, 0, A, 140), "Must shoot after exactly 40 elapsed server ticks");
        check(!normal.poll(true, 0, A, 140), "Cannot consume the charge twice in one tick");
        holdWithoutShot(normal, 141, 240, 0, A);
        normal.update(false, true, 0, A, 241);
        holdWithoutShot(normal, 241, 280, 0, A);
        normal.update(true, true, 0, A, 281);
        check(normal.poll(true, 0, A, 281), "A fresh press can fire a second shot");

        SpikeTrigger canceled = new SpikeTrigger();
        holdWithoutShot(canceled, 10, 49, 0, A);
        canceled.update(false, true, 0, A, 49);
        // Release/repress delivered in the same server tick still resets the timer.
        holdWithoutShot(canceled, 49, 88, 0, A);
        canceled.update(true, true, 0, A, 89);
        check(canceled.poll(true, 0, A, 89), "Release must discard the previous 39 ticks");

        SpikeTrigger spam = new SpikeTrigger();
        for (int i = 0; i < 1000; i++) {
            spam.update(true, true, 0, A, 20);
            check(!spam.poll(true, 0, A, 20), "Packet count must not advance the timer");
        }
        check(!spam.poll(true, 0, A, 61), "Missing heartbeat must cancel a charge");
        holdWithoutShot(spam, 61, 100, 0, A);
        spam.update(true, true, 0, A, 101);
        check(spam.poll(true, 0, A, 101), "Resuming after a timeout requires a full new charge");

        SpikeTrigger interrupted = new SpikeTrigger();
        holdWithoutShot(interrupted, 0, 39, 0, A);
        check(!interrupted.poll(false, 0, A, 40), "Leaving scope/reloading must prevent firing");
        holdWithoutShot(interrupted, 41, 80, 0, A);
        interrupted.update(true, true, 0, A, 81);
        check(interrupted.poll(true, 0, A, 81), "Invalid state must reset elapsed time");
        interrupted.update(true, false, 0, A, 82);
        holdWithoutShot(interrupted, 83, 150, 0, A);
        check(!interrupted.poll(true, 0, A, 150), "Reloading while holding must not rearm after a shot");

        SpikeTrigger changed = new SpikeTrigger();
        holdWithoutShot(changed, 0, 39, 0, A);
        check(!changed.poll(true, 1, A, 40), "Actual slot change invalidates stale input");
        holdWithoutShot(changed, 40, 79, 1, A);
        check(!changed.poll(true, 1, B, 80), "Another launcher in the same slot cannot use old charge");
        holdWithoutShot(changed, 80, 119, 1, B);
        changed.update(true, true, 1, B, 120);
        check(changed.poll(true, 1, B, 120), "The replacement launcher gets its own charge");

        SpikeTrigger empty = new SpikeTrigger();
        for (int tick = 0; tick < 60; tick++) {
            empty.update(true, false, 0, A, tick);
            check(!empty.poll(false, 0, A, tick), "Empty/not-ready launcher cannot fire");
        }
        holdWithoutShot(empty, 60, 99, 0, A);
        empty.update(true, true, 0, A, 100);
        check(empty.poll(true, 0, A, 100), "Time spent without ammo does not count");

        SpikeTrigger reset = new SpikeTrigger();
        holdWithoutShot(reset, 100, 139, 0, A);
        holdWithoutShot(reset, 1, 40, 0, A);
        reset.update(true, true, 0, A, 41);
        check(reset.poll(true, 0, A, 41), "Reset player tick counter starts a new charge");
        System.out.println("PASS: " + checks + " trigger checks; delay, cancel, repeat, spam, timeout, state/launcher changes");
    }
}
