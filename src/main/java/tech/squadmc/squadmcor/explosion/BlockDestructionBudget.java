package tech.squadmc.squadmcor.explosion;

/** Counts eligible blocks across all delayed batches of one explosion. */
public final class BlockDestructionBudget {
    private int remainder;

    public int take(int eligible) {
        int total = eligible + remainder;
        remainder = total % 2;
        return total / 2;
    }
}
