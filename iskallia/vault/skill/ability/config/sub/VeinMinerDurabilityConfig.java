package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.VeinMinerConfig;

public class VeinMinerDurabilityConfig extends VeinMinerConfig {
   @Expose
   private final float noDurabilityUsageChance;

   public VeinMinerDurabilityConfig(int cost, int blockLimit, float noDurabilityUsageChance) {
      super(cost, blockLimit);
      this.noDurabilityUsageChance = noDurabilityUsageChance;
   }

   public float getNoDurabilityUsageChance() {
      return this.noDurabilityUsageChance;
   }
}
