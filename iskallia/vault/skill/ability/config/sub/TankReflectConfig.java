package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.TankConfig;

public class TankReflectConfig extends TankConfig {
   @Expose
   private final float damageReflectChance;
   @Expose
   private final float damageReflectPercent;

   public TankReflectConfig(int cost, int durationTicks, float damageReductionPercent, float damageReflectChance, float damageReflectPercent) {
      super(cost, durationTicks, damageReductionPercent);
      this.damageReflectChance = damageReflectChance;
      this.damageReflectPercent = damageReflectPercent;
   }

   public float getDamageReflectChance() {
      return this.damageReflectChance;
   }

   public float getDamageReflectPercent() {
      return this.damageReflectPercent;
   }
}
