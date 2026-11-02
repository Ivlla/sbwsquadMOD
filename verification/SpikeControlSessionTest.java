import tech.squadmc.squadmcor.spike.SpikeControlSession;

public final class SpikeControlSessionTest {
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        SpikeControlSession a = new SpikeControlSession();
        check(!a.isActive(0, 0), "No control before a scope packet");
        a.update(true, 0, 100);
        long launchSession = a.revision();
        a.update(true, 0, 101);
        check(a.isActive(0, 101) && a.revision() == launchSession, "Steady ADS keeps control");
        a.update(false, 0, 102);
        check(!a.isActive(0, 102), "Leaving ADS stops control");
        a.update(true, 0, 102);
        check(a.isActive(0, 102) && a.revision() != launchSession,
                "Off/on within the same tick cannot reconnect the old missile");
        long secondSession = a.revision();
        a.update(true, 1, 103);
        check(!a.isActive(0, 103) && a.revision() != secondSession, "Switching slots breaks the link");
        long thirdSession = a.revision();
        check(!a.isActive(1, 114), "Missing heartbeat expires control");
        a.update(true, 1, 114);
        check(a.revision() != thirdSession, "Late heartbeat cannot revive an expired session");
        SpikeControlSession b = new SpikeControlSession();
        b.update(true, 2, 50);
        long otherPlayerSession = b.revision();
        a.update(false, 1, 115);
        check(b.isActive(2, 50) && b.revision() == otherPlayerSession, "Players are independent");
        b.update(true, 2, 1);
        check(b.revision() != otherPlayerSession, "A reset tick counter invalidates old state");
        System.out.println("PASS: control sessions, off/on, slot change, timeout, player isolation, tick reset");
    }
}
