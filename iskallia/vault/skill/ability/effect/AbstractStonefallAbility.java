package iskallia.vault.skill.ability.effect;

import iskallia.vault.skill.ability.config.StonefallConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;

public class AbstractStonefallAbility<C extends StonefallConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Stonefall";
   }
}
