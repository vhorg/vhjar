package iskallia.vault.item.gear;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public interface VaultLevelItem {
   static void doInitializeVaultLoot(ItemStack stack, @Nullable Vault vault, @Nullable BlockPos pos) {
      if (!stack.isEmpty() && stack.getItem() instanceof VaultLevelItem vaultLevelItem) {
         vaultLevelItem.initializeVaultLoot(stack, pos, vault);
      }
   }

   default void initializeVaultLoot(ItemStack stack, @Nullable BlockPos pos, @Nullable Vault vault) {
      int level = Optional.ofNullable(vault).flatMap(v -> v.getOptional(Vault.LEVEL)).map(VaultLevel::get).orElse(0);
      this.initializeVaultLoot(level, stack, pos, vault);
   }

   void initializeVaultLoot(int var1, ItemStack var2, @Nullable BlockPos var3, @Nullable Vault var4);
}
