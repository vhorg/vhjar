package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.RampageConfig;

public class RampageDotConfig extends RampageConfig {
   @Expose
   private final int dotSecondDuration;

   public RampageDotConfig(int cost, int damageIncrease, int durationTicks, int cooldown, int dotSecondDuration) {
      super(cost, damageIncrease, durationTicks, cooldown);
      this.dotSecondDuration = dotSecondDuration;
   }

   public int getDotSecondDuration() {
      return this.dotSecondDuration;
   }
}
