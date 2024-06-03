package iskallia.vault.item.crystal.recipe;

import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TreasureKeyAnvilRecipe extends VanillaAnvilRecipe {
   public static final Map<Item, Item> CLUSTER_TO_KEY = new HashMap<>();

   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() != ModItems.BLANK_KEY) {
         return false;
      } else {
         Item key = CLUSTER_TO_KEY.get(secondary.getItem());
         if (key == null) {
            return false;
         } else {
            int count = Math.min(primary.getCount(), secondary.getCount());
            context.setOutput(new ItemStack(key, count));
            context.onTake(context.getTake().append(() -> {
               context.getInput()[0].shrink(count);
               context.getInput()[1].shrink(count);
            }));
            return true;
         }
      }
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
      IVanillaRecipeFactory factory = registry.getVanillaRecipeFactory();
      List<ItemStack> primary = new ArrayList<>();
      List<ItemStack> secondary = new ArrayList<>();
      List<ItemStack> output = new ArrayList<>();
      primary.add(new ItemStack(ModItems.BLANK_KEY));
      CLUSTER_TO_KEY.forEach((cluster, key) -> {
         secondary.add(new ItemStack(cluster));
         output.add(new ItemStack(key));
      });
      registry.addRecipes(RecipeTypes.ANVIL, List.of(factory.createAnvilRecipe(primary, secondary, output)));
   }

   static {
      CLUSTER_TO_KEY.put(ModItems.ASHIUM_CLUSTER, ModItems.ASHIUM_KEY);
      CLUSTER_TO_KEY.put(ModItems.BOMIGNITE_CLUSTER, ModItems.BOMIGNITE_KEY);
      CLUSTER_TO_KEY.put(ModItems.GORGINITE_CLUSTER, ModItems.GORGINITE_KEY);
      CLUSTER_TO_KEY.put(ModItems.ISKALLIUM_CLUSTER, ModItems.ISKALLIUM_KEY);
      CLUSTER_TO_KEY.put(ModItems.PETZANITE_CLUSTER, ModItems.PETZANITE_KEY);
      CLUSTER_TO_KEY.put(ModItems.PUFFIUM_CLUSTER, ModItems.PUFFIUM_KEY);
      CLUSTER_TO_KEY.put(ModItems.SPARKLETINE_CLUSTER, ModItems.SPARKLETINE_KEY);
      CLUSTER_TO_KEY.put(ModItems.TUBIUM_CLUSTER, ModItems.TUBIUM_KEY);
      CLUSTER_TO_KEY.put(ModItems.UPALINE_CLUSTER, ModItems.UPALINE_KEY);
      CLUSTER_TO_KEY.put(ModItems.XENIUM_CLUSTER, ModItems.XENIUM_KEY);
   }
}
