package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.LootInitialization;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class JewelForgeRecipe extends VaultForgeRecipe {
   public JewelForgeRecipe(ResourceLocation id, ItemStack output) {
      super(ForgeRecipeType.JEWEL, id, output);
   }

   public JewelForgeRecipe(ResourceLocation id, ItemStack output, List<ItemStack> inputs) {
      super(ForgeRecipeType.JEWEL, id, output, inputs);
   }

   @Override
   public boolean usesLevel() {
      return true;
   }

   @Override
   public ItemStack getDisplayOutput(int vaultLevel) {
      ItemStack jewelStack = new ItemStack(ModItems.JEWEL);
      VaultGearData gearData = VaultGearData.read(jewelStack);
      gearData.setItemLevel(vaultLevel);
      gearData.write(jewelStack);
      return jewelStack;
   }

   @Override
   public ItemStack createOutput(List<OverSizedItemStack> consumed, ServerPlayer crafter, int vaultLevel) {
      ItemStack jewelStack = new ItemStack(ModItems.JEWEL);
      jewelStack.getOrCreateTag().putString(ModGearAttributes.GEAR_ROLL_TYPE_POOL.getRegistryName().toString(), "jewel_crafted");
      return LootInitialization.initializeVaultLoot(jewelStack, vaultLevel);
   }
}
