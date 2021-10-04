package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.VeinMinerConfig;

public class VeinMinerSizeDurabilityConfig extends VeinMinerConfig {
   @Expose
   private final float doubleDurabilityCostChance;

   public VeinMinerSizeDurabilityConfig(int cost, int blockLimit, float doubleDurabilityCostChance) {
      super(cost, blockLimit);
      this.doubleDurabilityCostChance = doubleDurabilityCostChance;
   }

   public float getDoubleDurabilityCostChance() {
      return this.doubleDurabilityCostChance;
   }
}
