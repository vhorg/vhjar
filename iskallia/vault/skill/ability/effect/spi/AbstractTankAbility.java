package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.skill.ability.config.TankConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractToggleManaAbility;

public abstract class AbstractTankAbility<C extends TankConfig> extends AbstractToggleManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Tank";
   }
}
