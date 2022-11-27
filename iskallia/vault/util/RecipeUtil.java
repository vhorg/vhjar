package iskallia.vault.util;

import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ObjectUtils;

public class RecipeUtil {
   @Nonnull
   public static Optional<Tuple<ItemStack, Float>> findSmeltingResult(Level world, BlockState input) {
      ItemStack stack = new ItemStack(input.getBlock());
      return stack.isEmpty() ? Optional.empty() : findSmeltingResult(world, stack);
   }

   @Nonnull
   public static Optional<Tuple<ItemStack, Float>> findSmeltingResult(Level world, ItemStack input) {
      RecipeManager mgr = world.getRecipeManager();
      Container inv = new SimpleContainer(new ItemStack[]{input});
      Optional<Recipe<Container>> optRecipe = (Optional<Recipe<Container>>)ObjectUtils.firstNonNull(
         new Optional[]{
            mgr.getRecipeFor(RecipeType.SMELTING, inv, world),
            mgr.getRecipeFor(RecipeType.CAMPFIRE_COOKING, inv, world),
            mgr.getRecipeFor(RecipeType.SMOKING, inv, world),
            Optional.empty()
         }
      );
      return optRecipe.map(recipe -> {
         ItemStack smeltResult = recipe.assemble(inv).copy();
         float exp = 0.0F;
         if (recipe instanceof AbstractCookingRecipe) {
            exp = ((AbstractCookingRecipe)recipe).getExperience();
         }

         return new Tuple(smeltResult, exp);
      });
   }
}
