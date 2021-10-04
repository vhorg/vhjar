package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;

public class VeinMinerConfig extends AbilityConfig {
   @Expose
   private final int blockLimit;

   public VeinMinerConfig(int cost, int blockLimit) {
      super(cost, AbilityConfig.Behavior.HOLD_TO_ACTIVATE);
      this.blockLimit = blockLimit;
   }

   public int getBlockLimit() {
      return this.blockLimit;
   }
}
