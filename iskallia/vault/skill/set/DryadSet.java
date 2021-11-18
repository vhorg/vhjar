package iskallia.vault.skill.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;

public class DryadSet extends PlayerSet {
   @Expose
   private float extraHealth;

   public DryadSet(float extraHealth) {
      super(VaultGear.Set.DRYAD);
      this.extraHealth = extraHealth;
   }

   public float getExtraHealth() {
      return this.extraHealth;
   }
}
