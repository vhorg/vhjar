package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;

public class ThornsDamageTalent extends PlayerTalent {
   @Expose
   private final float additionalThornsDamage;

   public ThornsDamageTalent(int cost, float additionalThornsDamage) {
      super(cost);
      this.additionalThornsDamage = additionalThornsDamage;
   }

   public float getAdditionalThornsDamage() {
      return this.additionalThornsDamage;
   }
}
