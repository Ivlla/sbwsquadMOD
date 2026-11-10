package tech.squadmc.squadmcor.performance;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder("squadmc")
@PrefixGameTestTemplate(false)
public class ChunkLoadingGameTest {
    @GameTest(template = "empty", templateNamespace = "forge", timeoutTicks = 40)
    public static void tickGate(GameTestHelper helper) {
        for (String name : new String[]{"projectile", "cannon_shell", "small_rocket", "wire_guide_missile"}) {
            Entity entity = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("superbwarfare", name)).create(helper.getLevel());
            entity.setPos(-16000, 250, -16000);
            entity.setDeltaMovement(20, 0, 0);
            helper.getLevel().tickNonPassenger(entity);
            helper.assertTrue(entity.tickCount == 0, "Unloaded chunk must gate complete tick: " + name);
            helper.assertTrue(entity.getX() == -16000, "Projectile moved into an unloaded chunk: " + name);
            helper.assertTrue(!entity.isAlwaysTicking(), "SBW manual/always tick path must be disabled: " + name);
        }
        helper.succeed();
    }

    @GameTest(template = "empty", templateNamespace = "forge", timeoutTicks = 2400)
    public static void asynchronousLoadingResumesBullet(GameTestHelper helper) {
        // GameTest can tick faster than wall time while world-generation workers are still busy.
        // Test eventual asynchronous completion independently from the production overload timeout.
        int previousWait = PerformanceConfig.WAIT_TICKS.get();
        PerformanceConfig.WAIT_TICKS.set(1200);
        Entity bullet = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("superbwarfare", "projectile"))
                .create(helper.getLevel());
        bullet.setPos(-16000, 250, -16000);
        bullet.setDeltaMovement(20, 0, 0);
        helper.getLevel().tickNonPassenger(bullet);
        helper.assertTrue(bullet.tickCount == 0, "Expected an initially unloaded destination");
        helper.succeedWhen(() -> {
            helper.getLevel().tickNonPassenger(bullet);
            helper.assertTrue(!bullet.isRemoved(), "Bullet timed out before chunks became ready");
            helper.assertTrue(bullet.tickCount > 0, "Waiting for asynchronous entity-ticking chunks");
            helper.assertTrue(bullet.getX() > -16000, "Bullet must resume its original motion");
            PerformanceConfig.WAIT_TICKS.set(previousWait);
        });
    }
}
