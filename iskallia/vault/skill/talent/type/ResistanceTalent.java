package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class ResistanceTalent extends PlayerTalent {
   @Expose
   protected float additionalResistanceLimit;

   public ResistanceTalent(int cost, float additionalResistanceLimit) {
      super(cost);
      this.additionalResistanceLimit = additionalResistanceLimit;
   }

   public float getAdditionalResistanceLimit() {
      return this.additionalResistanceLimit;
   }
}
