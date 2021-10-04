package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;

public class MegaJumpConfig extends AbilityConfig {
   @Expose
   private final int height;

   public MegaJumpConfig(int cost, int height) {
      super(cost, AbilityConfig.Behavior.RELEASE_TO_PERFORM);
      this.height = height;
   }

   public int getHeight() {
      return this.height;
   }
}
