package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class SoulShardTalent extends PlayerTalent {
   @Expose
   protected final float additionalSoulShardChance;

   public SoulShardTalent(int cost, float additionalSoulShardChance) {
      super(cost);
      this.additionalSoulShardChance = additionalSoulShardChance;
   }

   public float getAdditionalSoulShardChance() {
      return this.additionalSoulShardChance;
   }
}
