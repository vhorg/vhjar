package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class ResistanceTalent extends PlayerTalent {
   @Expose
   private final float percentDamageReduction;

   public ResistanceTalent(int cost, float percentDamageReduction) {
      super(cost);
      this.percentDamageReduction = percentDamageReduction;
   }

   public float getPercentDamageReduction() {
      return this.percentDamageReduction;
   }
}
