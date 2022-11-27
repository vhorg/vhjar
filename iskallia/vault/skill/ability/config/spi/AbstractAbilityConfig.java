package iskallia.vault.skill.ability.config.spi;

import com.google.gson.annotations.Expose;

public abstract class AbstractAbilityConfig {
   @Expose
   private final int learningCost;
   @Expose
   private final int regretCost;
   @Expose
   private final int cooldownTicks;
   @Expose
   private final int levelRequirement;

   public AbstractAbilityConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement) {
      this.learningCost = learningCost;
      this.cooldownTicks = cooldownTicks;
      this.levelRequirement = levelRequirement;
      this.regretCost = regretCost;
   }

   public int getLearningCost() {
      return this.learningCost;
   }

   public int getRegretCost() {
      return this.regretCost;
   }

   public int getCooldownTicks() {
      return this.cooldownTicks;
   }

   public int getLevelRequirement() {
      return this.levelRequirement;
   }
}
