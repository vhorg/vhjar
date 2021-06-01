package iskallia.vault.recipe;

import iskallia.vault.block.item.RelicStatueBlockItem;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.RelicPartItem;
import iskallia.vault.util.RelicSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class UnidentifiedGearRecipe extends SpecialRecipe {
   public UnidentifiedGearRecipe(ResourceLocation id) {
      super(id);
   }

   public boolean matches(CraftingInventory inv, World world) {
      RelicSet set = null;
      Set<RelicPartItem> parts = new HashSet<>();

      for (int i = 0; i < inv.func_70302_i_(); i++) {
         ItemStack stack = inv.func_70301_a(i);
         if (stack.func_77973_b() instanceof RelicPartItem) {
            if (set != null && ((RelicPartItem)stack.func_77973_b()).getRelicSet() != set) {
               return false;
            }

            set = ((RelicPartItem)stack.func_77973_b()).getRelicSet();
            parts.add((RelicPartItem)stack.func_77973_b());
         } else if (!stack.func_190926_b()) {
            return false;
         }
      }

      return set != null && parts.size() == set.getItemSet().size();
   }

   public ItemStack getCraftingResult(CraftingInventory inv) {
      for (int i = 0; i < inv.func_70302_i_(); i++) {
         ItemStack stack = inv.func_70301_a(i);
         if (stack.func_77973_b() instanceof RelicPartItem) {
            RelicSet set = ((RelicPartItem)stack.func_77973_b()).getRelicSet();
            return RelicStatueBlockItem.withRelicSet(set);
         }
      }

      return ItemStack.field_190927_a;
   }

   public boolean func_194133_a(int width, int height) {
      Optional<RelicSet> min = RelicSet.getAll().stream().min(Comparator.comparingInt(o -> o.getItemSet().size()));
      return min.isPresent() && width * height >= min.get().getItemSet().size();
   }

   public IRecipeSerializer<?> func_199559_b() {
      return ModRecipes.Serializer.CRAFTING_SPECIAL_RELIC_SET;
   }
}
