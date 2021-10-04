package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.TankConfig;

public class TankParryConfig extends TankConfig {
   @Expose
   private final float parryChance;

   public TankParryConfig(int cost, int durationTicks, float parryChance) {
      super(cost, durationTicks, 0.0F);
      this.parryChance = parryChance;
   }

   public float getParryChance() {
      return this.parryChance;
   }
}
