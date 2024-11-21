package iskallia.vault.util;

import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class LootInitialization {
   private static final RandomSource rand = JavaRandom.ofNanoTime();

   public static ItemStack initializeVaultLoot(ItemStack stack, @Nullable Vault vault, @Nullable BlockPos pos) {
      return initializeVaultLoot(stack, vault, pos, rand);
   }

   public static ItemStack initializeVaultLoot(ItemStack stack, @Nullable Vault vault, @Nullable BlockPos pos, RandomSource random) {
      stack = stack.copy();
      VaultLevelItem.doInitializeVaultLoot(stack, vault, pos);
      stack = DataTransferItem.doConvertStack(stack, random);
      DataInitializationItem.doInitialize(stack, random);
      return stack;
   }

   public static ItemStack initializeVaultLoot(ItemStack stack, int level) {
      return initializeVaultLoot(stack, level, rand);
   }

   public static ItemStack initializeVaultLoot(ItemStack stack, int level, RandomSource random) {
      stack = stack.copy();
      VaultLevelItem.doInitializeVaultLoot(stack, level);
      stack = DataTransferItem.doConvertStack(stack, random);
      DataInitializationItem.doInitialize(stack, random);
      return stack;
   }
}
