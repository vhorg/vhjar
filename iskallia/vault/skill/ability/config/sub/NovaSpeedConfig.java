package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.NovaConfig;

public class NovaSpeedConfig extends NovaConfig {
   @Expose
   private final int durationTicks;
   @Expose
   private final int amplifier;

   public NovaSpeedConfig(
      int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, float radius, int durationTicks, int amplifier
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost, radius, 0.0F, 0.0F);
      this.durationTicks = durationTicks;
      this.amplifier = amplifier;
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }

   public int getAmplifier() {
      return this.amplifier;
   }
}
