package iskallia.vault.util;

import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import org.apache.commons.lang3.ObjectUtils;

public class RecipeUtil {
   @Nonnull
   public static Optional<Tuple<ItemStack, Float>> findSmeltingResult(World world, BlockState input) {
      ItemStack stack = new ItemStack(input.func_177230_c());
      return stack.func_190926_b() ? Optional.empty() : findSmeltingResult(world, stack);
   }

   @Nonnull
   public static Optional<Tuple<ItemStack, Float>> findSmeltingResult(World world, ItemStack input) {
      RecipeManager mgr = world.func_199532_z();
      IInventory inv = new Inventory(new ItemStack[]{input});
      Optional<IRecipe<IInventory>> optRecipe = (Optional<IRecipe<IInventory>>)ObjectUtils.firstNonNull(
         new Optional[]{
            mgr.func_215371_a(IRecipeType.field_222150_b, inv, world),
            mgr.func_215371_a(IRecipeType.field_222153_e, inv, world),
            mgr.func_215371_a(IRecipeType.field_222152_d, inv, world),
            Optional.empty()
         }
      );
      return optRecipe.map(recipe -> {
         ItemStack smeltResult = recipe.func_77572_b(inv).func_77946_l();
         float exp = 0.0F;
         if (recipe instanceof AbstractCookingRecipe) {
            exp = ((AbstractCookingRecipe)recipe).func_222138_b();
         }

         return new Tuple(smeltResult, exp);
      });
   }
}
