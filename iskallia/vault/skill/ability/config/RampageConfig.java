package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.ability.special.RampageDamageModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.skill.ability.config.spi.AbstractToggleManaConfig;
import net.minecraft.world.entity.player.Player;

public class RampageConfig extends AbstractToggleManaConfig {
   @Expose
   private final float damageIncrease;

   public RampageConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCostPerSecond, float damageIncrease) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond);
      this.damageIncrease = damageIncrease;
   }

   public float getUnmodifiedDamageIncrease() {
      return this.damageIncrease;
   }

   public float getDamageIncrease(Player player) {
      float incDamage = this.getUnmodifiedDamageIncrease();

      for (ConfiguredModification<FloatValueConfig, RampageDamageModification> mod : SpecialAbilityModification.getModifications(
         player, RampageDamageModification.class
      )) {
         incDamage = mod.modification().adjustDamageIncrease(mod.config(), incDamage);
      }

      return incDamage;
   }
}
