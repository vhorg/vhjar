package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.skill.ability.effect.spi.core.ToggleManaAbility;

public class AbstractShellAbility extends ToggleManaAbility {
   public AbstractShellAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCostPerSecond) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond);
   }

   public AbstractShellAbility() {
   }
}