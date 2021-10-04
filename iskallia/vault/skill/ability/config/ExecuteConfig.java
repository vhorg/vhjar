package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModEffects;

public class ExecuteConfig extends EffectConfig {
   @Expose
   private final float healthPercentage;
   @Expose
   private final int effectDuration;

   public ExecuteConfig(int cost, AbilityConfig.Behavior behavior, float healthPercentage, int effectDuration) {
      super(cost, ModEffects.EXECUTE, 0, EffectConfig.Type.ICON_ONLY, behavior);
      this.healthPercentage = healthPercentage;
      this.effectDuration = effectDuration;
   }

   public float getHealthPercentage() {
      return this.healthPercentage;
   }

   public int getEffectDuration() {
      return this.effectDuration;
   }
}
