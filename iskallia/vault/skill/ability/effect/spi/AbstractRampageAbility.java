package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.skill.ability.config.RampageConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractToggleManaAbility;

public abstract class AbstractRampageAbility<C extends RampageConfig> extends AbstractToggleManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Rampage";
   }
}
