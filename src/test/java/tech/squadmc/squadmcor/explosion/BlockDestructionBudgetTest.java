package tech.squadmc.squadmcor.explosion;

/** Standalone regression test: javac/java, no Minecraft runtime required. */
public final class BlockDestructionBudgetTest {
    public static void main(String[] args) {
        for (int batchSize = 1; batchSize <= 31; batchSize++) {
            BlockDestructionBudget budget = new BlockDestructionBudget();
            int eligible = 0;
            int destroyed = 0;
            for (int batch = 0; batch < 100; batch++) {
                eligible += batchSize;
                destroyed += budget.take(batchSize, 8);
                if (destroyed != eligible / 8) throw new AssertionError("Delayed batch budget drift");
                if (budget.take(0, 8) != 0) throw new AssertionError("Empty batch consumed budget");
            }
        }
        BlockDestructionBudget first = new BlockDestructionBudget();
        first.take(1, 8);
        if (new BlockDestructionBudget().take(1, 8) != 0) throw new AssertionError("Shared explosion budget");
        System.out.println("Block destruction budget: 3,100 batch checks passed");
    }
}
