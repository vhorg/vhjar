package iskallia.vault.init;

import iskallia.vault.VaultMod;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;

public final class ModRelics {
   public static final Map<ResourceLocation, ModRelics.RelicRecipe> RECIPE_REGISTRY = new HashMap<>();
   private static final Map<String, ModRelics.RelicRecipe> RECIPES_BY_NAME = new HashMap<>();
   public static ModRelics.RelicRecipe EMPTY = new ModRelics.RelicRecipe("empty", VaultMod.id("empty"));
   public static ModRelics.RelicRecipe DRAGON = register(
      "dragon",
      ModDynamicModels.Relics.DRAGON_RELIC.getId(),
      ModDynamicModels.Relics.DRAGON_BREATH.getId(),
      ModDynamicModels.Relics.DRAGON_CHEST.getId(),
      ModDynamicModels.Relics.DRAGON_FOOT.getId(),
      ModDynamicModels.Relics.DRAGON_HEAD.getId(),
      ModDynamicModels.Relics.DRAGON_TAIL.getId()
   );
   public static ModRelics.RelicRecipe MINER = register(
      "miner",
      ModDynamicModels.Relics.MINER_RELIC.getId(),
      ModDynamicModels.Relics.MINERS_LIGHT.getId(),
      ModDynamicModels.Relics.MINERS_DELIGHT.getId(),
      ModDynamicModels.Relics.PICKAXE_HANDLE.getId(),
      ModDynamicModels.Relics.PICKAXE_HEAD.getId(),
      ModDynamicModels.Relics.PICKAXE_TOOL.getId()
   );
   public static ModRelics.RelicRecipe WARRIOR = register(
      "warrior",
      ModDynamicModels.Relics.WARRIOR_RELIC.getId(),
      ModDynamicModels.Relics.WARRIORS_ARMOUR.getId(),
      ModDynamicModels.Relics.WARRIORS_CHARM.getId(),
      ModDynamicModels.Relics.SWORD_BLADE.getId(),
      ModDynamicModels.Relics.SWORD_HANDLE.getId(),
      ModDynamicModels.Relics.SWORD_STICK.getId()
   );
   public static ModRelics.RelicRecipe RICHITY = register(
      "richity",
      ModDynamicModels.Relics.RICHITY_RELIC.getId(),
      ModDynamicModels.Relics.DIAMOND_ESSENCE.getId(),
      ModDynamicModels.Relics.GOLD_ESSENCE.getId(),
      ModDynamicModels.Relics.MYSTIC_GEM_ESSENCE.getId(),
      ModDynamicModels.Relics.NETHERITE_ESSENCE.getId(),
      ModDynamicModels.Relics.PLATINUM_ESSENCE.getId()
   );
   public static ModRelics.RelicRecipe TWITCH = register(
      "twitch",
      ModDynamicModels.Relics.TWITCH_RELIC.getId(),
      ModDynamicModels.Relics.TWITCH_EMOTE_1.getId(),
      ModDynamicModels.Relics.TWITCH_EMOTE_2.getId(),
      ModDynamicModels.Relics.TWITCH_EMOTE_3.getId(),
      ModDynamicModels.Relics.TWITCH_EMOTE_4.getId(),
      ModDynamicModels.Relics.TWITCH_EMOTE_5.getId()
   );
   public static ModRelics.RelicRecipe CUPCAKE = register(
      "cupcake",
      ModDynamicModels.Relics.CUPCAKE_RELIC.getId(),
      ModDynamicModels.Relics.CUPCAKE_BLUE.getId(),
      ModDynamicModels.Relics.CUPCAKE_LIME.getId(),
      ModDynamicModels.Relics.CUPCAKE_PINK.getId(),
      ModDynamicModels.Relics.CUPCAKE_PURPLE.getId(),
      ModDynamicModels.Relics.CUPCAKE_RED.getId()
   );
   public static ModRelics.RelicRecipe ELEMENTAL = register(
      "elemental",
      ModDynamicModels.Relics.ELEMENTAL_RELIC.getId(),
      ModDynamicModels.Relics.ELEMENT_AIR.getId(),
      ModDynamicModels.Relics.ELEMENT_EARTH.getId(),
      ModDynamicModels.Relics.ELEMENT_FIRE.getId(),
      ModDynamicModels.Relics.ELEMENT_WATER.getId(),
      ModDynamicModels.Relics.ELEMENT_SPIRIT.getId()
   );
   public static ModRelics.RelicRecipe NAZAR_BONCUGU = register(
      "nazar",
      ModDynamicModels.Relics.NAZAR_BONCUGU.getId(),
      ModDynamicModels.Relics.SER.getId(),
      ModDynamicModels.Relics.KEM_GOZ.getId(),
      ModDynamicModels.Relics.KADER.getId(),
      ModDynamicModels.Relics.KISMET.getId(),
      ModDynamicModels.Relics.NAZARLIK.getId()
   );

   public static Optional<ModRelics.RelicRecipe> getRelicOfFragment(ResourceLocation fragmentId) {
      for (ModRelics.RelicRecipe relicRecipe : RECIPE_REGISTRY.values()) {
         if (relicRecipe.getFragments().contains(fragmentId)) {
            return Optional.of(relicRecipe);
         }
      }

      return Optional.empty();
   }

   private static ModRelics.RelicRecipe register(String recipeName, ResourceLocation resultingRelic, ResourceLocation... fragments) {
      ModRelics.RelicRecipe relicRecipe = new ModRelics.RelicRecipe(recipeName, resultingRelic, fragments);
      RECIPE_REGISTRY.put(resultingRelic, relicRecipe);
      RECIPES_BY_NAME.put(relicRecipe.getRecipeName(), relicRecipe);
      return relicRecipe;
   }

   public static class RelicProperty extends Property<ModRelics.RelicRecipe> {
      protected RelicProperty(String name) {
         super(name, ModRelics.RelicRecipe.class);
      }

      @Nonnull
      public Collection<ModRelics.RelicRecipe> getPossibleValues() {
         List<ModRelics.RelicRecipe> relicRecipes = new LinkedList<>(ModRelics.RECIPE_REGISTRY.values());
         relicRecipes.add(ModRelics.EMPTY);
         return relicRecipes;
      }

      @Nonnull
      public String getName(@Nonnull ModRelics.RelicRecipe recipe) {
         return recipe.getRecipeName();
      }

      @Nonnull
      public Optional<ModRelics.RelicRecipe> getValue(@Nonnull String value) {
         ModRelics.RelicRecipe relicRecipe = ModRelics.RECIPES_BY_NAME.get(value);
         return Optional.ofNullable(relicRecipe);
      }

      public boolean equals(Object that) {
         if (this == that) {
            return true;
         } else {
            return that instanceof ModRelics.RelicProperty thatProperty && super.equals(that)
               ? this.getPossibleValues().equals(thatProperty.getPossibleValues())
               : false;
         }
      }

      public int generateHashCode() {
         return 31 * super.generateHashCode() + this.getPossibleValues().hashCode();
      }

      public static ModRelics.RelicProperty create(String name) {
         return new ModRelics.RelicProperty(name);
      }
   }

   public static class RelicRecipe implements Comparable<ModRelics.RelicRecipe> {
      private final String recipeName;
      private final ResourceLocation resultingRelic;
      private final Set<ResourceLocation> fragments;

      private RelicRecipe(String recipeName, ResourceLocation resultingRelic, ResourceLocation... fragments) {
         this.recipeName = recipeName;
         this.resultingRelic = resultingRelic;
         this.fragments = new HashSet<>(List.of(fragments));
      }

      public String getRecipeName() {
         return this.recipeName;
      }

      public ResourceLocation getResultingRelic() {
         return this.resultingRelic;
      }

      public Set<ResourceLocation> getFragments() {
         return this.fragments;
      }

      public int compareTo(@Nonnull ModRelics.RelicRecipe that) {
         return this == that ? 0 : this.resultingRelic.toString().compareTo(that.resultingRelic.toString());
      }

      @Override
      public String toString() {
         return this.recipeName;
      }
   }
}
