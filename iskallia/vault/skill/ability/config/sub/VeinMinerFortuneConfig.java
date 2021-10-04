package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.VeinMinerConfig;

public class VeinMinerFortuneConfig extends VeinMinerConfig {
   @Expose
   private final int additionalFortuneLevel;

   public VeinMinerFortuneConfig(int cost, int blockLimit, int additionalFortuneLevel) {
      super(cost, blockLimit);
      this.additionalFortuneLevel = additionalFortuneLevel;
   }

   public int getAdditionalFortuneLevel() {
      return this.additionalFortuneLevel;
   }
}
