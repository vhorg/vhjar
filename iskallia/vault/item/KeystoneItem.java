package iskallia.vault.item;

import iskallia.vault.core.vault.influence.VaultGod;
import net.minecraft.resources.ResourceLocation;

public class KeystoneItem extends BasicItem {
   private VaultGod god;

   public KeystoneItem(ResourceLocation id, VaultGod god) {
      super(id);
      this.god = god;
   }

   public VaultGod getGod() {
      return this.god;
   }
}
