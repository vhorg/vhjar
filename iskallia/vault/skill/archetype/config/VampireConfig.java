package iskallia.vault.skill.archetype.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.archetype.AbstractArchetypeConfig;

public class VampireConfig extends AbstractArchetypeConfig {
   @Expose
   private final float additionalPercentageLifeLeech;

   public VampireConfig(int learningCost, int levelRequirement, float additionalPercentageLifeLeech) {
      super(learningCost, levelRequirement);
      this.additionalPercentageLifeLeech = additionalPercentageLifeLeech;
   }

   public float getAdditionalPercentageLifeLeech() {
      return this.additionalPercentageLifeLeech;
   }
}
