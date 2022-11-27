package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.skill.ability.config.MegaJumpConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;

public abstract class AbstractMegaJumpAbility<C extends MegaJumpConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Mega Jump";
   }
}
