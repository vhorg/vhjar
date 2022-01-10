package iskallia.vault.skill.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;

public class PorcupineSet extends PlayerSet {
   @Expose
   protected float additionalThornsChance;
   @Expose
   protected float additionalThornsDamage;

   public PorcupineSet(float additionalThornsChance, float additionalThornsDamage) {
      super(VaultGear.Set.PORCUPINE);
      this.additionalThornsChance = additionalThornsChance;
      this.additionalThornsDamage = additionalThornsDamage;
   }

   public float getAdditionalThornsChance() {
      return this.additionalThornsChance;
   }

   public float getAdditionalThornsDamage() {
      return this.additionalThornsDamage;
   }
}
