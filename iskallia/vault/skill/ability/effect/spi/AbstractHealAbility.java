package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.skill.ability.config.HealConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;

public abstract class AbstractHealAbility<C extends HealConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Heal";
   }
}
