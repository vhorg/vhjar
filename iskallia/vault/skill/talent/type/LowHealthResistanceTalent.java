package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class LowHealthResistanceTalent extends LowHealthTalent {
   @Expose
   private float additionalResistance;

   public LowHealthResistanceTalent(int cost, float healthThreshold, float additionalResistance) {
      super(cost, healthThreshold);
      this.additionalResistance = additionalResistance;
   }

   public float getAdditionalResistance() {
      return this.additionalResistance;
   }
}
