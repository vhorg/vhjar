package iskallia.vault.skill.talent.type.archetype;

import com.google.gson.annotations.Expose;

public class BarbaricTalent extends ArchetypeTalent {
   @Expose
   protected int rageDegenTickDelay;
   @Expose
   protected float damageMultiplierPerRage;
   @Expose
   protected int ragePerAttack;

   public BarbaricTalent(int cost, int rageDegenTickDelay, float damageMultiplierPerRage, int ragePerAttack) {
      super(cost);
      this.rageDegenTickDelay = rageDegenTickDelay;
      this.damageMultiplierPerRage = damageMultiplierPerRage;
      this.ragePerAttack = ragePerAttack;
   }

   public int getRageDegenTickDelay() {
      return this.rageDegenTickDelay;
   }

   public float getDamageMultiplierPerRage() {
      return this.damageMultiplierPerRage;
   }

   public int getRagePerAttack() {
      return this.ragePerAttack;
   }
}
