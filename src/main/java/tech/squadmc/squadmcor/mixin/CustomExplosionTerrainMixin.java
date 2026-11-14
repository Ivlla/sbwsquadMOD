package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.tools.CustomExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import tech.squadmc.squadmcor.explosion.BlockDestructionBudget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/** SBW 0.8.9.2: filter destruction before loot and onBlockExploded, including delayed tiers. */
@Mixin(value = CustomExplosion.class, remap = false)
public abstract class CustomExplosionTerrainMixin {
    @Shadow @Final private Level level;
    @Unique private static final TagKey<Block> SQUADMC$PROTECTED = TagKey.create(
            Registries.BLOCK, new ResourceLocation("squadmc", "explosion_protected"));
    @Unique private final BlockDestructionBudget squadmc$budget = new BlockDestructionBudget();
    // Separate RNG: selecting terrain must not advance the world's damage/physics RNG.
    @Unique private final Random squadmc$terrainRandom = new Random();

    @ModifyVariable(method = "processBlockList", at = @At("HEAD"), argsOnly = true)
    private Collection<BlockPos> squadmc$lessTerrainDamage(Collection<BlockPos> blocks) {
        ArrayList<BlockPos> eligible = new ArrayList<>();
        for (BlockPos pos : blocks) {
            var state = level.getBlockState(pos);
            if (!state.isAir() && !state.is(SQUADMC$PROTECTED)) eligible.add(pos.immutable());
        }
        Collections.shuffle(eligible, squadmc$terrainRandom);
        return new ArrayList<>(eligible.subList(0, squadmc$budget.take(eligible.size())));
    }
}
