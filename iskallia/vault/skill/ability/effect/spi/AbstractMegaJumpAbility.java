package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;

public abstract class AbstractMegaJumpAbility extends InstantManaAbility {
   public AbstractMegaJumpAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
   }

   protected AbstractMegaJumpAbility() {
   }

   protected boolean canBreakBlock(BlockState state) {
      return !state.isAir()
         && (!state.requiresCorrectToolForDrops() || TierSortingRegistry.isCorrectTierForDrops(Tiers.IRON, state))
         && ModConfigs.ABILITIES_DRILL_DIG_DENY_CONFIG.isBlockAllowed(state.getBlock());
   }
}
