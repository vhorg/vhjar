package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.skill.ability.config.TauntConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;

public abstract class AbstractTauntAbility<C extends TauntConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Taunt";
   }
}
