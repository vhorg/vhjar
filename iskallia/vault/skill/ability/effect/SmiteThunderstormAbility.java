package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.effect.spi.AbstractSmiteAbility;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;

public class SmiteThunderstormAbility extends AbstractSmiteAbility {
   public SmiteThunderstormAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCostPerSecond,
      float radius,
      int intervalTicks,
      float playerDamagePercent,
      int color,
      float additionalManaPerBolt
   ) {
      super(
         unlockLevel,
         learnPointCost,
         regretPointCost,
         cooldownTicks,
         manaCostPerSecond,
         radius,
         intervalTicks,
         playerDamagePercent,
         color,
         additionalManaPerBolt
      );
   }

   public SmiteThunderstormAbility() {
   }

   @Override
   public ToggleAbilityEffect getEffect() {
      return ModEffects.SMITE_THUNDERSTORM;
   }
}
