package iskallia.vault.skill.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;

public class DragonSet extends PlayerSet {
   public static int MULTIPLIER_ID = -1;
   @Expose
   private float damageMultiplier;

   public DragonSet(float damageMultiplier) {
      super(VaultGear.Set.DRAGON);
      this.damageMultiplier = damageMultiplier;
   }

   public float getDamageMultiplier() {
      return this.damageMultiplier;
   }
}
