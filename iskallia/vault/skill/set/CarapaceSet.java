package iskallia.vault.skill.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;

public class CarapaceSet extends PlayerSet {
   @Expose
   private final float absorptionPercent;

   public CarapaceSet(float absorptionPercent) {
      super(VaultGear.Set.CARAPACE);
      this.absorptionPercent = absorptionPercent;
   }

   public float getAbsorptionPercent() {
      return this.absorptionPercent;
   }
}
