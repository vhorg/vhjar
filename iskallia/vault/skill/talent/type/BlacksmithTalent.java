package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class BlacksmithTalent extends PlayerTalent {
   @Expose
   private float chanceToNotConsumePotential;

   public BlacksmithTalent(int cost, float chanceToNotConsumePotential) {
      super(cost);
      this.chanceToNotConsumePotential = chanceToNotConsumePotential;
   }

   public float getChanceToNotConsumePotential() {
      return this.chanceToNotConsumePotential;
   }
}
