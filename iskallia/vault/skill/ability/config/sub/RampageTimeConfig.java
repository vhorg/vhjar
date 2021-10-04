package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.RampageConfig;

public class RampageTimeConfig extends RampageConfig {
   @Expose
   private final int tickTimeIncreasePerHit;

   public RampageTimeConfig(int cost, int damageIncrease, int durationTicks, int cooldown, int tickTimeIncreasePerHit) {
      super(cost, damageIncrease, durationTicks, cooldown);
      this.tickTimeIncreasePerHit = tickTimeIncreasePerHit;
   }

   public int getTickTimeIncreasePerHit() {
      return this.tickTimeIncreasePerHit;
   }
}
