package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.RampageConfig;

public class RampageLeechConfig extends RampageConfig {
   @Expose
   private float leechPercent;

   public RampageLeechConfig(int cost, int durationTicks, int cooldown, float leechPercent) {
      super(cost, 0.0F, durationTicks, cooldown);
      this.leechPercent = leechPercent;
   }

   public float getLeechPercent() {
      return this.leechPercent;
   }
}
