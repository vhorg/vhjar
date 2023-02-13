package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.util.SidedHelper;
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
   public ItemStack getDisplayOutput() {
      return new ItemStack(ModItems.JEWEL);
   }

   @Override
   public ItemStack createOutput(List<OverSizedItemStack> consumed, ServerPlayer crafter) {
      ItemStack jewelStack = new ItemStack(ModItems.JEWEL);
      VaultGearData gearData = VaultGearData.read(jewelStack);
      gearData.setItemLevel(SidedHelper.getVaultLevel(crafter));
      gearData.write(jewelStack);
      jewelStack.getOrCreateTag().putString(ModGearAttributes.GEAR_ROLL_TYPE_POOL.getRegistryName().toString(), "jewel_crafted");
      jewelStack = ModItems.JEWEL.convertStack(jewelStack, JavaRandom.ofNanoTime());
      DataInitializationItem.doInitialize(jewelStack);
      return jewelStack;
   }
}