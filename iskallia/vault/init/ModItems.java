package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.item.AnimalJarItem;
import iskallia.vault.item.ArchetypeStarItem;
import iskallia.vault.item.AugmentItem;
import iskallia.vault.item.BasicFuelItem;
import iskallia.vault.item.BasicItem;
import iskallia.vault.item.BasicMobEggItem;
import iskallia.vault.item.BasicScavengerItem;
import iskallia.vault.item.CompassItem;
import iskallia.vault.item.ErrorItem;
import iskallia.vault.item.GatedLootableItem;
import iskallia.vault.item.GodBlessingItem;
import iskallia.vault.item.InfiniteWaterBucketItem;
import iskallia.vault.item.InscriptionItem;
import iskallia.vault.item.InscriptionPieceItem;
import iskallia.vault.item.ItemDrillArrow;
import iskallia.vault.item.ItemKnowledgeStar;
import iskallia.vault.item.ItemLegendaryTreasure;
import iskallia.vault.item.ItemRelicBoosterPack;
import iskallia.vault.item.ItemRespecFlask;
import iskallia.vault.item.ItemShardPouch;
import iskallia.vault.item.ItemSkillOrb;
import iskallia.vault.item.ItemUnidentifiedArtifact;
import iskallia.vault.item.ItemUnidentifiedVaultKey;
import iskallia.vault.item.ItemVaultCrystalSeal;
import iskallia.vault.item.ItemVaultFruit;
import iskallia.vault.item.ItemVaultKey;
import iskallia.vault.item.LootableItem;
import iskallia.vault.item.MagnetItem;
import iskallia.vault.item.NeuralizerItem;
import iskallia.vault.item.OldNotesItem;
import iskallia.vault.item.PaxelJewelItem;
import iskallia.vault.item.QuestBookItem;
import iskallia.vault.item.RegretOrbItem;
import iskallia.vault.item.RelicFragmentItem;
import iskallia.vault.item.RelicItem;
import iskallia.vault.item.RokkitLaunchaItem;
import iskallia.vault.item.VaultBasicFoodItem;
import iskallia.vault.item.VaultCatalystInfusedItem;
import iskallia.vault.item.VaultCatalystItem;
import iskallia.vault.item.VaultCharmUpgrade;
import iskallia.vault.item.VaultDollItem;
import iskallia.vault.item.VaultRuneItem;
import iskallia.vault.item.VaultXPFoodItem;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.item.consumable.AbsorptionAppleItem;
import iskallia.vault.item.crystal.CrystalShardItem;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.gear.EtchingItem;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.item.gear.VaultAxeItem;
import iskallia.vault.item.gear.VaultShieldItem;
import iskallia.vault.item.gear.VaultSwordItem;
import iskallia.vault.item.gear.WandItem;
import iskallia.vault.item.modification.GearModificationItem;
import iskallia.vault.item.modification.ReforgeTagModificationFocus;
import iskallia.vault.item.tool.GemstoneItem;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.item.tool.PaxelItem;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.util.function.Memo;
import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {
   public static CreativeModeTab VAULT_MOD_GROUP = createCreativeTab("the_vault", Memo.of(() -> new ItemStack(ModItems.SPICY_HEARTY_BURGER)));
   public static CreativeModeTab SCAVENGER_GROUP = createCreativeTab("the_vault.scavenger", Memo.of(() -> new ItemStack(ModBlocks.SCAVENGER_ALTAR)));
   public static CreativeModeTab RELIC_GROUP = createCreativeTab("the_vault.relic", Memo.of(() -> new ItemStack(ModBlocks.RELIC_PEDESTAL)));
   public static CreativeModeTab GEAR_GROUP = createCreativeTab("the_vault.gear", Memo.of(() -> new ItemStack(ModItems.SWORD)));
   public static BasicItem VAULT_ROCK = new BasicItem(VaultMod.id("vault_rock"));
   public static BasicItem ALEXANDRITE_GEM = new BasicItem(VaultMod.id("gem_alexandrite"));
   public static BasicItem BENITOITE_GEM = new BasicItem(VaultMod.id("gem_benitoite"));
   public static BasicItem LARIMAR_GEM = new BasicItem(VaultMod.id("gem_larimar"));
   public static BasicItem BLACK_OPAL_GEM = new BasicItem(VaultMod.id("gem_black_opal"));
   public static BasicItem PAINITE_GEM = new BasicItem(VaultMod.id("gem_painite"));
   public static BasicItem ISKALLIUM_GEM = new BasicItem(VaultMod.id("gem_iskallium"));
   public static BasicItem GORGINITE_GEM = new BasicItem(VaultMod.id("gem_gorginite"));
   public static BasicItem SPARKLETINE_GEM = new BasicItem(VaultMod.id("gem_sparkletine"));
   public static BasicItem WUTODIE_GEM = new BasicItem(VaultMod.id("gem_wutodie"));
   public static BasicItem ASHIUM_GEM = new BasicItem(VaultMod.id("gem_ashium"));
   public static BasicItem BOMIGNITE_GEM = new BasicItem(VaultMod.id("gem_bomignite"));
   public static BasicItem TUBIUM_GEM = new BasicItem(VaultMod.id("gem_tubium"));
   public static BasicItem UPALINE_GEM = new BasicItem(VaultMod.id("gem_upaline"));
   public static BasicItem PUFFIUM_GEM = new BasicItem(VaultMod.id("gem_puffium"));
   public static BasicItem PETZANITE_GEM = new BasicItem(VaultMod.id("gem_petzanite"));
   public static BasicItem XENIUM_GEM = new BasicItem(VaultMod.id("gem_xenium"));
   public static BasicItem ECHO_GEM = new BasicItem(VaultMod.id("gem_echo"));
   public static BasicItem PERFECT_ALEXANDRITE = new BasicItem(VaultMod.id("perfect_alexandrite"));
   public static BasicItem PERFECT_PAINITE = new BasicItem(VaultMod.id("perfect_painite"));
   public static BasicItem PERFECT_BENITOITE = new BasicItem(VaultMod.id("perfect_benitoite"));
   public static BasicItem PERFECT_LARIMAR = new BasicItem(VaultMod.id("perfect_larimar"));
   public static BasicItem PERFECT_BLACK_OPAL = new BasicItem(VaultMod.id("perfect_black_opal"));
   public static BasicItem PERFECT_ECHO_GEM = new BasicItem(VaultMod.id("perfect_echo_gem"));
   public static BasicItem PERFECT_WUTODIE = new BasicItem(VaultMod.id("perfect_wutodie"));
   public static BasicItem ISKALLIUM_CHUNK = new BasicItem(VaultMod.id("chunk_iskallium"));
   public static BasicItem GORGINITE_CHUNK = new BasicItem(VaultMod.id("chunk_gorginite"));
   public static BasicItem SPARKLETINE_CHUNK = new BasicItem(VaultMod.id("chunk_sparkletine"));
   public static BasicItem ASHIUM_CHUNK = new BasicItem(VaultMod.id("chunk_ashium"));
   public static BasicItem BOMIGNITE_CHUNK = new BasicItem(VaultMod.id("chunk_bomignite"));
   public static BasicItem TUBIUM_CHUNK = new BasicItem(VaultMod.id("chunk_tubium"));
   public static BasicItem UPALINE_CHUNK = new BasicItem(VaultMod.id("chunk_upaline"));
   public static BasicItem PUFFIUM_CHUNK = new BasicItem(VaultMod.id("chunk_puffium"));
   public static BasicItem PETZANITE_CHUNK = new BasicItem(VaultMod.id("chunk_petzanite"));
   public static BasicItem XENIUM_CHUNK = new BasicItem(VaultMod.id("chunk_xenium"));
   public static BasicItem NETHERITE_CLUSTER = new BasicItem(VaultMod.id("cluster_netherite"));
   public static BasicItem ISKALLIUM_CLUSTER = new BasicItem(VaultMod.id("cluster_iskallium"));
   public static BasicItem GORGINITE_CLUSTER = new BasicItem(VaultMod.id("cluster_gorginite"));
   public static BasicItem SPARKLETINE_CLUSTER = new BasicItem(VaultMod.id("cluster_sparkletine"));
   public static BasicItem ASHIUM_CLUSTER = new BasicItem(VaultMod.id("cluster_ashium"));
   public static BasicItem BOMIGNITE_CLUSTER = new BasicItem(VaultMod.id("cluster_bomignite"));
   public static BasicItem TUBIUM_CLUSTER = new BasicItem(VaultMod.id("cluster_tubium"));
   public static BasicItem UPALINE_CLUSTER = new BasicItem(VaultMod.id("cluster_upaline"));
   public static BasicItem PUFFIUM_CLUSTER = new BasicItem(VaultMod.id("cluster_puffium"));
   public static BasicItem PETZANITE_CLUSTER = new BasicItem(VaultMod.id("cluster_petzanite"));
   public static BasicItem XENIUM_CLUSTER = new BasicItem(VaultMod.id("cluster_xenium"));
   public static BasicItem EXTRAORDINARY_ALEXANDRITE = new BasicItem(VaultMod.id("extraordinary_alexandrite"));
   public static BasicItem EXTRAORDINARY_PAINITE = new BasicItem(VaultMod.id("extraordinary_painite"));
   public static BasicItem EXTRAORDINARY_BENITOITE = new BasicItem(VaultMod.id("extraordinary_benitoite"));
   public static BasicItem EXTRAORDINARY_LARIMAR = new BasicItem(VaultMod.id("extraordinary_larimar"));
   public static BasicItem EXTRAORDINARY_BLACK_OPAL = new BasicItem(VaultMod.id("extraordinary_black_opal"));
   public static BasicItem EXTRAORDINARY_ECHO_GEM = new BasicItem(VaultMod.id("extraordinary_echo_gem"));
   public static BasicItem EXTRAORDINARY_WUTODIE = new BasicItem(VaultMod.id("extraordinary_wutodie"));
   public static BasicItem SPARKING_GEMSTONE = new BasicItem(VaultMod.id("sparking_gemstone"));
   public static BasicItem ECHOING_GEMSTONE = new BasicItem(VaultMod.id("echoing_gemstone"));
   public static BasicItem GORGEOUS_GEMSTONE = new BasicItem(VaultMod.id("gorgeous_gemstone"));
   public static BasicItem ISKALLIC_GEMSTONE = new BasicItem(VaultMod.id("iskallic_gemstone"));
   public static BasicItem TUBIUM_GEMSTONE = new BasicItem(VaultMod.id("tubium_gemstone"));
   public static BasicItem UPALINE_GEMSTONE = new BasicItem(VaultMod.id("upaline_gemstone"));
   public static BasicItem BOMIGNITE_GEMSTONE = new BasicItem(VaultMod.id("bomignite_gemstone"));
   public static BasicItem BENITOITE_GEMSTONE = new BasicItem(VaultMod.id("benitoite_gemstone"));
   public static BasicItem XENIUM_GEMSTONE = new BasicItem(VaultMod.id("xenium_gemstone"));
   public static BasicItem WUTODIC_GEMSTONE = new BasicItem(VaultMod.id("wutodic_gemstone"));
   public static BasicItem ASHIUM_GEMSTONE = new BasicItem(VaultMod.id("ashium_gemstone"));
   public static BasicItem PETZANITE_GEMSTONE = new BasicItem(VaultMod.id("petzanite_gemstone"));
   public static BasicItem POGGING_GEMSTONE = new BasicItem(VaultMod.id("pogging_gemstone"));
   public static BasicItem POG = new BasicItem(VaultMod.id("gem_pog"));
   public static BasicItem ECHO_POG = new BasicItem(VaultMod.id("echo_pog"));
   public static BasicItem OMEGA_POG = new BasicItem(VaultMod.id("omega_pog"));
   public static BasicItem SILVER_SCRAP = new BasicItem(VaultMod.id("silver_scrap"));
   public static BasicItem BRONZE_SCRAP = new BasicItem(VaultMod.id("bronze_scrap"));
   public static BasicItem ARTIFACT_FRAGMENT = new BasicItem(VaultMod.id("artifact_fragment"), new Properties().tab(VAULT_MOD_GROUP).fireResistant());
   public static BasicItem VAULT_ESSENCE = new BasicItem(VaultMod.id("vault_essence"));
   public static BasicItem VAULT_DIAMOND = new BasicItem(VaultMod.id("vault_diamond"));
   public static BasicItem BURGER_PATTY = new BasicItem(VaultMod.id("burger_patty"));
   public static BasicItem BURGER_BUN = new BasicItem(VaultMod.id("burger_bun"));
   public static BasicItem BURGER_CHEESE = new BasicItem(VaultMod.id("burger_cheese"));
   public static BasicItem BURGER_CHILI = new BasicItem(VaultMod.id("burger_chili"));
   public static BasicItem BURGER_LETTUCE = new BasicItem(VaultMod.id("burger_lettuce"));
   public static BasicItem BURGER_PICKLES = new BasicItem(VaultMod.id("burger_pickles"));
   public static BasicItem BURGER_SAUCE = new BasicItem(VaultMod.id("burger_sauce"));
   public static BasicItem BURGER_TOMATO = new BasicItem(VaultMod.id("burger_tomato"));
   public static BasicItem ECHOING_INGOT = new BasicItem(VaultMod.id("echoing_ingot"));
   public static BasicItem GEMMED_INGOT = new BasicItem(VaultMod.id("gemmed_ingot"));
   public static BasicItem VAULTERITE_INGOT = new BasicItem(VaultMod.id("vaulterite_ingot"));
   public static BasicItem RED_VAULT_ESSENCE = new BasicItem(VaultMod.id("red_vault_essence"));
   public static BasicItem VAULT_DUST = new BasicItem(VaultMod.id("vault_dust"));
   public static BasicItem VAULT_NUGGET = new BasicItem(VaultMod.id("vault_nugget"));
   public static BasicItem VAULT_SCRAP = new BasicItem(VaultMod.id("vault_scrap"));
   public static BasicItem TRINKET_SCRAP = new BasicItem(VaultMod.id("trinket_scrap"));
   public static BasicItem VAULT_INGOT = new BasicItem(VaultMod.id("vault_ingot"));
   public static BasicItem RAW_CHROMATIC_IRON = new BasicItem(VaultMod.id("raw_chromatic_iron"));
   public static BasicItem CHROMATIC_IRON_TINY_DUST = new BasicItem(VaultMod.id("chromatic_iron_tiny_dust"));
   public static BasicItem CHROMATIC_IRON_SMALL_DUST = new BasicItem(VaultMod.id("chromatic_iron_small_dust"));
   public static BasicItem CHROMATIC_IRON_DUST = new BasicItem(VaultMod.id("chromatic_iron_dust"));
   public static BasicItem CHROMATIC_IRON_DIRTY_DUST = new BasicItem(VaultMod.id("chromatic_iron_dirty_dust"));
   public static BasicItem CHROMATIC_IRON_SHARD = new BasicItem(VaultMod.id("chromatic_iron_shard"));
   public static BasicItem CHROMATIC_IRON_CLUMP = new BasicItem(VaultMod.id("chromatic_iron_clump"));
   public static BasicItem CHROMATIC_IRON_CRYSTAL = new BasicItem(VaultMod.id("chromatic_iron_crystal"));
   public static BasicItem CHROMATIC_IRON_NUGGET = new BasicItem(VaultMod.id("chromatic_iron_nugget"));
   public static BasicItem CHROMATIC_IRON_INGOT = new BasicItem(VaultMod.id("chromatic_iron_ingot"));
   public static BasicItem CHROMATIC_STEEL_NUGGET = new BasicItem(VaultMod.id("chromatic_steel_nugget"));
   public static BasicItem CHROMATIC_STEEL_INGOT = new BasicItem(VaultMod.id("chromatic_steel_ingot"));
   public static BasicItem BLACK_CHROMATIC_STEEL_NUGGET = new BasicItem(VaultMod.id("black_chromatic_steel_nugget"));
   public static BasicItem BLACK_CHROMATIC_STEEL_INGOT = new BasicItem(VaultMod.id("black_chromatic_steel_ingot"));
   public static BasicItem MAGIC_SILK = new BasicItem(VaultMod.id("magic_silk"));
   public static BasicItem CHIPPED_VAULT_ROCK = new BasicItem(VaultMod.id("chipped_vault_rock"));
   public static BasicItem CARBON = new BasicItem(VaultMod.id("carbon"));
   public static BasicItem CARBON_NUGGET = new BasicItem(VaultMod.id("carbon_nugget"));
   public static BasicItem DIAMOND_NUGGET = new BasicItem(VaultMod.id("diamond_nugget"));
   public static BasicItem DRIFTWOOD = new BasicItem(VaultMod.id("driftwood"));
   public static BasicItem WOODEN_CHEST_SCROLL = new BasicItem(VaultMod.id("wooden_chest_scroll"));
   public static BasicItem ORNATE_CHEST_SCROLL = new BasicItem(VaultMod.id("ornate_chest_scroll"));
   public static BasicItem GILDED_CHEST_SCROLL = new BasicItem(VaultMod.id("gilded_chest_scroll"));
   public static BasicItem LIVING_CHEST_SCROLL = new BasicItem(VaultMod.id("living_chest_scroll"));
   public static BasicItem HARDENED_CHEST_SCROLL = new BasicItem(VaultMod.id("hardened_chest_scroll"));
   public static BasicItem FLESH_CHEST_SCROLL = new BasicItem(VaultMod.id("flesh_chest_scroll"));
   public static BasicItem ENIGMA_CHEST_SCROLL = new BasicItem(VaultMod.id("enigma_chest_scroll"));
   public static BasicItem ALTAR_CHEST_SCROLL = new BasicItem(VaultMod.id("altar_chest_scroll"));
   public static BasicItem TREASURE_CHEST_SCROLL = new BasicItem(VaultMod.id("treasure_chest_scroll"));
   public static BasicItem WUTODIC_MASS = new BasicItem(VaultMod.id("wutodic_mass"));
   public static BasicItem HARDENED_WUTODIC_MASS = new BasicItem(VaultMod.id("hardened_wutodic_mass"));
   public static BasicItem WUTODIC_SILVER_NUGGET = new BasicItem(VaultMod.id("wutodic_silver_nugget"));
   public static BasicItem WUTODIC_SILVER_INGOT = new BasicItem(VaultMod.id("wutodic_silver_ingot"));
   public static BasicItem SUBLIME_VAULT_ELIXIR = new BasicItem(VaultMod.id("sublime_vault_elixir"));
   public static BasicItem SUBLIME_VAULT_SUBSTANCE = new BasicItem(VaultMod.id("sublime_vault_substance"));
   public static BasicItem SUBLIME_VAULT_VISION = new BasicItem(VaultMod.id("sublime_vault_vision"));
   public static BasicItem KEYSTONE_IDONA = new BasicItem(VaultMod.id("final_keystone_idona"));
   public static BasicItem KEYSTONE_TENOS = new BasicItem(VaultMod.id("final_keystone_tenos"));
   public static BasicItem KEYSTONE_VELARA = new BasicItem(VaultMod.id("final_keystone_velara"));
   public static BasicItem KEYSTONE_WENDARR = new BasicItem(VaultMod.id("final_keystone_wendarr"));
   public static GodBlessingItem GOD_BLESSING = new GodBlessingItem(VaultMod.id("god_blessing"));
   public static BasicItem POISONOUS_MUSHROOM = new BasicItem(VaultMod.id("poisonous_mushroom"));
   public static BasicItem HUNTER_EYE = new BasicItem(VaultMod.id("hunter_eye"));
   public static BasicItem ETERNAL_SOUL = new BasicItem(VaultMod.id("eternal_soul"));
   public static BasicItem INFUSED_ETERNAL_SOUL = new BasicItem(VaultMod.id("infused_eternal_soul"));
   public static BasicItem KEY_PIECE = new BasicItem(VaultMod.id("key_piece"));
   public static BasicItem KEY_MOULD = new BasicItem(VaultMod.id("key_mould"));
   public static BasicItem BLANK_KEY = new BasicItem(VaultMod.id("blank_key"));
   public static ItemVaultKey ISKALLIUM_KEY = new ItemVaultKey(VAULT_MOD_GROUP, VaultMod.id("key_iskallium"));
   public static ItemVaultKey GORGINITE_KEY = new ItemVaultKey(VAULT_MOD_GROUP, VaultMod.id("key_gorginite"));
   public static ItemVaultKey SPARKLETINE_KEY = new ItemVaultKey(VAULT_MOD_GROUP, VaultMod.id("key_sparkletine"));
   public static ItemVaultKey ASHIUM_KEY = new ItemVaultKey(VAULT_MOD_GROUP, VaultMod.id("key_ashium"));
   public static ItemVaultKey BOMIGNITE_KEY = new ItemVaultKey(VAULT_MOD_GROUP, VaultMod.id("key_bomignite"));
   public static ItemVaultKey TUBIUM_KEY = new ItemVaultKey(VAULT_MOD_GROUP, VaultMod.id("key_tubium"));
   public static ItemVaultKey UPALINE_KEY = new ItemVaultKey(VAULT_MOD_GROUP, VaultMod.id("key_upaline"));
   public static ItemVaultKey PUFFIUM_KEY = new ItemVaultKey(VAULT_MOD_GROUP, VaultMod.id("key_puffium"));
   public static ItemVaultKey PETZANITE_KEY = new ItemVaultKey(VAULT_MOD_GROUP, VaultMod.id("key_petzanite"));
   public static ItemVaultKey XENIUM_KEY = new ItemVaultKey(VAULT_MOD_GROUP, VaultMod.id("key_xenium"));
   public static BasicItem MOTE_CLARITY = new BasicItem(VaultMod.id("mote_clarity"));
   public static BasicItem MOTE_PURITY = new BasicItem(VaultMod.id("mote_purity"));
   public static BasicItem MOTE_SANCTITY = new BasicItem(VaultMod.id("mote_sanctity"));
   public static VaultXPFoodItem PLAIN_BURGER = new VaultXPFoodItem.Flat(
      VaultMod.id("plain_burger"),
      () -> ModConfigs.VAULT_ITEMS.PLAIN_BURGER.minExp,
      () -> ModConfigs.VAULT_ITEMS.PLAIN_BURGER.maxExp,
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static VaultXPFoodItem CHEESE_BURGER = new VaultXPFoodItem.Flat(
      VaultMod.id("cheese_burger"),
      () -> ModConfigs.VAULT_ITEMS.CHEESE_BURGER.minExp,
      () -> ModConfigs.VAULT_ITEMS.CHEESE_BURGER.maxExp,
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static VaultXPFoodItem DOUBLE_CHEESE_BURGER = new VaultXPFoodItem.Flat(
      VaultMod.id("double_cheese_burger"),
      () -> ModConfigs.VAULT_ITEMS.DOUBLE_CHEESE_BURGER.minExp,
      () -> ModConfigs.VAULT_ITEMS.DOUBLE_CHEESE_BURGER.maxExp,
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static VaultXPFoodItem DELUXE_CHEESE_BURGER = new VaultXPFoodItem.Flat(
      VaultMod.id("deluxe_cheese_burger"),
      () -> ModConfigs.VAULT_ITEMS.DELUXE_CHEESE_BURGER.minExp,
      () -> ModConfigs.VAULT_ITEMS.DELUXE_CHEESE_BURGER.maxExp,
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static VaultXPFoodItem CRISPY_DELUXE_CHEESE_BURGER = new VaultXPFoodItem.Flat(
      VaultMod.id("crispy_deluxe_cheese_burger"),
      () -> ModConfigs.VAULT_ITEMS.CRISPY_DELUXE_CHEESE_BURGER.minExp,
      () -> ModConfigs.VAULT_ITEMS.CRISPY_DELUXE_CHEESE_BURGER.maxExp,
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static VaultXPFoodItem SALTY_DELUXE_CHEESE_BURGER = new VaultXPFoodItem.Flat(
      VaultMod.id("salty_deluxe_cheese_burger"),
      () -> ModConfigs.VAULT_ITEMS.SALTY_DELUXE_CHEESE_BURGER.minExp,
      () -> ModConfigs.VAULT_ITEMS.SALTY_DELUXE_CHEESE_BURGER.maxExp,
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static VaultXPFoodItem CHEESE_BURGER_FEAST = new VaultXPFoodItem.Flat(
      VaultMod.id("cheese_burger_feast"),
      () -> ModConfigs.VAULT_ITEMS.CHEESE_BURGER_FEAST.minExp,
      () -> ModConfigs.VAULT_ITEMS.CHEESE_BURGER_FEAST.maxExp,
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static VaultXPFoodItem SPICY_HEARTY_BURGER = new VaultXPFoodItem.Flat(
      VaultMod.id("spicy_hearty_burger"),
      () -> ModConfigs.VAULT_ITEMS.SPICY_HEARTY_BURGER.minExp,
      () -> ModConfigs.VAULT_ITEMS.SPICY_HEARTY_BURGER.maxExp,
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static VaultXPFoodItem VAULT_COOKIE = new VaultXPFoodItem.Flat(
      VaultMod.id("vault_cookie"),
      () -> ModConfigs.VAULT_ITEMS.VAULT_COOKIE.minExp,
      () -> ModConfigs.VAULT_ITEMS.VAULT_COOKIE.maxExp,
      new Properties().tab(VAULT_MOD_GROUP),
      25
   );
   public static ItemVaultFruit.BitterLemon BITTER_LEMON = new ItemVaultFruit.BitterLemon(VAULT_MOD_GROUP, VaultMod.id("bitter_lemon"), 600);
   public static ItemVaultFruit.SourOrange SOUR_ORANGE = new ItemVaultFruit.SourOrange(VAULT_MOD_GROUP, VaultMod.id("sour_orange"), 1200);
   public static ItemVaultFruit.MysticPear MYSTIC_PEAR = new ItemVaultFruit.MysticPear(VAULT_MOD_GROUP, VaultMod.id("mystic_pear"), 6000);
   public static ItemVaultFruit.SweetKiwi SWEET_KIWI = new ItemVaultFruit.SweetKiwi(VAULT_MOD_GROUP, VaultMod.id("sweet_kiwi"), 200);
   public static BasicItem VAULT_APPLE = new BasicItem(VaultMod.id("vault_apple"));
   public static AbsorptionAppleItem HEARTY_APPLE = new AbsorptionAppleItem(VaultMod.id("hearty_apple"));
   public static VaultBasicFoodItem VAULT_MEAT = new VaultBasicFoodItem(
      VaultMod.id("vault_meat"), new Builder().meat().nutrition(2).saturationMod(0.1F).build()
   );
   public static VaultBasicFoodItem RAW_VAULT_STEAK = new VaultBasicFoodItem(
      VaultMod.id("raw_vault_steak"), new Builder().meat().nutrition(3).saturationMod(0.3F).build()
   );
   public static VaultBasicFoodItem COOKED_VAULT_STEAK = new VaultBasicFoodItem(
      VaultMod.id("cooked_vault_steak"), new Builder().meat().fast().nutrition(8).saturationMod(1.4F).build()
   );
   public static RelicItem RELIC = new RelicItem(RELIC_GROUP, VaultMod.id("vault_relic"));
   public static RelicFragmentItem RELIC_FRAGMENT = new RelicFragmentItem(RELIC_GROUP, VaultMod.id("vault_relic_fragment"));
   public static ItemRelicBoosterPack RELIC_BOOSTER_PACK = new ItemRelicBoosterPack(VAULT_MOD_GROUP, VaultMod.id("relic_booster_pack"));
   public static ItemLegendaryTreasure LEGENDARY_TREASURE_NORMAL = new ItemLegendaryTreasure(
      VAULT_MOD_GROUP, VaultMod.id("legendary_treasure_normal"), VaultRarity.COMMON
   );
   public static ItemLegendaryTreasure LEGENDARY_TREASURE_RARE = new ItemLegendaryTreasure(
      VAULT_MOD_GROUP, VaultMod.id("legendary_treasure_rare"), VaultRarity.RARE
   );
   public static ItemLegendaryTreasure LEGENDARY_TREASURE_EPIC = new ItemLegendaryTreasure(
      VAULT_MOD_GROUP, VaultMod.id("legendary_treasure_epic"), VaultRarity.EPIC
   );
   public static ItemLegendaryTreasure LEGENDARY_TREASURE_OMEGA = new ItemLegendaryTreasure(
      VAULT_MOD_GROUP, VaultMod.id("legendary_treasure_omega"), VaultRarity.OMEGA
   );
   public static LootableItem MYSTERY_BOX = new LootableItem(
      VaultMod.id("mystery_box"), new Properties().tab(VAULT_MOD_GROUP), () -> ModConfigs.MYSTERY_BOX.POOL.getRandom(new Random()).generateItemStack()
   );
   public static LootableItem MYSTERY_EGG = new LootableItem(
      VaultMod.id("mystery_egg"), new Properties().tab(VAULT_MOD_GROUP), () -> ModConfigs.MYSTERY_EGG.POOL.getRandom(new Random()).generateItemStack()
   );
   public static LootableItem MYSTERY_HOSTILE_EGG = new LootableItem(
      VaultMod.id("mystery_hostile_egg"),
      new Properties().tab(VAULT_MOD_GROUP),
      () -> ModConfigs.MYSTERY_HOSTILE_EGG.POOL.getRandom(new Random()).generateItemStack()
   );
   public static LootableItem PANDORAS_BOX = new LootableItem(
      VaultMod.id("pandoras_box"), new Properties().tab(VAULT_MOD_GROUP), () -> ModConfigs.PANDORAS_BOX.POOL.getRandom(new Random()).generateItemStack()
   );
   public static LootableItem UNIDENTIFIED_RELIC_FRAGMENT = new LootableItem(
      VaultMod.id("unidentified_relic_fragment"),
      new Properties().tab(VAULT_MOD_GROUP),
      () -> ModConfigs.UNIDENTIFIED_RELIC_FRAGMENTS.getRandomFragment(new Random())
   );
   public static GatedLootableItem MOD_BOX = new GatedLootableItem(VaultMod.id("mod_box"), new Properties().tab(VAULT_MOD_GROUP));
   public static ItemUnidentifiedVaultKey UNIDENTIFIED_TREASURE_KEY = new ItemUnidentifiedVaultKey(
      VaultMod.id("unidentified_treasure_key"), new Properties().tab(VAULT_MOD_GROUP), () -> ModConfigs.UNIDENTIFIED_TREASURE_KEY.getRandomKey(new Random())
   );
   public static ItemUnidentifiedArtifact UNIDENTIFIED_ARTIFACT = new ItemUnidentifiedArtifact(VAULT_MOD_GROUP, VaultMod.id("unidentified_artifact"));
   public static ItemSkillOrb SKILL_ORB = new ItemSkillOrb(VaultMod.id("skill_orb"));
   public static BasicItem SKILL_ORB_FRAME = new BasicItem(VaultMod.id("orb_frame"));
   public static BasicItem SKILL_SHARD = new BasicItem(VaultMod.id("skill_shard"));
   public static BasicItem SKILL_ESSENCE = new BasicItem(VaultMod.id("skill_essence"));
   public static ItemKnowledgeStar KNOWLEDGE_STAR = new ItemKnowledgeStar(VaultMod.id("knowledge_star"));
   public static BasicItem KNOWLEDGE_STAR_SHARD = new BasicItem(VaultMod.id("knowledge_star_shard"));
   public static BasicItem KNOWLEDGE_STAR_CORE = new BasicItem(VaultMod.id("knowledge_star_core"));
   public static BasicItem KNOWLEDGE_STAR_ESSENCE = new BasicItem(VaultMod.id("knowledge_star_essence"));
   public static BasicItem EMPTY_FLASK = new BasicItem(VaultMod.id("empty_flask"));
   public static ItemRespecFlask RESPEC_FLASK = new ItemRespecFlask(VAULT_MOD_GROUP, VaultMod.id("respec_flask"));
   public static BasicItem REGRET_NUGGET = new BasicItem(VaultMod.id("regret_nugget"));
   public static BasicItem REGRET_CHUNK = new BasicItem(VaultMod.id("regret_chunk"));
   public static RegretOrbItem REGRET_ORB = new RegretOrbItem(VaultMod.id("regret_orb"));
   public static NeuralizerItem NEURALIZER = new NeuralizerItem(VaultMod.id("neuralizer"));
   public static ArchetypeStarItem ARCHETYPE_STAR = new ArchetypeStarItem(VaultMod.id("archetype_star"));
   public static BasicItem ARCHETYPE_STAR_SHARD = new BasicItem(VaultMod.id("archetype_star_shard"));
   public static BasicItem ARCHETYPE_STAR_CORE = new BasicItem(VaultMod.id("archetype_star_core"));
   public static BasicItem ARCHETYPE_STAR_ESSENCE = new BasicItem(VaultMod.id("archetype_star_essence"));
   public static VaultSwordItem SWORD = new VaultSwordItem(VaultMod.id("sword"), new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static VaultAxeItem AXE = new VaultAxeItem(VaultMod.id("axe"), new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static VaultArmorItem HELMET = new VaultArmorItem(VaultMod.id("helmet"), EquipmentSlot.HEAD, new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static VaultArmorItem CHESTPLATE = new VaultArmorItem(VaultMod.id("chestplate"), EquipmentSlot.CHEST, new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static VaultArmorItem LEGGINGS = new VaultArmorItem(VaultMod.id("leggings"), EquipmentSlot.LEGS, new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static VaultArmorItem BOOTS = new VaultArmorItem(VaultMod.id("boots"), EquipmentSlot.FEET, new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static VaultShieldItem SHIELD = new VaultShieldItem(VaultMod.id("shield"), new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static EtchingItem ETCHING = new EtchingItem(VaultMod.id("etching"), new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static TrinketItem TRINKET = new TrinketItem(VaultMod.id("trinket"));
   public static IdolItem IDOL_BENEVOLENT = new IdolItem(VaultMod.id("idol_benevolent"), VaultGod.VELARA, new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static IdolItem IDOL_OMNISCIENT = new IdolItem(VaultMod.id("idol_omniscient"), VaultGod.TENOS, new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static IdolItem IDOL_TIMEKEEPER = new IdolItem(VaultMod.id("idol_timekeeper"), VaultGod.WENDARR, new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static IdolItem IDOL_MALEVOLENCE = new IdolItem(VaultMod.id("idol_malevolence"), VaultGod.IDONA, new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static ToolItem TOOL = new ToolItem(VaultMod.id("tool"), new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static JewelItem JEWEL = new JewelItem(VaultMod.id("jewel"), new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static GemstoneItem GEMSTONE = new GemstoneItem(VaultMod.id("gemstone"), new Properties().tab(GEAR_GROUP));
   public static BottleItem BOTTLE = new BottleItem(VaultMod.id("bottle"), new Properties().stacksTo(1).tab(GEAR_GROUP));
   public static WandItem WAND = new WandItem(VaultMod.id("wand"), new Properties().stacksTo(1).tab(GEAR_GROUP));
   public static BasicItem VAULT_PLATING = new BasicItem(VaultMod.id("vault_plating"));
   public static BasicItem REPAIR_CORE = new BasicItem(VaultMod.id("repair_core"));
   public static BasicItem VAULT_ALLOY = new BasicItem(VaultMod.id("vault_alloy"));
   public static BasicItem ETCHING_FRAGMENT = new BasicItem(VaultMod.id("etching_fragment"));
   public static BasicItem WILD_FOCUS = new GearModificationItem(VaultMod.id("wild_focus"), ModGearModifications.REFORGE_ALL_MODIFIERS);
   public static BasicItem AMPLIFYING_FOCUS = new GearModificationItem(VaultMod.id("amplifying_focus"), ModGearModifications.ADD_MODIFIER);
   public static BasicItem NULLIFYING_FOCUS = new GearModificationItem(VaultMod.id("nullifying_focus"), ModGearModifications.REMOVE_MODIFIER);
   public static BasicItem OPPORTUNISTIC_FOCUS = new GearModificationItem(VaultMod.id("opportunistic_focus"), ModGearModifications.RESET_POTENTIAL);
   public static BasicItem RESILIENT_FOCUS = new GearModificationItem(VaultMod.id("resilient_focus"), ModGearModifications.REFORGE_REPAIR_SLOTS);
   public static BasicItem FUNDAMENTAL_FOCUS = new GearModificationItem(VaultMod.id("fundamental_focus"), ModGearModifications.REFORGE_ALL_IMPLICITS);
   public static BasicItem FACETED_FOCUS = new ReforgeTagModificationFocus(VaultMod.id("faceted_focus"), ModGearModifications.REFORGE_ALL_ADD_TAG);
   public static BasicItem CHAOTIC_FOCUS = new GearModificationItem(VaultMod.id("chaotic_focus"), ModGearModifications.REFORGE_RANDOM_TIER);
   public static BasicItem WAXING_FOCUS = new GearModificationItem(VaultMod.id("waxing_focus"), ModGearModifications.REFORGE_PREFIXES);
   public static BasicItem WANING_FOCUS = new GearModificationItem(VaultMod.id("waning_focus"), ModGearModifications.REFORGE_SUFFIXES);
   public static VaultCrystalItem VAULT_CRYSTAL = new VaultCrystalItem(VAULT_MOD_GROUP, VaultMod.id("vault_crystal"));
   public static BasicItem SOUL_FLAME = new BasicItem(VaultMod.id("soul_flame"));
   public static VaultCatalystItem VAULT_CATALYST = new VaultCatalystItem(VAULT_MOD_GROUP, VaultMod.id("vault_catalyst"));
   public static VaultCatalystInfusedItem VAULT_CATALYST_INFUSED = new VaultCatalystInfusedItem(VAULT_MOD_GROUP, VaultMod.id("vault_catalyst_infused"));
   public static BasicItem VAULT_CATALYST_CHAOS = new BasicItem(VaultMod.id("vault_catalyst_chaos"));
   public static BasicItem CRYSTAL_SEAL_EMPTY = new BasicItem(VaultMod.id("crystal_seal_empty"));
   public static ItemVaultCrystalSeal CRYSTAL_SEAL_EXECUTIONER = new ItemVaultCrystalSeal(VaultMod.id("crystal_seal_executioner"));
   public static ItemVaultCrystalSeal CRYSTAL_SEAL_HUNTER = new ItemVaultCrystalSeal(VaultMod.id("crystal_seal_hunter"));
   public static ItemVaultCrystalSeal CRYSTAL_SEAL_ARCHITECT = new ItemVaultCrystalSeal(VaultMod.id("crystal_seal_architect"));
   public static ItemVaultCrystalSeal CRYSTAL_SEAL_ANCIENTS = new ItemVaultCrystalSeal(VaultMod.id("crystal_seal_ancients"));
   public static ItemVaultCrystalSeal CRYSTAL_SEAL_RAID = new ItemVaultCrystalSeal(VaultMod.id("crystal_seal_raid"));
   public static ItemVaultCrystalSeal CRYSTAL_SEAL_CAKE = new ItemVaultCrystalSeal(VaultMod.id("crystal_seal_cake"));
   public static ItemVaultCrystalSeal CRYSTAL_SEAL_SAGE = new ItemVaultCrystalSeal(VaultMod.id("crystal_seal_sage"));
   public static ItemVaultCrystalSeal CRYSTAL_SEAL_SPEEDRUN = new ItemVaultCrystalSeal(VaultMod.id("crystal_seal_speedrun"));
   public static VaultRuneItem RUNE = new VaultRuneItem(VAULT_MOD_GROUP, VaultMod.id("rune"));
   public static InscriptionItem INSCRIPTION = new InscriptionItem(VAULT_MOD_GROUP, VaultMod.id("inscription"));
   public static InscriptionPieceItem INSCRIPTION_PIECE = new InscriptionPieceItem(VAULT_MOD_GROUP, VaultMod.id("inscription_piece"));
   public static AugmentItem AUGMENT = new AugmentItem(VAULT_MOD_GROUP, VaultMod.id("augment"));
   public static BasicItem VAULT_CATALYST_FRAGMENT = new BasicItem(VaultMod.id("vault_catalyst_fragment"));
   public static BasicItem PHOENIX_DUST = new BasicItem(VaultMod.id("phoenix_dust"));
   public static BasicItem PHOENIX_FEATHER = new BasicItem(VaultMod.id("phoenix_feather"));
   public static BasicItem DREAMSTONE = new BasicItem(VaultMod.id("dreamstone"));
   public static BasicItem EYE_OF_AVARICE = new BasicItem(VaultMod.id("eye_of_avarice"));
   public static BasicItem MYSTICAL_POWDER = new BasicItem(VaultMod.id("mystical_powder"));
   public static BasicItem ABYSSAL_ICHOR = new BasicItem(VaultMod.id("abyssal_ichor"));
   public static MagnetItem MAGNET = new MagnetItem(VaultMod.id("magnet"), new Properties().tab(GEAR_GROUP).stacksTo(1));
   public static BasicItem MAGNETITE = new BasicItem(VaultMod.id("magnetite"));
   public static BasicItem MAGNETITE_INGOT = new BasicItem(VaultMod.id("magnetite_ingot"));
   public static BasicItem UNKNOWN_EGG = new BasicItem(VaultMod.id("unknown_egg"));
   public static InfiniteWaterBucketItem INFINITE_WATER_BUCKET = new InfiniteWaterBucketItem(VaultMod.id("infinite_water_bucket"));
   public static BasicItem ACCELERATION_CHIP = new BasicItem(VaultMod.id("acceleration_chip"));
   public static BasicItem IDENTIFICATION_TOME = new BasicItem(VaultMod.id("identification_tome"), new Properties().tab(VAULT_MOD_GROUP).stacksTo(1));
   public static VaultDollItem VAULT_DOLL = new VaultDollItem(VaultMod.id("vault_doll"), new Properties().tab(VAULT_MOD_GROUP).stacksTo(1));
   public static BasicItem VAULT_COMPASS = new CompassItem();
   public static BucketItem VOID_LIQUID_BUCKET = (BucketItem)new BucketItem(
         ModFluids.VOID_LIQUID, new Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(VAULT_MOD_GROUP)
      )
      .setRegistryName(VaultMod.id("void_liquid_bucket"));
   public static CrystalShardItem CRYSTAL_SHARD_BENEVOLENT = new CrystalShardItem(
      VaultMod.id("shard_benevolent"), VAULT_MOD_GROUP, new TranslatableComponent("tooltip.the_vault.shard_benevolent")
   );
   public static CrystalShardItem CRYSTAL_SHARD_OMNISCIENT = new CrystalShardItem(
      VaultMod.id("shard_omniscient"), VAULT_MOD_GROUP, new TranslatableComponent("tooltip.the_vault.shard_omniscient")
   );
   public static CrystalShardItem CRYSTAL_SHARD_TIMEKEEPER = new CrystalShardItem(
      VaultMod.id("shard_timekeeper"), VAULT_MOD_GROUP, new TranslatableComponent("tooltip.the_vault.shard_timekeeper")
   );
   public static CrystalShardItem CRYSTAL_SHARD_MALEVOLENCE = new CrystalShardItem(
      VaultMod.id("shard_malevolence"), VAULT_MOD_GROUP, new TranslatableComponent("tooltip.the_vault.shard_malevolence")
   );
   public static BasicItem LOST_BOUNTY = new BasicItem(VaultMod.id("lost_bounty"), new Properties().tab(VAULT_MOD_GROUP).stacksTo(1).rarity(Rarity.UNCOMMON));
   public static BasicItem BOUNTY_PEARL = new BasicItem(VaultMod.id("bounty_pearl"), new Properties().tab(VAULT_MOD_GROUP));
   public static OldNotesItem OLD_NOTES = new OldNotesItem(VaultMod.id("old_notes"), new Properties().tab(VAULT_MOD_GROUP).stacksTo(1).rarity(Rarity.UNCOMMON));
   public static QuestBookItem QUEST_BOOK = new QuestBookItem(VaultMod.id("quest_book"));
   public static ErrorItem ERROR_ITEM = new ErrorItem(VaultMod.id("error_item"));
   public static BasicItem TOPAZ_SHARD = new BasicItem(VaultMod.id("topaz_shard"), new Properties().tab(VAULT_MOD_GROUP));
   public static BasicItem GILDED_INGOT = new BasicItem(VaultMod.id("gilded_ingot"), new Properties().tab(VAULT_MOD_GROUP));
   public static BasicItem ORNATE_INGOT = new BasicItem(VaultMod.id("ornate_ingot"), new Properties().tab(VAULT_MOD_GROUP));
   public static BasicItem ANCIENT_COPPER_INGOT = new BasicItem(VaultMod.id("ancient_copper_ingot"), new Properties().tab(VAULT_MOD_GROUP));
   public static BasicItem VELVET = new BasicItem(VaultMod.id("velvet"), new Properties().tab(VAULT_MOD_GROUP));
   public static BasicItem ROTTEN_MEAT = new BasicItem(VaultMod.id("rotten_meat"), new Properties().tab(VAULT_MOD_GROUP));
   public static BasicFuelItem WOODEN_CHUNK = new BasicFuelItem(VaultMod.id("wooden_chunk"), new Properties().tab(VAULT_MOD_GROUP), 400);
   public static BasicFuelItem OVERGROWN_WOODEN_CHUNK = new BasicFuelItem(VaultMod.id("overgrown_wooden_chunk"), new Properties().tab(VAULT_MOD_GROUP), 400);
   public static BasicItem SANDY_ROCKS = new BasicItem(VaultMod.id("sandy_rocks"), new Properties().tab(VAULT_MOD_GROUP));
   public static BasicItem LIVING_ROCK = new BasicItem(VaultMod.id("living_rock"), new Properties().tab(VAULT_MOD_GROUP));
   public static BasicItem MOSSY_BONE = new BasicItem(VaultMod.id("mossy_bone"), new Properties().tab(VAULT_MOD_GROUP));
   public static VaultBasicFoodItem VAULT_SWEETS = new VaultBasicFoodItem(
      VaultMod.id("vault_sweets"), new Builder().fast().nutrition(3).saturationMod(0.5F).build()
   );
   public static AnimalJarItem ANIMAL_JAR = new AnimalJarItem(VaultMod.id("animal_jar"), new Properties().tab(VAULT_MOD_GROUP).stacksTo(1));
   public static BasicItem DRILL_ARROW_PART = new BasicItem(VaultMod.id("drill_arrow_part"), new Properties().tab(VAULT_MOD_GROUP).stacksTo(8));
   public static ItemDrillArrow DRILL_ARROW = new ItemDrillArrow(VAULT_MOD_GROUP, VaultMod.id("drill_arrow"));
   public static RokkitLaunchaItem ROKKIT_LAUNCHA = new RokkitLaunchaItem(
      VaultMod.id("rokkit_launcha"), new Properties().tab(VAULT_MOD_GROUP).stacksTo(1).durability(465)
   );
   public static BasicItem SOUL_SHARD = new BasicItem(VaultMod.id("soul_shard"));
   public static BasicItem SOUL_DUST = new BasicItem(VaultMod.id("soul_dust"));
   public static ItemShardPouch SHARD_POUCH = new ItemShardPouch(VaultMod.id("shard_pouch"));
   public static BasicItem UNKNOWN_ITEM = new BasicItem(VaultMod.id("unknown_item"), new Properties().tab(VAULT_MOD_GROUP).stacksTo(1));
   public static BasicItem BANISHED_SOUL = new BasicItem(VaultMod.id("banished_soul"));
   public static BasicScavengerItem SCAVENGER_CREEPER_EYE = new BasicScavengerItem("creeper_eye");
   public static BasicScavengerItem SCAVENGER_CREEPER_FOOT = new BasicScavengerItem("creeper_foot");
   public static BasicScavengerItem SCAVENGER_CREEPER_FUSE = new BasicScavengerItem("creeper_fuse");
   public static BasicScavengerItem SCAVENGER_CREEPER_TNT = new BasicScavengerItem("creeper_tnt");
   public static BasicScavengerItem SCAVENGER_CREEPER_VIAL = new BasicScavengerItem("creeper_vial");
   public static BasicScavengerItem SCAVENGER_CREEPER_CHARM = new BasicScavengerItem("creeper_soul_charm");
   public static BasicScavengerItem SCAVENGER_DROWNED_BARNACLE = new BasicScavengerItem("drowned_barnacle");
   public static BasicScavengerItem SCAVENGER_DROWNED_EYE = new BasicScavengerItem("drowned_eye");
   public static BasicScavengerItem SCAVENGER_DROWNED_HIDE = new BasicScavengerItem("drowned_hide");
   public static BasicScavengerItem SCAVENGER_DROWNED_VIAL = new BasicScavengerItem("drowned_vial");
   public static BasicScavengerItem SCAVENGER_DROWNED_CHARM = new BasicScavengerItem("drowned_soul_charm");
   public static BasicScavengerItem SCAVENGER_SKELETON_SHARD = new BasicScavengerItem("skeleton_bone_shard");
   public static BasicScavengerItem SCAVENGER_SKELETON_EYE = new BasicScavengerItem("skeleton_milky_eye");
   public static BasicScavengerItem SCAVENGER_SKELETON_RIBCAGE = new BasicScavengerItem("skeleton_ribcage");
   public static BasicScavengerItem SCAVENGER_SKELETON_SKULL = new BasicScavengerItem("skeleton_skull");
   public static BasicScavengerItem SCAVENGER_SKELETON_WISHBONE = new BasicScavengerItem("skeleton_wishbone");
   public static BasicScavengerItem SCAVENGER_SKELETON_VIAL = new BasicScavengerItem("skeleton_milky_vial");
   public static BasicScavengerItem SCAVENGER_SKELETON_CHARM = new BasicScavengerItem("skeleton_soul_charm");
   public static BasicScavengerItem SCAVENGER_SPIDER_FANGS = new BasicScavengerItem("spider_fangs");
   public static BasicScavengerItem SCAVENGER_SPIDER_LEG = new BasicScavengerItem("spider_leg");
   public static BasicScavengerItem SCAVENGER_SPIDER_WEBBING = new BasicScavengerItem("spider_webbing_spool");
   public static BasicScavengerItem SCAVENGER_SPIDER_CURSED_CHARM = new BasicScavengerItem("spider_cursed_charm");
   public static BasicScavengerItem SCAVENGER_SPIDER_VIAL = new BasicScavengerItem("spider_vial");
   public static BasicScavengerItem SCAVENGER_SPIDER_CHARM = new BasicScavengerItem("spider_soul_charm");
   public static BasicScavengerItem SCAVENGER_ZOMBIE_BRAIN = new BasicScavengerItem("zombie_brain");
   public static BasicScavengerItem SCAVENGER_ZOMBIE_ARM = new BasicScavengerItem("zombie_arm");
   public static BasicScavengerItem SCAVENGER_ZOMBIE_EAR = new BasicScavengerItem("zombie_ear");
   public static BasicScavengerItem SCAVENGER_ZOMBIE_EYE = new BasicScavengerItem("zombie_eye");
   public static BasicScavengerItem SCAVENGER_ZOMBIE_HIDE = new BasicScavengerItem("zombie_hide");
   public static BasicScavengerItem SCAVENGER_ZOMBIE_NOSE = new BasicScavengerItem("zombie_nose");
   public static BasicScavengerItem SCAVENGER_ZOMBIE_VIAL = new BasicScavengerItem("zombie_blood_vial");
   public static BasicScavengerItem SCAVENGER_TREASURE_BANGLE_BLUE = new BasicScavengerItem("blue_bangle");
   public static BasicScavengerItem SCAVENGER_TREASURE_BANGLE_PINK = new BasicScavengerItem("pink_bangle");
   public static BasicScavengerItem SCAVENGER_TREASURE_BANGLE_GREEN = new BasicScavengerItem("green_bangle");
   public static BasicScavengerItem SCAVENGER_TREASURE_EARRINGS = new BasicScavengerItem("earrings");
   public static BasicScavengerItem SCAVENGER_TREASURE_GOBLET = new BasicScavengerItem("goblet");
   public static BasicScavengerItem SCAVENGER_TREASURE_SACK = new BasicScavengerItem("sack");
   public static BasicScavengerItem SCAVENGER_TREASURE_SCROLL_RED = new BasicScavengerItem("red_scroll");
   public static BasicScavengerItem SCAVENGER_TREASURE_SCROLL_BLUE = new BasicScavengerItem("blue_scroll");
   public static BasicScavengerItem SCAVENGER_SCRAP_BROKEN_POTTERY = new BasicScavengerItem("broken_pottery");
   public static BasicScavengerItem SCAVENGER_SCRAP_CRACKED_PEARL = new BasicScavengerItem("cracked_pearl");
   public static BasicScavengerItem SCAVENGER_SCRAP_CRACKED_SCRIPT = new BasicScavengerItem("cracked_script");
   public static BasicScavengerItem SCAVENGER_SCRAP_EMPTY_JAR = new BasicScavengerItem("empty_jar");
   public static BasicScavengerItem SCAVENGER_SCRAP_OLD_BOOK = new BasicScavengerItem("old_book");
   public static BasicScavengerItem SCAVENGER_SCRAP_POTTERY_SHARD = new BasicScavengerItem("pottery_shard");
   public static BasicScavengerItem SCAVENGER_SCRAP_POULTICE_JAR = new BasicScavengerItem("poultice_jar");
   public static BasicScavengerItem SCAVENGER_SCRAP_PRESERVES_JAR = new BasicScavengerItem("preserves_jar");
   public static BasicScavengerItem SCAVENGER_SCRAP_RIPPED_PAGE = new BasicScavengerItem("ripped_page");
   public static BasicScavengerItem SCAVENGER_SCRAP_SADDLE_BAG = new BasicScavengerItem("saddle_bag");
   public static BasicScavengerItem SCAVENGER_SCRAP_SPICE_JAR = new BasicScavengerItem("spice_jar");
   public static BasicScavengerItem SCAVENGER_SCRAP_WIZARD_WAND = new BasicScavengerItem("wizard_wand");
   public static BasicScavengerItem SCAVENGER_MOB_BLACK = new BasicScavengerItem("mob_black");
   public static BasicScavengerItem SCAVENGER_MOB_GREEN = new BasicScavengerItem("mob_green");
   public static BasicScavengerItem SCAVENGER_MOB_PURPLE = new BasicScavengerItem("mob_purple");
   public static BasicItem LIFE_SCROLL = new BasicItem(VaultMod.id("life_scroll"));
   public static BasicItem AURA_SCROLL = new BasicItem(VaultMod.id("aura_scroll"));
   public static BasicItem MEMORY_POWDER = new BasicItem(VaultMod.id("memory_powder"));
   public static BasicItem MEMORY_SHARD = new BasicItem(VaultMod.id("memory_shard"));
   public static BasicItem MEMORY_CRYSTAL = new BasicItem(VaultMod.id("memory_crystal"));
   public static PaxelItem VAULTERITE_PICKAXE = new PaxelItem(VaultMod.id("vaulterite_pickaxe"));
   public static PaxelItem VAULT_PICKAXE = new PaxelItem(VaultMod.id("vault_pickaxe"));
   public static PaxelItem BLACK_CHROMATIC_PICKAXE = new PaxelItem(VaultMod.id("black_chromatic_pickaxe"));
   public static PaxelItem ECHOING_PICKAXE = new PaxelItem(VaultMod.id("echoing_pickaxe"));
   public static PaxelItem PRISMATIC_PICKAXE = new PaxelItem(VaultMod.id("prismatic_pickaxe"));
   public static PaxelJewelItem SPARKING_JEWEL = new PaxelJewelItem(VaultMod.id("sparking_jewel"), PaxelItem.Perk.QUICK);
   public static PaxelJewelItem ECHOING_JEWEL = new PaxelJewelItem(VaultMod.id("echoing_jewel"), PaxelItem.Perk.IMMORTAL);
   public static PaxelJewelItem ISKALLIC_JEWEL = new PaxelJewelItem(VaultMod.id("iskallic_jewel"), PaxelItem.Perk.PULVERISING);
   public static PaxelJewelItem GORGEOUS_JEWEL = new PaxelJewelItem(VaultMod.id("gorgeous_jewel"), PaxelItem.Perk.SHOVELING);
   public static PaxelJewelItem TUBIC_JEWEL = new PaxelJewelItem(VaultMod.id("tubic_jewel"), PaxelItem.Perk.HAMMERING);
   public static PaxelJewelItem UPAL_JEWEL = new PaxelJewelItem(VaultMod.id("upal_jewel"), PaxelItem.Perk.EXCAVATING);
   public static PaxelJewelItem BOMBING_JEWEL = new PaxelJewelItem(VaultMod.id("bombing_jewel"), PaxelItem.Perk.SMELTING);
   public static PaxelJewelItem POGGING_JEWEL = new PaxelJewelItem(VaultMod.id("pogging_jewel"), PaxelItem.Perk.SHATTERING);
   public static PaxelJewelItem BENITE_JEWEL = new PaxelJewelItem(VaultMod.id("benite_jewel"), PaxelItem.Perk.FARMING);
   public static PaxelJewelItem XEN_JEWEL = new PaxelJewelItem(VaultMod.id("xen_jewel"), PaxelItem.Perk.AXING);
   public static PaxelJewelItem ASH_JEWEL = new PaxelJewelItem(VaultMod.id("ash_jewel"), PaxelItem.Perk.STURDY);
   public static PaxelJewelItem PETZAN_JEWEL = new PaxelJewelItem(VaultMod.id("petzan_jewel"), PaxelItem.Perk.REINFORCED);
   public static BasicItem VAULT_CHARM = new BasicItem(VaultMod.id("vault_charm"), new Properties().tab(VAULT_MOD_GROUP).stacksTo(1));
   public static VaultCharmUpgrade CHARM_UPGRADE_TIER_1 = new VaultCharmUpgrade(
      VaultMod.id("charm_upgrade_tier_1"), VaultCharmUpgrade.Tier.ONE, new Properties().tab(VAULT_MOD_GROUP).stacksTo(1).fireResistant()
   );
   public static VaultCharmUpgrade CHARM_UPGRADE_TIER_2 = new VaultCharmUpgrade(
      VaultMod.id("charm_upgrade_tier_2"), VaultCharmUpgrade.Tier.TWO, new Properties().tab(VAULT_MOD_GROUP).stacksTo(1).fireResistant()
   );
   public static VaultCharmUpgrade CHARM_UPGRADE_TIER_3 = new VaultCharmUpgrade(
      VaultMod.id("charm_upgrade_tier_3"), VaultCharmUpgrade.Tier.THREE, new Properties().tab(VAULT_MOD_GROUP).stacksTo(1).fireResistant()
   );
   public static VaultCharmUpgrade CHARM_UPGRADE_TIER_4 = new VaultCharmUpgrade(
      VaultMod.id("charm_upgrade_tier_4"), VaultCharmUpgrade.Tier.FOUR, new Properties().tab(VAULT_MOD_GROUP).stacksTo(1).fireResistant()
   );
   public static final BasicMobEggItem VAULT_FIGHTER_0_EGG = new BasicMobEggItem(
      VaultMod.id("vault_fighter_0_egg"),
      () -> ModEntities.VAULT_FIGHTER_TYPES.get(0),
      1447446,
      DyeColor.GRAY.getTextColor(),
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_FIGHTER_1_EGG = new BasicMobEggItem(
      VaultMod.id("vault_fighter_1_egg"),
      () -> ModEntities.VAULT_FIGHTER_TYPES.get(1),
      1447446,
      DyeColor.ORANGE.getTextColor(),
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_FIGHTER_2_EGG = new BasicMobEggItem(
      VaultMod.id("vault_fighter_2_egg"),
      () -> ModEntities.VAULT_FIGHTER_TYPES.get(2),
      1447446,
      DyeColor.BROWN.getTextColor(),
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_FIGHTER_3_EGG = new BasicMobEggItem(
      VaultMod.id("vault_fighter_3_egg"),
      () -> ModEntities.VAULT_FIGHTER_TYPES.get(3),
      1447446,
      DyeColor.CYAN.getTextColor(),
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_FIGHTER_4_EGG = new BasicMobEggItem(
      VaultMod.id("vault_fighter_4_egg"),
      () -> ModEntities.VAULT_FIGHTER_TYPES.get(4),
      1447446,
      DyeColor.GREEN.getTextColor(),
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_FIGHTER_5_EGG = new BasicMobEggItem(
      VaultMod.id("vault_fighter_5_egg"),
      () -> ModEntities.VAULT_FIGHTER_TYPES.get(5),
      1447446,
      DyeColor.LIGHT_BLUE.getTextColor(),
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_FIGHTER_6_EGG = new BasicMobEggItem(
      VaultMod.id("vault_fighter_6_egg"),
      () -> ModEntities.VAULT_FIGHTER_TYPES.get(6),
      1447446,
      DyeColor.LIGHT_GRAY.getTextColor(),
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_FIGHTER_7_EGG = new BasicMobEggItem(
      VaultMod.id("vault_fighter_7_egg"),
      () -> ModEntities.VAULT_FIGHTER_TYPES.get(7),
      1447446,
      DyeColor.MAGENTA.getTextColor(),
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_FIGHTER_8_EGG = new BasicMobEggItem(
      VaultMod.id("vault_fighter_8_egg"),
      () -> ModEntities.VAULT_FIGHTER_TYPES.get(8),
      1447446,
      DyeColor.RED.getTextColor(),
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_FIGHTER_9_EGG = new BasicMobEggItem(
      VaultMod.id("vault_fighter_9_egg"),
      () -> ModEntities.VAULT_FIGHTER_TYPES.get(9),
      1447446,
      DyeColor.LIME.getTextColor(),
      new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_SPIDER_EGG = new BasicMobEggItem(
      VaultMod.id("vault_spider_egg"), () -> ModEntities.VAULT_SPIDER, 3419431, 11013646, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem AGGRESSIVE_COW_EGG = new BasicMobEggItem(
      VaultMod.id("aggressive_cow_egg"), () -> ModEntities.AGGRESSIVE_COW, 4470310, 10592673, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem ELITE_HUSK_EGG = new BasicMobEggItem(
      VaultMod.id("elite_husk_egg"), () -> ModEntities.ELITE_HUSK, 7958625, 15125652, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem ELITE_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("elite_zombie_egg"), () -> ModEntities.ELITE_ZOMBIE, 44975, 7969893, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem ELITE_SPIDER_EGG = new BasicMobEggItem(
      VaultMod.id("elite_spider_egg"), () -> ModEntities.ELITE_SPIDER, 3419431, 11013646, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem ELITE_SKELETON_EGG = new BasicMobEggItem(
      VaultMod.id("elite_skeleton_egg"), () -> ModEntities.ELITE_SKELETON, 12698049, 4802889, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem ELITE_WITHER_SKELETON_EGG = new BasicMobEggItem(
      VaultMod.id("elite_wither_skeleton_egg"), () -> ModEntities.ELITE_WITHER_SKELETON, 1315860, 4672845, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem ELITE_STRAY_EGG = new BasicMobEggItem(
      VaultMod.id("elite_stray_egg"), () -> ModEntities.ELITE_STRAY, 6387319, 14543594, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem ELITE_ENDERMAN_EGG = new BasicMobEggItem(
      VaultMod.id("elite_enderman_egg"), () -> ModEntities.ELITE_ENDERMAN, 1447446, 0, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem ELITE_DROWNED_EGG = new BasicMobEggItem(
      VaultMod.id("elite_drowned_egg"), () -> ModEntities.ELITE_DROWNED, 9433559, 7969893, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem ELITE_WITCH_EGG = new BasicMobEggItem(
      VaultMod.id("elite_witch_egg"), () -> ModEntities.ELITE_WITCH, 3407872, 5349438, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_CREEPER_EGG = new BasicMobEggItem(
      VaultMod.id("t1_creeper_egg"), () -> ModEntities.T1_CREEPER, 894731, 0, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_DROWNED_EGG = new BasicMobEggItem(
      VaultMod.id("t1_drowned_egg"), () -> ModEntities.T1_DROWNED, 9433559, 7969893, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_ENDERMAN_EGG = new BasicMobEggItem(
      VaultMod.id("t1_enderman_egg"), () -> ModEntities.T1_ENDERMAN, 1447446, 0, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_HUSK_EGG = new BasicMobEggItem(
      VaultMod.id("t1_husk_egg"), () -> ModEntities.T1_HUSK, 7958625, 15125652, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_PIGLIN_EGG = new BasicMobEggItem(
      VaultMod.id("t1_piglin_egg"), () -> ModEntities.T1_PIGLIN, 10051392, 16380836, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_SKELETON_EGG = new BasicMobEggItem(
      VaultMod.id("t1_skeleton_egg"), () -> ModEntities.T1_SKELETON, 12698049, 4802889, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_STRAY_EGG = new BasicMobEggItem(
      VaultMod.id("t1_stray_egg"), () -> ModEntities.T1_STRAY, 6387319, 14543594, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_WITHER_SKELETON_EGG = new BasicMobEggItem(
      VaultMod.id("t1_wither_skeleton_egg"), () -> ModEntities.T1_WITHER_SKELETON, 1315860, 4672845, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t1_zombie_egg"), () -> ModEntities.T1_ZOMBIE, 44975, 7969893, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_CREEPER_EGG = new BasicMobEggItem(
      VaultMod.id("t2_creeper_egg"), () -> ModEntities.T2_CREEPER, 894731, 0, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_DROWNED_EGG = new BasicMobEggItem(
      VaultMod.id("t2_drowned_egg"), () -> ModEntities.T2_DROWNED, 9433559, 7969893, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_ENDERMAN_EGG = new BasicMobEggItem(
      VaultMod.id("t2_enderman_egg"), () -> ModEntities.T2_ENDERMAN, 1447446, 0, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_HUSK_EGG = new BasicMobEggItem(
      VaultMod.id("t2_husk_egg"), () -> ModEntities.T2_HUSK, 7958625, 15125652, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_PIGLIN_EGG = new BasicMobEggItem(
      VaultMod.id("t2_piglin_egg"), () -> ModEntities.T2_PIGLIN, 10051392, 16380836, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_SKELETON_EGG = new BasicMobEggItem(
      VaultMod.id("t2_skeleton_egg"), () -> ModEntities.T2_SKELETON, 12698049, 4802889, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_STRAY_EGG = new BasicMobEggItem(
      VaultMod.id("t2_stray_egg"), () -> ModEntities.T2_STRAY, 6387319, 14543594, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_WITHER_SKELETON_EGG = new BasicMobEggItem(
      VaultMod.id("t2_wither_skeleton_egg"), () -> ModEntities.T2_WITHER_SKELETON, 1315860, 4672845, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t2_zombie_egg"), () -> ModEntities.T2_ZOMBIE, 44975, 7969893, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_CREEPER_EGG = new BasicMobEggItem(
      VaultMod.id("t3_creeper_egg"), () -> ModEntities.T3_CREEPER, 894731, 0, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_DROWNED_EGG = new BasicMobEggItem(
      VaultMod.id("t3_drowned_egg"), () -> ModEntities.T3_DROWNED, 9433559, 7969893, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_ENDERMAN_EGG = new BasicMobEggItem(
      VaultMod.id("t3_enderman_egg"), () -> ModEntities.T3_ENDERMAN, 1447446, 0, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_HUSK_EGG = new BasicMobEggItem(
      VaultMod.id("t3_husk_egg"), () -> ModEntities.T3_HUSK, 7958625, 15125652, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_SKELETON_EGG = new BasicMobEggItem(
      VaultMod.id("t3_skeleton_egg"), () -> ModEntities.T3_SKELETON, 12698049, 4802889, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_STRAY_EGG = new BasicMobEggItem(
      VaultMod.id("t3_stray_egg"), () -> ModEntities.T3_STRAY, 6387319, 14543594, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_WITHER_SKELETON_EGG = new BasicMobEggItem(
      VaultMod.id("t3_wither_skeleton_egg"), () -> ModEntities.T3_WITHER_SKELETON, 1315860, 4672845, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_PIGLIN_EGG = new BasicMobEggItem(
      VaultMod.id("t3_piglin_egg"), () -> ModEntities.T3_PIGLIN, 10051392, 16380836, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t3_zombie_egg"), () -> ModEntities.T3_ZOMBIE, 44975, 7969893, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_GREEN_GUMMY_SOLDIER_EGG = new BasicMobEggItem(
      VaultMod.id("vault_green_gummy_soldier_egg"), () -> ModEntities.VAULT_GREEN_GUMMY_SOLDIER, 2538582, 3593854, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_BLUE_GUMMY_SOLDIER_EGG = new BasicMobEggItem(
      VaultMod.id("vault_blue_gummy_soldier_egg"), () -> ModEntities.VAULT_BLUE_GUMMY_SOLDIER, 5008827, 6189781, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_YELLOW_GUMMY_SOLDIER_EGG = new BasicMobEggItem(
      VaultMod.id("vault_yellow_gummy_soldier_egg"), () -> ModEntities.VAULT_YELLOW_GUMMY_SOLDIER, 13933333, 15847205, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_RED_GUMMY_SOLDIER_EGG = new BasicMobEggItem(
      VaultMod.id("vault_red_gummy_soldier_egg"), () -> ModEntities.VAULT_RED_GUMMY_SOLDIER, 13183310, 15022667, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem WINTER_WOLF_EGG = new BasicMobEggItem(
      VaultMod.id("winter_wolf_egg"), () -> ModEntities.WINTER_WOLF, 10000793, 2845360, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem SHIVER_EGG = new BasicMobEggItem(
      VaultMod.id("shiver_egg"), () -> ModEntities.SHIVER, 3498338, 9677749, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T0_SKELETON_PIRATE_EGG = new BasicMobEggItem(
      VaultMod.id("t0_skeleton_pirate_egg"), () -> ModEntities.T0_SKELETON_PIRATE, 4549221, 7520684, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_SKELETON_PIRATE_EGG = new BasicMobEggItem(
      VaultMod.id("t1_skeleton_pirate_egg"), () -> ModEntities.T1_SKELETON_PIRATE, 4549221, 7520684, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_SKELETON_PIRATE_EGG = new BasicMobEggItem(
      VaultMod.id("t2_skeleton_pirate_egg"), () -> ModEntities.T2_SKELETON_PIRATE, 4549221, 7520684, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_SKELETON_PIRATE_EGG = new BasicMobEggItem(
      VaultMod.id("t3_skeleton_pirate_egg"), () -> ModEntities.T3_SKELETON_PIRATE, 4549221, 7520684, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T4_SKELETON_PIRATE_EGG = new BasicMobEggItem(
      VaultMod.id("t4_skeleton_pirate_egg"), () -> ModEntities.T4_SKELETON_PIRATE, 4549221, 7520684, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T5_SKELETON_PIRATE_EGG = new BasicMobEggItem(
      VaultMod.id("t5_skeleton_pirate_egg"), () -> ModEntities.T5_SKELETON_PIRATE, 4549221, 7520684, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T0_WINTERWALKER_EGG = new BasicMobEggItem(
      VaultMod.id("t0_winterwalker_egg"), () -> ModEntities.T0_WINTERWALKER, 5732472, 13623005, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_WINTERWALKER_EGG = new BasicMobEggItem(
      VaultMod.id("t2_winterwalker_egg"), () -> ModEntities.T2_WINTERWALKER, 5732472, 13623005, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_WINTERWALKER_EGG = new BasicMobEggItem(
      VaultMod.id("t1_winterwalker_egg"), () -> ModEntities.T1_WINTERWALKER, 5732472, 13623005, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_WINTERWALKER_EGG = new BasicMobEggItem(
      VaultMod.id("t3_winterwalker_egg"), () -> ModEntities.T3_WINTERWALKER, 5732472, 13623005, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T4_WINTERWALKER_EGG = new BasicMobEggItem(
      VaultMod.id("t4_winterwalker_egg"), () -> ModEntities.T4_WINTERWALKER, 5732472, 13623005, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T5_WINTERWALKER_EGG = new BasicMobEggItem(
      VaultMod.id("t5_winterwalker_egg"), () -> ModEntities.T5_WINTERWALKER, 5732472, 13623005, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T0_OVERGROWN_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t0_overgrown_zombie_egg"), () -> ModEntities.T0_OVERGROWN_ZOMBIE, 3161882, 8035635, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_OVERGROWN_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t1_overgrown_zombie_egg"), () -> ModEntities.T1_OVERGROWN_ZOMBIE, 3161882, 8035635, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_OVERGROWN_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t2_overgrown_zombie_egg"), () -> ModEntities.T2_OVERGROWN_ZOMBIE, 3161882, 8035635, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_OVERGROWN_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t3_overgrown_zombie_egg"), () -> ModEntities.T3_OVERGROWN_ZOMBIE, 3161882, 8035635, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T4_OVERGROWN_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t4_overgrown_zombie_egg"), () -> ModEntities.T4_OVERGROWN_ZOMBIE, 3161882, 8035635, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T5_OVERGROWN_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t5_overgrown_zombie_egg"), () -> ModEntities.T5_OVERGROWN_ZOMBIE, 3161882, 8035635, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T0_MUMMY_EGG = new BasicMobEggItem(
      VaultMod.id("t0_mummy_egg"), () -> ModEntities.T0_MUMMY, 11046226, 5656643, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_MUMMY_EGG = new BasicMobEggItem(
      VaultMod.id("t1_mummy_egg"), () -> ModEntities.T1_MUMMY, 11046226, 5656643, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_MUMMY_EGG = new BasicMobEggItem(
      VaultMod.id("t2_mummy_egg"), () -> ModEntities.T2_MUMMY, 11046226, 5656643, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T0_MUSHROOM_EGG = new BasicMobEggItem(
      VaultMod.id("t0_mushroom_egg"), () -> ModEntities.T0_MUSHROOM, 7696744, 13157045, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_MUSHROOM_EGG = new BasicMobEggItem(
      VaultMod.id("t1_mushroom_egg"), () -> ModEntities.T1_MUSHROOM, 7696744, 13157045, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_MUSHROOM_EGG = new BasicMobEggItem(
      VaultMod.id("t2_mushroom_egg"), () -> ModEntities.T2_MUSHROOM, 7696744, 13157045, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_MUSHROOM_EGG = new BasicMobEggItem(
      VaultMod.id("t3_mushroom_egg"), () -> ModEntities.T3_MUSHROOM, 7696744, 13157045, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T4_MUSHROOM_EGG = new BasicMobEggItem(
      VaultMod.id("t4_mushroom_egg"), () -> ModEntities.T4_MUSHROOM, 7696744, 13157045, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T5_MUSHROOM_EGG = new BasicMobEggItem(
      VaultMod.id("t5_mushroom_egg"), () -> ModEntities.T5_MUSHROOM, 7696744, 13157045, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem DEATHCAP_EGG = new BasicMobEggItem(
      VaultMod.id("deathcap_egg"), () -> ModEntities.DEATHCAP, 7696744, 13157045, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem SMOLCAP_EGG = new BasicMobEggItem(
      VaultMod.id("smolcap_egg"), () -> ModEntities.SMOLCAP, 7696744, 13157045, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T0_MINER_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t0_miner_zombie_egg"), () -> ModEntities.T0_MINER_ZOMBIE, 2698001, 4406815, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T1_MINER_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t1_miner_zombie_egg"), () -> ModEntities.T1_MINER_ZOMBIE, 2698001, 4406815, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T2_MINER_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t2_miner_zombie_egg"), () -> ModEntities.T2_MINER_ZOMBIE, 2698001, 4406815, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T3_MINER_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t3_miner_zombie_egg"), () -> ModEntities.T3_MINER_ZOMBIE, 2698001, 4406815, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T4_MINER_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t4_miner_zombie_egg"), () -> ModEntities.T4_MINER_ZOMBIE, 2698001, 4406815, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem T5_MINER_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("t5_miner_zombie_egg"), () -> ModEntities.T5_MINER_ZOMBIE, 2698001, 4406815, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem DEEP_DARK_ZOMBIE_EGG = new BasicMobEggItem(
      VaultMod.id("deep_dark_zombie_egg"), () -> ModEntities.DEEP_DARK_ZOMBIE, 660249, 2331531, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem DEEP_DARK_SKELETON_EGG = new BasicMobEggItem(
      VaultMod.id("deep_dark_skeleton_egg"), () -> ModEntities.DEEP_DARK_SKELETON, 660249, 2331531, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem DEEP_DARK_PIGLIN_EGG = new BasicMobEggItem(
      VaultMod.id("deep_dark_piglin_egg"), () -> ModEntities.DEEP_DARK_PIGLIN, 660249, 2331531, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem DEEP_DARK_SILVERFISH_EGG = new BasicMobEggItem(
      VaultMod.id("deep_dark_silverfish_egg"), () -> ModEntities.DEEP_DARK_SILVERFISH, 660249, 2331531, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem DEEP_DARK_HORROR_EGG = new BasicMobEggItem(
      VaultMod.id("deep_dark_horror_egg"), () -> ModEntities.DEEP_DARK_HORROR, 660249, 2331531, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_HORSE_EGG = new BasicMobEggItem(
      VaultMod.id("vault_horse_egg"), () -> ModEntities.VAULT_HORSE, 12623485, 15656192, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem VAULT_DOOD_EGG = new BasicMobEggItem(
      VaultMod.id("vault_dood_egg"), () -> ModEntities.VAULT_DOOD, 9534058, 12693665, new Properties().tab(VAULT_MOD_GROUP)
   );
   public static final BasicMobEggItem SPIRIT_EGG = new BasicMobEggItem(
      VaultMod.id("spirit_egg"), () -> ModEntities.SPIRIT, 12698049, 4802889, new Properties().tab(VAULT_MOD_GROUP)
   );

   private static CreativeModeTab createCreativeTab(String label, final Supplier<ItemStack> itemStack) {
      return new CreativeModeTab(label) {
         @Nonnull
         public ItemStack makeIcon() {
            return itemStack.get();
         }

         public boolean hasSearchBar() {
            return true;
         }

         @Nonnull
         public ResourceLocation getBackgroundImage() {
            return CreativeModeTab.TAB_SEARCH.getBackgroundImage();
         }
      };
   }

   public static void registerItems(Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();
      registry.register(SILVER_SCRAP);
      registry.register(BRONZE_SCRAP);
      registry.register(GORGEOUS_JEWEL);
      registry.register(ASH_JEWEL);
      registry.register(BENITE_JEWEL);
      registry.register(BOMBING_JEWEL);
      registry.register(ECHOING_JEWEL);
      registry.register(PETZAN_JEWEL);
      registry.register(POGGING_JEWEL);
      registry.register(TUBIC_JEWEL);
      registry.register(XEN_JEWEL);
      registry.register(SPARKING_JEWEL);
      registry.register(ISKALLIC_JEWEL);
      registry.register(UPAL_JEWEL);
      registry.register(GORGEOUS_GEMSTONE);
      registry.register(ASHIUM_GEMSTONE);
      registry.register(BENITOITE_GEMSTONE);
      registry.register(BOMIGNITE_GEMSTONE);
      registry.register(ECHOING_GEMSTONE);
      registry.register(PETZANITE_GEMSTONE);
      registry.register(POGGING_GEMSTONE);
      registry.register(TUBIUM_GEMSTONE);
      registry.register(WUTODIC_GEMSTONE);
      registry.register(XENIUM_GEMSTONE);
      registry.register(SPARKING_GEMSTONE);
      registry.register(ISKALLIC_GEMSTONE);
      registry.register(UPALINE_GEMSTONE);
      registry.register(PHOENIX_DUST);
      registry.register(PHOENIX_FEATHER);
      registry.register(DREAMSTONE);
      registry.register(EYE_OF_AVARICE);
      registry.register(MYSTICAL_POWDER);
      registry.register(ABYSSAL_ICHOR);
      registry.register(MOTE_CLARITY);
      registry.register(MOTE_PURITY);
      registry.register(MOTE_SANCTITY);
      registry.register(PLAIN_BURGER);
      registry.register(CHEESE_BURGER);
      registry.register(DOUBLE_CHEESE_BURGER);
      registry.register(DELUXE_CHEESE_BURGER);
      registry.register(CRISPY_DELUXE_CHEESE_BURGER);
      registry.register(SALTY_DELUXE_CHEESE_BURGER);
      registry.register(CHEESE_BURGER_FEAST);
      registry.register(SPICY_HEARTY_BURGER);
      registry.register(BURGER_CHEESE);
      registry.register(BURGER_CHILI);
      registry.register(BURGER_LETTUCE);
      registry.register(BURGER_PICKLES);
      registry.register(BURGER_SAUCE);
      registry.register(BURGER_TOMATO);
      registry.register(VAULT_COOKIE);
      registry.register(SKILL_ORB);
      registry.register(ALEXANDRITE_GEM);
      registry.register(BENITOITE_GEM);
      registry.register(LARIMAR_GEM);
      registry.register(BLACK_OPAL_GEM);
      registry.register(PAINITE_GEM);
      registry.register(ISKALLIUM_GEM);
      registry.register(GORGINITE_GEM);
      registry.register(SPARKLETINE_GEM);
      registry.register(ASHIUM_GEM);
      registry.register(BOMIGNITE_GEM);
      registry.register(TUBIUM_GEM);
      registry.register(WUTODIE_GEM);
      registry.register(UPALINE_GEM);
      registry.register(PUFFIUM_GEM);
      registry.register(PETZANITE_GEM);
      registry.register(XENIUM_GEM);
      registry.register(ECHO_GEM);
      registry.register(VAULT_ROCK);
      registry.register(POG);
      registry.register(ECHO_POG);
      registry.register(ECHOING_INGOT);
      registry.register(GEMMED_INGOT);
      registry.register(VAULT_CRYSTAL);
      registry.register(ISKALLIUM_KEY);
      registry.register(GORGINITE_KEY);
      registry.register(SPARKLETINE_KEY);
      registry.register(ASHIUM_KEY);
      registry.register(BOMIGNITE_KEY);
      registry.register(TUBIUM_KEY);
      registry.register(UPALINE_KEY);
      registry.register(PUFFIUM_KEY);
      registry.register(PETZANITE_KEY);
      registry.register(XENIUM_KEY);
      registry.register(RELIC_BOOSTER_PACK);
      registry.register(LEGENDARY_TREASURE_NORMAL);
      registry.register(LEGENDARY_TREASURE_RARE);
      registry.register(LEGENDARY_TREASURE_EPIC);
      registry.register(LEGENDARY_TREASURE_OMEGA);
      registry.register(UNIDENTIFIED_ARTIFACT);
      registry.register(RELIC);
      registry.register(RELIC_FRAGMENT);
      registry.register(SKILL_ORB_FRAME);
      registry.register(SKILL_SHARD);
      registry.register(BITTER_LEMON);
      registry.register(SOUR_ORANGE);
      registry.register(MYSTIC_PEAR);
      registry.register(KEY_PIECE);
      registry.register(KEY_MOULD);
      registry.register(BLANK_KEY);
      registry.register(NETHERITE_CLUSTER);
      registry.register(ISKALLIUM_CLUSTER);
      registry.register(GORGINITE_CLUSTER);
      registry.register(SPARKLETINE_CLUSTER);
      registry.register(ASHIUM_CLUSTER);
      registry.register(BOMIGNITE_CLUSTER);
      registry.register(TUBIUM_CLUSTER);
      registry.register(UPALINE_CLUSTER);
      registry.register(PUFFIUM_CLUSTER);
      registry.register(PETZANITE_CLUSTER);
      registry.register(XENIUM_CLUSTER);
      registry.register(POISONOUS_MUSHROOM);
      registry.register(VAULT_DIAMOND);
      registry.register(SKILL_ESSENCE);
      registry.register(UNIDENTIFIED_RELIC_FRAGMENT);
      registry.register(SWEET_KIWI);
      registry.register(HUNTER_EYE);
      registry.register(BURGER_PATTY);
      registry.register(BURGER_BUN);
      registry.register(VAULT_SCRAP);
      registry.register(TRINKET_SCRAP);
      registry.register(VAULT_INGOT);
      registry.register(MYSTERY_BOX);
      registry.register(DRILL_ARROW_PART);
      registry.register(DRILL_ARROW);
      registry.register(ROKKIT_LAUNCHA);
      registry.register(EMPTY_FLASK);
      registry.register(RESPEC_FLASK);
      registry.register(MYSTERY_EGG);
      registry.register(MYSTERY_HOSTILE_EGG);
      registry.register(ACCELERATION_CHIP);
      registry.register(PANDORAS_BOX);
      registry.register(ISKALLIUM_CHUNK);
      registry.register(GORGINITE_CHUNK);
      registry.register(SPARKLETINE_CHUNK);
      registry.register(ASHIUM_CHUNK);
      registry.register(BOMIGNITE_CHUNK);
      registry.register(TUBIUM_CHUNK);
      registry.register(UPALINE_CHUNK);
      registry.register(PUFFIUM_CHUNK);
      registry.register(PETZANITE_CHUNK);
      registry.register(XENIUM_CHUNK);
      registry.register(OMEGA_POG);
      registry.register(ETERNAL_SOUL);
      registry.register(KNOWLEDGE_STAR_SHARD);
      registry.register(KNOWLEDGE_STAR_CORE);
      registry.register(KNOWLEDGE_STAR_ESSENCE);
      registry.register(KNOWLEDGE_STAR);
      registry.register(ARCHETYPE_STAR_SHARD);
      registry.register(ARCHETYPE_STAR_CORE);
      registry.register(ARCHETYPE_STAR_ESSENCE);
      registry.register(ARCHETYPE_STAR);
      registry.register(SWORD);
      registry.register(AXE);
      registry.register(HELMET);
      registry.register(CHESTPLATE);
      registry.register(LEGGINGS);
      registry.register(BOOTS);
      registry.register(SHIELD);
      registry.register(ETCHING);
      registry.register(ETCHING_FRAGMENT);
      registry.register(IDOL_BENEVOLENT);
      registry.register(IDOL_OMNISCIENT);
      registry.register(IDOL_TIMEKEEPER);
      registry.register(IDOL_MALEVOLENCE);
      registry.register(TOOL);
      registry.register(JEWEL);
      registry.register(GEMSTONE);
      registry.register(BOTTLE);
      registry.register(WAND);
      registry.register(INFUSED_ETERNAL_SOUL);
      registry.register(UNKNOWN_EGG);
      registry.register(VAULT_APPLE);
      registry.register(HEARTY_APPLE);
      registry.register(PERFECT_ALEXANDRITE);
      registry.register(PERFECT_PAINITE);
      registry.register(PERFECT_BENITOITE);
      registry.register(PERFECT_LARIMAR);
      registry.register(PERFECT_BLACK_OPAL);
      registry.register(PERFECT_ECHO_GEM);
      registry.register(PERFECT_WUTODIE);
      registry.register(VAULT_DUST);
      registry.register(VAULT_NUGGET);
      registry.register(MAGNETITE);
      registry.register(MAGNETITE_INGOT);
      registry.register(VAULT_ESSENCE);
      registry.register(VAULT_PLATING);
      registry.register(VAULT_ALLOY);
      registry.register(REPAIR_CORE);
      registry.register(WILD_FOCUS);
      registry.register(AMPLIFYING_FOCUS);
      registry.register(NULLIFYING_FOCUS);
      registry.register(OPPORTUNISTIC_FOCUS);
      registry.register(RESILIENT_FOCUS);
      registry.register(FUNDAMENTAL_FOCUS);
      registry.register(FACETED_FOCUS);
      registry.register(CHAOTIC_FOCUS);
      registry.register(WAXING_FOCUS);
      registry.register(WANING_FOCUS);
      registry.register(VAULT_CATALYST);
      registry.register(VAULT_CATALYST_INFUSED);
      registry.register(VAULT_CATALYST_CHAOS);
      registry.register(RUNE);
      registry.register(INSCRIPTION);
      registry.register(INSCRIPTION_PIECE);
      registry.register(AUGMENT);
      registry.register(VAULT_CATALYST_FRAGMENT);
      registry.register(CRYSTAL_SHARD_BENEVOLENT);
      registry.register(CRYSTAL_SHARD_OMNISCIENT);
      registry.register(CRYSTAL_SHARD_TIMEKEEPER);
      registry.register(CRYSTAL_SHARD_MALEVOLENCE);
      registry.register(SOUL_FLAME);
      registry.register(CRYSTAL_SEAL_EMPTY);
      registry.register(CRYSTAL_SEAL_EXECUTIONER);
      registry.register(CRYSTAL_SEAL_HUNTER);
      registry.register(CRYSTAL_SEAL_ARCHITECT);
      registry.register(CRYSTAL_SEAL_ANCIENTS);
      registry.register(CRYSTAL_SEAL_RAID);
      registry.register(CRYSTAL_SEAL_CAKE);
      registry.register(CRYSTAL_SEAL_SAGE);
      registry.register(CRYSTAL_SEAL_SPEEDRUN);
      registry.register(IDENTIFICATION_TOME);
      registry.register(UNKNOWN_ITEM);
      registry.register(SOUL_SHARD);
      registry.register(SOUL_DUST);
      registry.register(BANISHED_SOUL);
      registry.register(SHARD_POUCH);
      registry.register(TRINKET);
      registry.register(VAULTERITE_PICKAXE);
      registry.register(VAULT_PICKAXE);
      registry.register(PRISMATIC_PICKAXE);
      registry.register(BLACK_CHROMATIC_PICKAXE);
      registry.register(ECHOING_PICKAXE);
      registry.register(INFINITE_WATER_BUCKET);
      registry.register(MAGNET);
      registry.register(SCAVENGER_CREEPER_EYE);
      registry.register(SCAVENGER_CREEPER_FOOT);
      registry.register(SCAVENGER_CREEPER_FUSE);
      registry.register(SCAVENGER_CREEPER_TNT);
      registry.register(SCAVENGER_CREEPER_VIAL);
      registry.register(SCAVENGER_CREEPER_CHARM);
      registry.register(SCAVENGER_DROWNED_BARNACLE);
      registry.register(SCAVENGER_DROWNED_EYE);
      registry.register(SCAVENGER_DROWNED_HIDE);
      registry.register(SCAVENGER_DROWNED_VIAL);
      registry.register(SCAVENGER_DROWNED_CHARM);
      registry.register(SCAVENGER_SKELETON_SHARD);
      registry.register(SCAVENGER_SKELETON_EYE);
      registry.register(SCAVENGER_SKELETON_RIBCAGE);
      registry.register(SCAVENGER_SKELETON_SKULL);
      registry.register(SCAVENGER_SKELETON_WISHBONE);
      registry.register(SCAVENGER_SKELETON_VIAL);
      registry.register(SCAVENGER_SKELETON_CHARM);
      registry.register(SCAVENGER_SPIDER_FANGS);
      registry.register(SCAVENGER_SPIDER_LEG);
      registry.register(SCAVENGER_SPIDER_WEBBING);
      registry.register(SCAVENGER_SPIDER_CURSED_CHARM);
      registry.register(SCAVENGER_SPIDER_VIAL);
      registry.register(SCAVENGER_SPIDER_CHARM);
      registry.register(SCAVENGER_ZOMBIE_BRAIN);
      registry.register(SCAVENGER_ZOMBIE_ARM);
      registry.register(SCAVENGER_ZOMBIE_EAR);
      registry.register(SCAVENGER_ZOMBIE_EYE);
      registry.register(SCAVENGER_ZOMBIE_HIDE);
      registry.register(SCAVENGER_ZOMBIE_NOSE);
      registry.register(SCAVENGER_ZOMBIE_VIAL);
      registry.register(SCAVENGER_TREASURE_BANGLE_BLUE);
      registry.register(SCAVENGER_TREASURE_BANGLE_PINK);
      registry.register(SCAVENGER_TREASURE_BANGLE_GREEN);
      registry.register(SCAVENGER_TREASURE_EARRINGS);
      registry.register(SCAVENGER_TREASURE_GOBLET);
      registry.register(SCAVENGER_TREASURE_SACK);
      registry.register(SCAVENGER_TREASURE_SCROLL_RED);
      registry.register(SCAVENGER_TREASURE_SCROLL_BLUE);
      registry.register(SCAVENGER_SCRAP_BROKEN_POTTERY);
      registry.register(SCAVENGER_SCRAP_CRACKED_PEARL);
      registry.register(SCAVENGER_SCRAP_CRACKED_SCRIPT);
      registry.register(SCAVENGER_SCRAP_EMPTY_JAR);
      registry.register(SCAVENGER_SCRAP_OLD_BOOK);
      registry.register(SCAVENGER_SCRAP_POTTERY_SHARD);
      registry.register(SCAVENGER_SCRAP_POULTICE_JAR);
      registry.register(SCAVENGER_SCRAP_PRESERVES_JAR);
      registry.register(SCAVENGER_SCRAP_RIPPED_PAGE);
      registry.register(SCAVENGER_SCRAP_SADDLE_BAG);
      registry.register(SCAVENGER_SCRAP_SPICE_JAR);
      registry.register(SCAVENGER_SCRAP_WIZARD_WAND);
      registry.register(SCAVENGER_MOB_BLACK);
      registry.register(SCAVENGER_MOB_GREEN);
      registry.register(SCAVENGER_MOB_PURPLE);
      registry.register(VOID_LIQUID_BUCKET);
      registry.register(MOD_BOX);
      registry.register(VAULTERITE_INGOT);
      registry.register(RED_VAULT_ESSENCE);
      registry.register(UNIDENTIFIED_TREASURE_KEY);
      registry.register(LIFE_SCROLL);
      registry.register(AURA_SCROLL);
      registry.register(ARTIFACT_FRAGMENT);
      registry.register(VAULT_CHARM);
      registry.register(CHARM_UPGRADE_TIER_1);
      registry.register(CHARM_UPGRADE_TIER_2);
      registry.register(CHARM_UPGRADE_TIER_3);
      registry.register(CHARM_UPGRADE_TIER_4);
      registry.register(KEYSTONE_IDONA);
      registry.register(KEYSTONE_VELARA);
      registry.register(KEYSTONE_TENOS);
      registry.register(KEYSTONE_WENDARR);
      registry.register(GOD_BLESSING);
      registry.register(RAW_CHROMATIC_IRON);
      registry.register(CHROMATIC_IRON_TINY_DUST);
      registry.register(CHROMATIC_IRON_SMALL_DUST);
      registry.register(CHROMATIC_IRON_DUST);
      registry.register(CHROMATIC_IRON_DIRTY_DUST);
      registry.register(CHROMATIC_IRON_SHARD);
      registry.register(CHROMATIC_IRON_CLUMP);
      registry.register(CHROMATIC_IRON_CRYSTAL);
      registry.register(CHROMATIC_IRON_NUGGET);
      registry.register(CHROMATIC_IRON_INGOT);
      registry.register(CHROMATIC_STEEL_NUGGET);
      registry.register(CHROMATIC_STEEL_INGOT);
      registry.register(BLACK_CHROMATIC_STEEL_NUGGET);
      registry.register(BLACK_CHROMATIC_STEEL_INGOT);
      registry.register(MAGIC_SILK);
      registry.register(CHIPPED_VAULT_ROCK);
      registry.register(CARBON);
      registry.register(CARBON_NUGGET);
      registry.register(DIAMOND_NUGGET);
      registry.register(DRIFTWOOD);
      registry.register(WOODEN_CHEST_SCROLL);
      registry.register(ORNATE_CHEST_SCROLL);
      registry.register(GILDED_CHEST_SCROLL);
      registry.register(LIVING_CHEST_SCROLL);
      registry.register(HARDENED_CHEST_SCROLL);
      registry.register(FLESH_CHEST_SCROLL);
      registry.register(ENIGMA_CHEST_SCROLL);
      registry.register(ALTAR_CHEST_SCROLL);
      registry.register(TREASURE_CHEST_SCROLL);
      registry.register(WUTODIC_MASS);
      registry.register(HARDENED_WUTODIC_MASS);
      registry.register(WUTODIC_SILVER_NUGGET);
      registry.register(WUTODIC_SILVER_INGOT);
      registry.register(SUBLIME_VAULT_ELIXIR);
      registry.register(VAULT_MEAT);
      registry.register(RAW_VAULT_STEAK);
      registry.register(COOKED_VAULT_STEAK);
      registry.register(SUBLIME_VAULT_SUBSTANCE);
      registry.register(VAULT_DOLL);
      registry.register(VAULT_COMPASS);
      registry.register(VAULT_FIGHTER_0_EGG);
      registry.register(VAULT_FIGHTER_1_EGG);
      registry.register(VAULT_FIGHTER_2_EGG);
      registry.register(VAULT_FIGHTER_3_EGG);
      registry.register(VAULT_FIGHTER_4_EGG);
      registry.register(VAULT_FIGHTER_5_EGG);
      registry.register(VAULT_FIGHTER_6_EGG);
      registry.register(VAULT_FIGHTER_7_EGG);
      registry.register(VAULT_FIGHTER_8_EGG);
      registry.register(VAULT_FIGHTER_9_EGG);
      registry.register(VAULT_SPIDER_EGG);
      registry.register(AGGRESSIVE_COW_EGG);
      registry.register(ELITE_HUSK_EGG);
      registry.register(ELITE_ZOMBIE_EGG);
      registry.register(ELITE_SPIDER_EGG);
      registry.register(ELITE_SKELETON_EGG);
      registry.register(ELITE_WITHER_SKELETON_EGG);
      registry.register(ELITE_STRAY_EGG);
      registry.register(ELITE_ENDERMAN_EGG);
      registry.register(ELITE_DROWNED_EGG);
      registry.register(ELITE_WITCH_EGG);
      registry.register(T1_CREEPER_EGG);
      registry.register(T1_DROWNED_EGG);
      registry.register(T1_ENDERMAN_EGG);
      registry.register(T1_HUSK_EGG);
      registry.register(T1_PIGLIN_EGG);
      registry.register(T1_SKELETON_EGG);
      registry.register(T1_STRAY_EGG);
      registry.register(T1_WITHER_SKELETON_EGG);
      registry.register(T1_ZOMBIE_EGG);
      registry.register(T2_CREEPER_EGG);
      registry.register(T2_DROWNED_EGG);
      registry.register(T2_ENDERMAN_EGG);
      registry.register(T2_HUSK_EGG);
      registry.register(T2_PIGLIN_EGG);
      registry.register(T2_SKELETON_EGG);
      registry.register(T2_STRAY_EGG);
      registry.register(T2_WITHER_SKELETON_EGG);
      registry.register(T2_ZOMBIE_EGG);
      registry.register(T3_CREEPER_EGG);
      registry.register(T3_DROWNED_EGG);
      registry.register(T3_ENDERMAN_EGG);
      registry.register(T3_HUSK_EGG);
      registry.register(T3_PIGLIN_EGG);
      registry.register(T3_SKELETON_EGG);
      registry.register(T3_STRAY_EGG);
      registry.register(T3_WITHER_SKELETON_EGG);
      registry.register(T3_ZOMBIE_EGG);
      registry.register(VAULT_GREEN_GUMMY_SOLDIER_EGG);
      registry.register(VAULT_BLUE_GUMMY_SOLDIER_EGG);
      registry.register(VAULT_YELLOW_GUMMY_SOLDIER_EGG);
      registry.register(VAULT_RED_GUMMY_SOLDIER_EGG);
      registry.register(WINTER_WOLF_EGG);
      registry.register(SHIVER_EGG);
      registry.register(T0_SKELETON_PIRATE_EGG);
      registry.register(T1_SKELETON_PIRATE_EGG);
      registry.register(T2_SKELETON_PIRATE_EGG);
      registry.register(T3_SKELETON_PIRATE_EGG);
      registry.register(T4_SKELETON_PIRATE_EGG);
      registry.register(T5_SKELETON_PIRATE_EGG);
      registry.register(T0_WINTERWALKER_EGG);
      registry.register(T1_WINTERWALKER_EGG);
      registry.register(T2_WINTERWALKER_EGG);
      registry.register(T3_WINTERWALKER_EGG);
      registry.register(T4_WINTERWALKER_EGG);
      registry.register(T5_WINTERWALKER_EGG);
      registry.register(T0_OVERGROWN_ZOMBIE_EGG);
      registry.register(T1_OVERGROWN_ZOMBIE_EGG);
      registry.register(T2_OVERGROWN_ZOMBIE_EGG);
      registry.register(T3_OVERGROWN_ZOMBIE_EGG);
      registry.register(T4_OVERGROWN_ZOMBIE_EGG);
      registry.register(T5_OVERGROWN_ZOMBIE_EGG);
      registry.register(T0_MUMMY_EGG);
      registry.register(T1_MUMMY_EGG);
      registry.register(T2_MUMMY_EGG);
      registry.register(T0_MUSHROOM_EGG);
      registry.register(T1_MUSHROOM_EGG);
      registry.register(T2_MUSHROOM_EGG);
      registry.register(T3_MUSHROOM_EGG);
      registry.register(T4_MUSHROOM_EGG);
      registry.register(T5_MUSHROOM_EGG);
      registry.register(DEATHCAP_EGG);
      registry.register(SMOLCAP_EGG);
      registry.register(T0_MINER_ZOMBIE_EGG);
      registry.register(T1_MINER_ZOMBIE_EGG);
      registry.register(T2_MINER_ZOMBIE_EGG);
      registry.register(T3_MINER_ZOMBIE_EGG);
      registry.register(T4_MINER_ZOMBIE_EGG);
      registry.register(T5_MINER_ZOMBIE_EGG);
      registry.register(DEEP_DARK_ZOMBIE_EGG);
      registry.register(DEEP_DARK_SKELETON_EGG);
      registry.register(DEEP_DARK_PIGLIN_EGG);
      registry.register(DEEP_DARK_SILVERFISH_EGG);
      registry.register(DEEP_DARK_HORROR_EGG);
      registry.register(VAULT_HORSE_EGG);
      registry.register(VAULT_DOOD_EGG);
      registry.register(SPIRIT_EGG);
      registry.register(EXTRAORDINARY_ALEXANDRITE);
      registry.register(EXTRAORDINARY_PAINITE);
      registry.register(EXTRAORDINARY_BENITOITE);
      registry.register(EXTRAORDINARY_LARIMAR);
      registry.register(EXTRAORDINARY_BLACK_OPAL);
      registry.register(EXTRAORDINARY_ECHO_GEM);
      registry.register(EXTRAORDINARY_WUTODIE);
      registry.register(REGRET_NUGGET);
      registry.register(REGRET_CHUNK);
      registry.register(REGRET_ORB);
      registry.register(NEURALIZER);
      registry.register(MEMORY_POWDER);
      registry.register(MEMORY_SHARD);
      registry.register(MEMORY_CRYSTAL);
      registry.register(SUBLIME_VAULT_VISION);
      registry.register(ANIMAL_JAR);
      registry.register(LOST_BOUNTY);
      registry.register(BOUNTY_PEARL);
      registry.register(OLD_NOTES);
      registry.register(QUEST_BOOK);
      registry.register(ERROR_ITEM);
      registry.register(TOPAZ_SHARD);
      registry.register(GILDED_INGOT);
      registry.register(ORNATE_INGOT);
      registry.register(ANCIENT_COPPER_INGOT);
      registry.register(VELVET);
      registry.register(ROTTEN_MEAT);
      registry.register(WOODEN_CHUNK);
      registry.register(OVERGROWN_WOODEN_CHUNK);
      registry.register(SANDY_ROCKS);
      registry.register(LIVING_ROCK);
      registry.register(MOSSY_BONE);
      registry.register(VAULT_SWEETS);
   }
}
