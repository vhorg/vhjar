package iskallia.vault.item.crystal.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.registration.IRecipeRegistration;

public class AnvilRecipes {
   private static List<AnvilRecipe> REGISTRY = new ArrayList<>();

   public static void register() {
      register(new AugmentAnvilRecipe());
      register(new BanishedSoulAnvilRecipe());
      register(new CatalystAnvilRecipe());
      register(new ChaosCatalystAnvilRecipe());
      register(new CharmAnvilRecipe());
      register(new EyeOfAvariceAnvilRecipe());
      register(new GodTokenAnvilRecipe());
      register(new InscriptionAnvilRecipe());
      register(new JewelAnvilRecipe());
      register(new MoteAnvilRecipe());
      register(new PhoenixFeatherAnvilRecipe());
      register(new PlundererPearlAnvilRecipe());
      register(new RepairGearAnvilRecipe());
      register(new RerollArtifactAnvilRecipe());
      register(new SealAnvilRecipe());
      register(new SoulFlameAnvilRecipe());
      register(new TreasureKeyAnvilRecipe());
      register(new WardensPearlAnvilRecipe());
      register(new PeacefulAnvilRecipe());
      register(new DungeonCapstoneAnvilRecipe());
      register(new TreasureCapstoneAnvilRecipe());
      register(new PylonHunterCapstoneAnvilRecipe());
      register(new VendoorCapstoneAnvilRecipe());
   }

   public static void registerJEI(IRecipeRegistration registry) {
      for (AnvilRecipe recipe : REGISTRY) {
         recipe.onRegisterJEI(registry);
      }
   }

   public static Optional<AnvilRecipe> get(AnvilContext context) {
      for (AnvilRecipe recipe : REGISTRY) {
         if (recipe.onCraft(context)) {
            return Optional.of(recipe);
         }
      }

      return Optional.empty();
   }

   private static <T extends AnvilRecipe> T register(T recipe) {
      REGISTRY.add(recipe);
      return recipe;
   }
}
