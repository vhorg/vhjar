package iskallia.vault.config.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.recipe.ConfigToolRecipe;
import iskallia.vault.gear.crafting.recipe.ToolForgeRecipe;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.item.tool.ToolMaterial;
import iskallia.vault.item.tool.ToolType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ToolRecipesConfig extends ForgeRecipesConfig<ConfigToolRecipe, ToolForgeRecipe> {
   @Expose
   private final List<ConfigToolRecipe> toolRecipes = new ArrayList<>();

   public ToolRecipesConfig() {
      super(ForgeRecipeType.TOOL);
   }

   @Override
   public List<ConfigToolRecipe> getConfigRecipes() {
      return this.toolRecipes;
   }

   @Override
   protected void reset() {
      this.toolRecipes.clear();
      ToolType[] basicTypes = new ToolType[]{ToolType.PICK, ToolType.AXE, ToolType.SHOVEL, ToolType.HAMMER, ToolType.SICKLE};

      for (ToolMaterial toolMaterial : ToolMaterial.values()) {
         for (ToolType toolType : basicTypes) {
            ItemStack out = ToolItem.create(toolMaterial, toolType);
            ConfigToolRecipe recipe = new ConfigToolRecipe(out, toolType, toolMaterial);
            recipe.addInput(new ItemStack(Items.DIAMOND, 2));
            this.toolRecipes.add(recipe);
         }
      }
   }
}
