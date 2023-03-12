package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.ability.special.TauntRadiusModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.skill.ability.config.spi.AbstractInstantManaConfig;
import net.minecraft.world.entity.player.Player;

public class TauntConfig extends AbstractInstantManaConfig {
   @Expose
   private final float radius;
   @Expose
   private final int durationTicks;

   public TauntConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, float radius, int durationTicks) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost);
      this.radius = radius;
      this.durationTicks = durationTicks;
   }

   public float getUnmodifiedRadius() {
      return this.radius;
   }

   public float getRadius(Player player) {
      float realRadius = this.getUnmodifiedRadius();

      for (ConfiguredModification<FloatValueConfig, TauntRadiusModification> mod : SpecialAbilityModification.getModifications(
         player, TauntRadiusModification.class
      )) {
         realRadius = mod.modification().adjustRadius(mod.config(), realRadius);
      }

      return realRadius;
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }
}
