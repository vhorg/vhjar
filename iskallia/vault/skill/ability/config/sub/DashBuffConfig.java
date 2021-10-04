package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.DashConfig;

public class DashBuffConfig extends DashConfig {
   @Expose
   private final float damageIncreasePerDash;
   @Expose
   private final int damageIncreaseTickTime;

   public DashBuffConfig(int cost, int extraRadius, float damageIncreasePerDash, int damageIncreaseTickTime) {
      super(cost, extraRadius);
      this.damageIncreasePerDash = damageIncreasePerDash;
      this.damageIncreaseTickTime = damageIncreaseTickTime;
   }

   public float getDamageIncreasePerDash() {
      return this.damageIncreasePerDash;
   }

   public int getDamageIncreaseTickTime() {
      return this.damageIncreaseTickTime;
   }
}
