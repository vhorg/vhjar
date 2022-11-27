package iskallia.vault.core.vault.stat;

import iskallia.vault.util.VaultRarity;

public class VaultStatInterface {
   private final VaultSnapshot snapshot;

   public VaultStatInterface(VaultSnapshot snapshot) {
      this.snapshot = snapshot;
   }

   public int getLootedChests(VaultChestType type, VaultRarity rarity) {
      return 0;
   }
}
