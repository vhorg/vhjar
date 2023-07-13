package iskallia.vault.skill.ability.effect;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.skill.ability.effect.spi.AbstractSmiteAbility;

public class SmiteAbility extends AbstractSmiteAbility {
   public SmiteAbility(
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

   @Override
   public ActiveFlags getFlag() {
      return ActiveFlags.IS_SMITE_BASE_ATTACKING;
   }

   public SmiteAbility() {
   }
}
