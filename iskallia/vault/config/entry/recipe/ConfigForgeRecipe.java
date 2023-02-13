package iskallia.vault.config.entry.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class ConfigForgeRecipe<T extends VaultForgeRecipe> {
   @Expose
   protected final ResourceLocation id;
   @Expose
   protected final ItemEntry output;
   @Expose
   protected final List<ItemEntry> inputs = new ArrayList<>();

   public ConfigForgeRecipe(ItemStack out) {
      this(out.getItem().getRegistryName(), out);
   }

   public ConfigForgeRecipe(ResourceLocation id, ItemStack out) {
      this.id = id;
      this.output = new ItemEntry(out);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void addInput(ItemStack in) {
      this.inputs.add(new ItemEntry(in));
   }

   public abstract T makeRecipe();
}
