package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModEffects;

public class TankConfig extends EffectConfig {
   @Expose
   private final int durationTicks;
   @Expose
   private final float damageReductionPercent;

   public TankConfig(int cost, int durationTicks, float damageReductionPercent) {
      super(cost, ModEffects.TANK, 0, EffectConfig.Type.ICON_ONLY, AbilityConfig.Behavior.RELEASE_TO_PERFORM);
      this.durationTicks = durationTicks;
      this.damageReductionPercent = damageReductionPercent;
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }

   public float getDamageReductionPercent() {
      return this.damageReductionPercent;
   }
}
