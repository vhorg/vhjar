package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class LowManaHealingEfficiencyTalent extends LowManaTalent {
   @Expose
   private float additionalHealingEfficiency;

   public LowManaHealingEfficiencyTalent(int cost, float healthThreshold, float additionalHealingEfficiency) {
      super(cost, healthThreshold);
      this.additionalHealingEfficiency = additionalHealingEfficiency;
   }

   public float getAdditionalHealingEfficiency() {
      return this.additionalHealingEfficiency;
   }
}
