package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.DashConfig;

public class DashHealConfig extends DashConfig {
   @Expose
   private final float healPerDash;

   public DashHealConfig(int cost, int extraRadius, float healPerDash) {
      super(cost, extraRadius);
      this.healPerDash = healPerDash;
   }

   public float getHealPerDash() {
      return this.healPerDash;
   }
}
