package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.antique.Antique;
import iskallia.vault.antique.condition.AntiqueCondition;
import iskallia.vault.antique.condition.AntiqueConditionAnd;
import iskallia.vault.antique.condition.AntiqueConditionChance;
import iskallia.vault.antique.condition.AntiqueConditionEntityGroup;
import iskallia.vault.antique.condition.AntiqueConditionKey;
import iskallia.vault.antique.condition.AntiqueConditionLevel;
import iskallia.vault.antique.condition.AntiqueConditionNegate;
import iskallia.vault.antique.condition.AntiqueConditionOr;
import iskallia.vault.antique.condition.AntiqueConditionTag;
import iskallia.vault.antique.condition.AntiqueConditionType;
import iskallia.vault.antique.reward.AntiqueReward;
import iskallia.vault.antique.reward.AntiqueRewardCombined;
import iskallia.vault.antique.reward.AntiqueRewardItemList;
import iskallia.vault.antique.reward.AntiqueRewardSpecificGear;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModAntiques {
   static final List<Antique> ALL_ANTIQUES = new ArrayList<>();
   public static final Antique SHIP_IN_A_BOTTLE = register("ship_in_a_bottle");
   public static final Antique SNOW_FOX = register("snow_fox");
   public static final Antique THE_LOST_ONE = register("the_lost_one");
   public static final Antique SOUL_MIRROR = register("soul_mirror");
   public static final Antique ACQUIRED_TASTE = register("acquired_taste");
   public static final Antique NAZAR_BONCUGU = register("nazar_boncugu");
   public static final Antique CREST_ORANGE_BRIGADE = register("crest_orange_brigade");
   public static final Antique HAGS_COOKING_POT = register("hags_cooking_pot");
   public static final Antique GEM_KING = register("gem_king");
   public static final Antique THE_UNICORN = register("the_unicorn");
   public static final Antique THE_LIFE_KEY = register("the_life_key");
   public static final Antique ARCANE_SABER = register("arcane_saber");
   public static final Antique COMPRESSED_CUBE = register("compressed_cube");
   public static final Antique THE_BLUE_JAY = register("the_blue_jay");
   public static final Antique POCKET_PENGUIN = register("pocket_penguin");
   public static final Antique BOUNTIFUL_HARVEST = register("bountiful_harvest");
   public static final Antique PIRATES_LOCKPICK = register("pirates_lockpick");
   public static final Antique LUCKY_CAT_MEDALLION = register("lucky_cat_medallion");
   public static final Antique THE_SNOW_PAW = register("the_snow_paw");
   public static final Antique LUCKY_BAMBOO = register("lucky_bamboo");
   public static final Antique GLIMPSE_OF_THE_COSMOS = register("glimpse_of_the_cosmos");
   public static final Antique FOUNTAIN_OF_KNOWLEDGE = register("fountain_of_knowledge");
   public static final Antique INTERTWINED = register("intertwined");

   public static void registerAntiques(Register<Antique> event) {
      IForgeRegistry<Antique> registry = event.getRegistry();
      ALL_ANTIQUES.forEach(registry::register);
   }

   public static Antique register(String antiqueRegistryPath) {
      return register(new Antique(VaultMod.id(antiqueRegistryPath)));
   }

   public static <T extends Antique> T register(T antique) {
      ALL_ANTIQUES.add(antique);
      return antique;
   }

   public static class Conditions {
      public static final AntiqueCondition.Provider OR = simple("or", AntiqueConditionOr::new);
      public static final AntiqueCondition.Provider AND = simple("and", AntiqueConditionAnd::new);
      public static final AntiqueCondition.Provider NOT = simple("not", AntiqueConditionNegate::new);
      public static final AntiqueCondition.Provider LEVEL = simple("level", AntiqueConditionLevel::new);
      public static final AntiqueCondition.Provider TYPE = simple("type", AntiqueConditionType::new);
      public static final AntiqueCondition.Provider TAG = simple("tag", AntiqueConditionTag::new);
      public static final AntiqueCondition.Provider KEY = simple("key", AntiqueConditionKey::new);
      public static final AntiqueCondition.Provider CHANCE = simple("chance", AntiqueConditionChance::new);
      public static final AntiqueCondition.Provider ENTITY_GROUP = simple("entity_group", AntiqueConditionEntityGroup::new);

      public static void registerAntiqueConditions(Register<AntiqueCondition.Provider> event) {
         IForgeRegistry<AntiqueCondition.Provider> registry = event.getRegistry();
         registry.register(OR);
         registry.register(AND);
         registry.register(NOT);
         registry.register(LEVEL);
         registry.register(TYPE);
         registry.register(TAG);
         registry.register(KEY);
         registry.register(CHANCE);
         registry.register(ENTITY_GROUP);
      }

      private static AntiqueCondition.Provider simple(String name, Supplier<AntiqueCondition> newInst) {
         return AntiqueCondition.Provider.make(VaultMod.id(name), newInst);
      }
   }

   public static class Rewards {
      public static final AntiqueReward.Provider ITEM_LIST = simple("item_list", AntiqueRewardItemList::new);
      public static final AntiqueReward.Provider SPECIFIC_GEAR = simple("gear_item_list", AntiqueRewardSpecificGear::new);
      public static final AntiqueReward.Provider COMBINED = simple("combined", AntiqueRewardCombined::new);

      public static void registerAntiqueRewards(Register<AntiqueReward.Provider> event) {
         IForgeRegistry<AntiqueReward.Provider> registry = event.getRegistry();
         registry.register(ITEM_LIST);
         registry.register(SPECIFIC_GEAR);
         registry.register(COMBINED);
      }

      private static AntiqueReward.Provider simple(String name, Supplier<AntiqueReward> newInst) {
         return AntiqueReward.Provider.make(VaultMod.id(name), newInst);
      }
   }
}
