package iskallia.vault.config.entry.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.recipe.JewelCraftingRecipe;
import iskallia.vault.init.ModItems;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ConfigJewelCraftingRecipe extends ConfigForgeRecipe<JewelCraftingRecipe> {
   @Expose
   private ResourceLocation jewelAttribute;
   @Expose
   private int size;

   public ConfigJewelCraftingRecipe(ResourceLocation jewelAttribute, int size) {
      super(VaultMod.id("jewel_crafting"), new ItemStack(ModItems.JEWEL));
      this.jewelAttribute = jewelAttribute;
      this.size = size;
   }

   public JewelCraftingRecipe makeRecipe() {
      ItemStack out = this.output.createItemStack();
      List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
      return new JewelCraftingRecipe(this.id, out, in, this.jewelAttribute, this.size);
   }
}
