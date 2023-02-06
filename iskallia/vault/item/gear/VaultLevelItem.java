package iskallia.vault.item.gear;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.world.data.ServerVaults;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public interface VaultLevelItem {
   static void doInitializeVaultLoot(ItemStack stack, ServerLevel world, @Nullable BlockPos pos) {
      if (!stack.isEmpty() && stack.getItem() instanceof VaultLevelItem) {
         doInitializeVaultLoot(stack, ServerVaults.get(world).orElse(null), pos);
      }
   }

   static void doInitializeVaultLoot(ItemStack stack, @Nullable Vault vault, @Nullable BlockPos pos) {
      if (!stack.isEmpty() && stack.getItem() instanceof VaultLevelItem vaultLevelItem && vault != null) {
         vaultLevelItem.initializeVaultLoot(vault, stack, pos);
      }
   }

   void initializeVaultLoot(Vault var1, ItemStack var2, @Nullable BlockPos var3);
}
