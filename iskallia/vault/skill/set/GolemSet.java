package iskallia.vault.skill.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;

public class GolemSet extends PlayerSet {
   @Expose
   private final float bonusResistance;
   @Expose
   private final float bonusResistanceCap;

   public GolemSet(float bonusResistance, float bonusResistanceCap) {
      super(VaultGear.Set.GOLEM);
      this.bonusResistance = bonusResistance;
      this.bonusResistanceCap = bonusResistanceCap;
   }

   public float getBonusResistance() {
      return this.bonusResistance;
   }

   public float getBonusResistanceCap() {
      return this.bonusResistanceCap;
   }
}
