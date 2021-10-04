package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class ThornsChanceTalent extends PlayerTalent {
   @Expose
   private final float additionalThornsChance;

   public ThornsChanceTalent(int cost, float additionalThornsChance) {
      super(cost);
      this.additionalThornsChance = additionalThornsChance;
   }

   public float getAdditionalThornsChance() {
      return this.additionalThornsChance;
   }
}
