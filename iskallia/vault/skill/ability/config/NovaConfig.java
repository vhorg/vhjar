package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.ability.special.NovaRadiusModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.skill.ability.config.spi.AbstractInstantManaConfig;
import net.minecraft.world.entity.player.Player;

public class NovaConfig extends AbstractInstantManaConfig {
   @Expose
   private final float radius;
   @Expose
   private final float percentAttackDamageDealt;
   @Expose
   private final float knockbackStrengthMultiplier;

   public NovaConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCost,
      float radius,
      float percentAttackDamageDealt,
      float knockbackStrengthMultiplier
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost);
      this.radius = radius;
      this.percentAttackDamageDealt = percentAttackDamageDealt;
      this.knockbackStrengthMultiplier = knockbackStrengthMultiplier;
   }

   public float getUnmodifiedRadius() {
      return this.radius;
   }

   public float getRadius(Player player) {
      float realRadius = this.getUnmodifiedRadius();

      for (ConfiguredModification<FloatValueConfig, NovaRadiusModification> mod : SpecialAbilityModification.getModifications(
         player, NovaRadiusModification.class
      )) {
         realRadius = mod.modification().adjustRadius(mod.config(), realRadius);
      }

      return realRadius;
   }

   public float getPercentAttackDamageDealt() {
      return this.percentAttackDamageDealt;
   }

   public float getKnockbackStrengthMultiplier() {
      return this.knockbackStrengthMultiplier;
   }
}
