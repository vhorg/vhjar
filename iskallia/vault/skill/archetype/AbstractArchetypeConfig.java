package iskallia.vault.skill.archetype;

import com.google.gson.annotations.Expose;

public abstract class AbstractArchetypeConfig {
   @Expose
   protected final int learningCost;
   @Expose
   protected final int levelRequirement;

   protected AbstractArchetypeConfig(int learningCost, int levelRequirement) {
      this.learningCost = learningCost;
      this.levelRequirement = levelRequirement;
   }

   public int getLearningCost() {
      return this.learningCost;
   }

   public int getLevelRequirement() {
      return this.levelRequirement;
   }
}
