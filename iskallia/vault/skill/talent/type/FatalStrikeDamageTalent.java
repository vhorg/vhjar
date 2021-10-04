package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class FatalStrikeDamageTalent extends PlayerTalent {
   @Expose
   private final float additionalFatalStrikeDamage;

   public FatalStrikeDamageTalent(int cost, float additionalFatalStrikeDamage) {
      super(cost);
      this.additionalFatalStrikeDamage = additionalFatalStrikeDamage;
   }

   public float getAdditionalFatalStrikeDamage() {
      return this.additionalFatalStrikeDamage;
   }
}
