package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.config.MegaJumpConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;

public abstract class AbstractMegaJumpAbility<C extends MegaJumpConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Mega Jump";
   }

   protected boolean canBreakBlock(BlockState state) {
      return !state.isAir()
         && (!state.requiresCorrectToolForDrops() || TierSortingRegistry.isCorrectTierForDrops(Tiers.IRON, state))
         && ModConfigs.ABILITIES_DRILL_DIG_DENY_CONFIG.isBlockAllowed(state.getBlock());
   }
}
