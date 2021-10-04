package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;

public class AbilityConfig {
   @Expose
   private final int learningCost;
   @Expose
   private final AbilityConfig.Behavior behavior;
   @Expose
   private final int cooldown;
   @Expose
   private final int levelRequirement;

   public AbilityConfig(int learningCost, AbilityConfig.Behavior behavior) {
      this(learningCost, behavior, 200);
   }

   public AbilityConfig(int learningCost, AbilityConfig.Behavior behavior, int cooldown) {
      this(learningCost, behavior, cooldown, 0);
   }

   public AbilityConfig(int learningCost, AbilityConfig.Behavior behavior, int cooldown, int levelRequirement) {
      this.learningCost = learningCost;
      this.behavior = behavior;
      this.cooldown = cooldown;
      this.levelRequirement = levelRequirement;
   }

   public int getLearningCost() {
      return this.learningCost;
   }

   public AbilityConfig.Behavior getBehavior() {
      return this.behavior;
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public int getLevelRequirement() {
      return this.levelRequirement;
   }

   public static enum Behavior {
      HOLD_TO_ACTIVATE,
      PRESS_TO_TOGGLE,
      RELEASE_TO_PERFORM;
   }
}
