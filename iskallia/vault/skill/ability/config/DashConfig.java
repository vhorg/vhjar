package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;

public class DashConfig extends AbilityConfig {
   @Expose
   private final int extraRadius;

   public DashConfig(int cost, int extraRadius) {
      super(cost, AbilityConfig.Behavior.RELEASE_TO_PERFORM);
      this.extraRadius = extraRadius;
   }

   public int getExtraRadius() {
      return this.extraRadius;
   }
}
