package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class AbsorptionTalent extends PlayerTalent {
   @Expose
   private float increasedAbsorptionLimit;

   public AbsorptionTalent(int cost, float increasedAbsorptionLimit) {
      super(cost);
      this.increasedAbsorptionLimit = increasedAbsorptionLimit;
   }

   public float getIncreasedAbsorptionLimit() {
      return this.increasedAbsorptionLimit;
   }
}
