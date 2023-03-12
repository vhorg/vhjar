package iskallia.vault.gear.crafting.recipe;

import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GearForgeRecipe extends VaultForgeRecipe {
   public GearForgeRecipe(ResourceLocation id, ItemStack output) {
      super(ForgeRecipeType.GEAR, id, output);
   }

   public GearForgeRecipe(ResourceLocation id, ItemStack output, List<ItemStack> inputs) {
      super(ForgeRecipeType.GEAR, id, output, inputs);
   }

   @Override
   public boolean usesLevel() {
      return true;
   }

   @Override
   public ItemStack getDisplayOutput(int vaultLevel) {
      ItemStack out = super.getDisplayOutput(vaultLevel);
      VaultGearData data = VaultGearData.read(out);
      data.setState(VaultGearState.IDENTIFIED);
      data.setItemLevel(vaultLevel);
      data.write(out);
      return out;
   }

   @Override
   public ItemStack createOutput(List<OverSizedItemStack> consumed, ServerPlayer crafter, int vaultLevel) {
      ItemStack stack = super.createOutput(consumed, crafter, vaultLevel);
      Item item = stack.getItem();
      if (item instanceof IdolItem) {
         item = MiscUtils.getRandomEntry(ModItems.IDOL_BENEVOLENT, ModItems.IDOL_MALEVOLENCE, ModItems.IDOL_OMNISCIENT, ModItems.IDOL_TIMEKEEPER);
      }

      return item instanceof VaultGearItem gearItem ? VaultGearCraftingHelper.doCraftGear(gearItem, crafter, vaultLevel, false) : stack;
   }
}
