package iskallia.vault.config.entry.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.recipe.ToolForgeRecipe;
import iskallia.vault.item.tool.ToolMaterial;
import iskallia.vault.item.tool.ToolType;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class ConfigToolRecipe extends ConfigForgeRecipe<ToolForgeRecipe> {
   @Expose
   private final ToolType toolType;
   @Expose
   private final ToolMaterial toolMaterial;

   public ConfigToolRecipe(ItemStack output, ToolType toolType, ToolMaterial toolMaterial) {
      super(VaultMod.id("tool/" + toolType.getId() + "/" + toolMaterial.getId()), output);
      this.toolType = toolType;
      this.toolMaterial = toolMaterial;
   }

   public ToolForgeRecipe makeRecipe() {
      ItemStack out = this.output.createItemStack();
      List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
      return new ToolForgeRecipe(this.id, out, in, this.toolType, this.toolMaterial);
   }
}
