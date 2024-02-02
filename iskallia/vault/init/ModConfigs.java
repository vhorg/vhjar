package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.config.AbilitiesConfig;
import iskallia.vault.config.AbilitiesDescriptionsConfig;
import iskallia.vault.config.AbilitiesDrillDigDenyConfig;
import iskallia.vault.config.AbilitiesGUIConfig;
import iskallia.vault.config.AbilitiesVeinMinerDenyConfig;
import iskallia.vault.config.AbilitiesVignetteConfig;
import iskallia.vault.config.AlchemyTableConfig;
import iskallia.vault.config.AncientCopperGolemConfig;
import iskallia.vault.config.ArchetypeDescriptionsConfig;
import iskallia.vault.config.ArchetypeGUIConfig;
import iskallia.vault.config.ArchetypesConfig;
import iskallia.vault.config.ArchitectConfig;
import iskallia.vault.config.AscensionConfig;
import iskallia.vault.config.AscensionForgeConfig;
import iskallia.vault.config.AugmentConfig;
import iskallia.vault.config.BestiaryConfig;
import iskallia.vault.config.BlackMarketConfig;
import iskallia.vault.config.CatalystInfusionTableConfig;
import iskallia.vault.config.ChampionsConfig;
import iskallia.vault.config.CharmConfig;
import iskallia.vault.config.ColorsConfig;
import iskallia.vault.config.Config;
import iskallia.vault.config.CryoChamberConfig;
import iskallia.vault.config.CrystalBuddingConfig;
import iskallia.vault.config.CustomEntitySpawnerConfig;
import iskallia.vault.config.DivineParadoxConfig;
import iskallia.vault.config.DurabilityConfig;
import iskallia.vault.config.EliteSpawnerConfig;
import iskallia.vault.config.ElixirConfig;
import iskallia.vault.config.EntityGroupsConfig;
import iskallia.vault.config.EtchingConfig;
import iskallia.vault.config.EternalAttributeConfig;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.config.EternalConfig;
import iskallia.vault.config.EternalsNamepoolConfig;
import iskallia.vault.config.ExpertisesConfig;
import iskallia.vault.config.ExpertisesGUIConfig;
import iskallia.vault.config.EyesoreConfig;
import iskallia.vault.config.FighterConfig;
import iskallia.vault.config.GearModelRollRaritiesConfig;
import iskallia.vault.config.HeraldTrophyConfig;
import iskallia.vault.config.InscriptionConfig;
import iskallia.vault.config.ItemGroupsConfig;
import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.config.LegacyMagnetConfigs;
import iskallia.vault.config.LegendaryTreasureEpicConfig;
import iskallia.vault.config.LegendaryTreasureNormalConfig;
import iskallia.vault.config.LegendaryTreasureOmegaConfig;
import iskallia.vault.config.LegendaryTreasureRareConfig;
import iskallia.vault.config.LootInfoConfig;
import iskallia.vault.config.ManaConfig;
import iskallia.vault.config.MenuPlayerStatDescriptionConfig;
import iskallia.vault.config.ModBoxConfig;
import iskallia.vault.config.MonolithConfig;
import iskallia.vault.config.MysteryBoxConfig;
import iskallia.vault.config.MysteryEggConfig;
import iskallia.vault.config.MysteryHostileEggConfig;
import iskallia.vault.config.OldNotesConfig;
import iskallia.vault.config.OmegaSoulShardConfig;
import iskallia.vault.config.OverLevelEnchantConfig;
import iskallia.vault.config.PandorasBoxConfig;
import iskallia.vault.config.PaxelConfigs;
import iskallia.vault.config.PlayerResearchTransferConfig;
import iskallia.vault.config.PlayerTitlesConfig;
import iskallia.vault.config.PotionConfig;
import iskallia.vault.config.ResearchConfig;
import iskallia.vault.config.ResearchGroupConfig;
import iskallia.vault.config.ResearchGroupStyleConfig;
import iskallia.vault.config.ResearchesGUIConfig;
import iskallia.vault.config.SandEventConfig;
import iskallia.vault.config.ScavengerConfig;
import iskallia.vault.config.ShopPedestalConfig;
import iskallia.vault.config.SkillAltarConfig;
import iskallia.vault.config.SkillDescriptionsConfig;
import iskallia.vault.config.SkillGatesConfig;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.config.SpiritConfig;
import iskallia.vault.config.StatueLootConfig;
import iskallia.vault.config.StatueRecyclingConfig;
import iskallia.vault.config.TalentsConfig;
import iskallia.vault.config.TalentsGUIConfig;
import iskallia.vault.config.TileGroupsConfig;
import iskallia.vault.config.TooltipConfig;
import iskallia.vault.config.TraderExclusionsConfig;
import iskallia.vault.config.TrinketConfig;
import iskallia.vault.config.UnidentifiedRelicFragmentsConfig;
import iskallia.vault.config.UnidentifiedTreasureKeyConfig;
import iskallia.vault.config.UnknownEggConfig;
import iskallia.vault.config.VaultAltarConfig;
import iskallia.vault.config.VaultBossConfig;
import iskallia.vault.config.VaultCharmConfig;
import iskallia.vault.config.VaultChestConfig;
import iskallia.vault.config.VaultCrystalCatalystConfig;
import iskallia.vault.config.VaultCrystalConfig;
import iskallia.vault.config.VaultDiffuserConfig;
import iskallia.vault.config.VaultEntitiesConfig;
import iskallia.vault.config.VaultGeneralConfig;
import iskallia.vault.config.VaultGuardianConfig;
import iskallia.vault.config.VaultItemsConfig;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.config.VaultLevelsConfig;
import iskallia.vault.config.VaultMetaChestConfig;
import iskallia.vault.config.VaultMobGearConfig;
import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.config.VaultModifierOverlayConfig;
import iskallia.vault.config.VaultModifierPoolsConfig;
import iskallia.vault.config.VaultModifiersConfig;
import iskallia.vault.config.VaultPortalConfig;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.config.VaultRuneConfig;
import iskallia.vault.config.VaultStatsConfig;
import iskallia.vault.config.WildSpawnerConfig;
import iskallia.vault.config.altar.VaultAltarIngredientsConfig;
import iskallia.vault.config.bounty.BountyConfig;
import iskallia.vault.config.bounty.BountyEntitiesConfig;
import iskallia.vault.config.bounty.BountyOresConfig;
import iskallia.vault.config.bounty.RewardConfig;
import iskallia.vault.config.bounty.task.TaskConfig;
import iskallia.vault.config.core.LootPoolsConfig;
import iskallia.vault.config.core.LootTablesConfig;
import iskallia.vault.config.core.PalettesConfig;
import iskallia.vault.config.core.TemplatePoolsConfig;
import iskallia.vault.config.core.TemplatesConfig;
import iskallia.vault.config.core.ThemesConfig;
import iskallia.vault.config.gear.VaultGearCommonConfig;
import iskallia.vault.config.gear.VaultGearCraftingConfig;
import iskallia.vault.config.gear.VaultGearEnchantmentConfig;
import iskallia.vault.config.gear.VaultGearModificationConfig;
import iskallia.vault.config.gear.VaultGearTagConfig;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.config.gear.VaultGearTypeConfig;
import iskallia.vault.config.gear.VaultGearTypePoolConfig;
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.config.quest.QuestConfig;
import iskallia.vault.config.quest.SkyVaultQuestConfig;
import iskallia.vault.config.recipe.GearRecipesConfig;
import iskallia.vault.config.recipe.InscriptionRecipesConfig;
import iskallia.vault.config.recipe.JewelRecipesConfig;
import iskallia.vault.config.recipe.ToolRecipesConfig;
import iskallia.vault.config.recipe.TrinketRecipesConfig;
import iskallia.vault.config.tool.ToolPulverizingConfig;
import iskallia.vault.core.data.key.LootPoolKey;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.loot.LootTableInfo;
import iskallia.vault.core.world.loot.generator.TieredLootTableGenerator;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.processor.tile.ReferenceTileProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.template.data.TemplatePool;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.item.Item;

public class ModConfigs {
   public static final Set<String> INVALID_CONFIGS = new HashSet<>();
   public static final Set<Config> CONFIGS = new HashSet<>();
   private static boolean initialized = false;
   public static ColorsConfig COLORS;
   public static AbilitiesConfig ABILITIES;
   public static AbilitiesGUIConfig ABILITIES_GUI;
   public static AbilitiesDescriptionsConfig ABILITIES_DESCRIPTIONS;
   public static AbilitiesVignetteConfig ABILITIES_VIGNETTE;
   public static AbilitiesVeinMinerDenyConfig ABILITIES_VEIN_MINER_DENY_CONFIG;
   public static AbilitiesDrillDigDenyConfig ABILITIES_DRILL_DIG_DENY_CONFIG;
   public static ArchetypesConfig ARCHETYPES;
   public static ArchetypeGUIConfig ARCHETYPES_GUI;
   public static ArchetypeDescriptionsConfig ARCHETYPE_DESCRIPTIONS;
   public static TalentsConfig TALENTS;
   public static TalentsGUIConfig TALENTS_GUI;
   public static ExpertisesGUIConfig EXPERTISES_GUI;
   public static ExpertisesConfig EXPERTISES;
   public static ResearchConfig RESEARCHES;
   public static ResearchesGUIConfig RESEARCHES_GUI;
   public static ResearchGroupConfig RESEARCH_GROUPS;
   public static ResearchGroupStyleConfig RESEARCH_GROUP_STYLES;
   public static SkillDescriptionsConfig SKILL_DESCRIPTIONS;
   public static SkillGatesConfig SKILL_GATES;
   public static VaultLevelsConfig LEVELS_META;
   public static UnidentifiedRelicFragmentsConfig UNIDENTIFIED_RELIC_FRAGMENTS;
   public static VaultMobsConfig VAULT_MOBS;
   public static VaultMobGearConfig VAULT_MOBS_GEAR;
   public static ChampionsConfig CHAMPIONS;
   public static VaultItemsConfig VAULT_ITEMS;
   public static VaultAltarConfig VAULT_ALTAR;
   public static VaultGeneralConfig VAULT_GENERAL;
   public static VaultPortalConfig VAULT_PORTAL;
   public static LegendaryTreasureNormalConfig LEGENDARY_TREASURE_NORMAL;
   public static LegendaryTreasureRareConfig LEGENDARY_TREASURE_RARE;
   public static LegendaryTreasureEpicConfig LEGENDARY_TREASURE_EPIC;
   public static LegendaryTreasureOmegaConfig LEGENDARY_TREASURE_OMEGA;
   public static StatueLootConfig STATUE_LOOT;
   public static CryoChamberConfig CRYO_CHAMBER;
   public static OverLevelEnchantConfig OVERLEVEL_ENCHANT;
   public static MysteryBoxConfig MYSTERY_BOX;
   public static VaultModifiersConfig VAULT_MODIFIERS;
   public static VaultModifierPoolsConfig VAULT_MODIFIER_POOLS;
   public static VaultCrystalConfig VAULT_CRYSTAL;
   public static PandorasBoxConfig PANDORAS_BOX;
   public static EternalConfig ETERNAL;
   public static VaultChestConfig VAULT_CHEST;
   public static VaultChestConfig VAULT_TREASURE_CHEST;
   public static VaultChestConfig VAULT_ALTAR_CHEST;
   public static VaultChestConfig VAULT_COOP_CHEST;
   public static VaultChestConfig VAULT_BONUS_CHEST;
   public static VaultMetaChestConfig VAULT_CHEST_META;
   public static StatueRecyclingConfig STATUE_RECYCLING;
   public static UnknownEggConfig UNKNOWN_EGG;
   public static LegacyLootTablesConfig LOOT_TABLES;
   public static VaultCrystalCatalystConfig VAULT_CRYSTAL_CATALYST;
   public static SandEventConfig SAND_EVENT;
   public static DurabilityConfig DURABILITY;
   public static ModBoxConfig MOD_BOX;
   public static UnidentifiedTreasureKeyConfig UNIDENTIFIED_TREASURE_KEY;
   public static SoulShardConfig SOUL_SHARD;
   public static OmegaSoulShardConfig OMEGA_SOUL_SHARD;
   public static BlackMarketConfig BLACK_MARKET;
   public static EternalAttributeConfig ETERNAL_ATTRIBUTES;
   public static EternalAuraConfig ETERNAL_AURAS;
   public static VaultCharmConfig VAULT_CHARM;
   public static MysteryEggConfig MYSTERY_EGG;
   public static MysteryHostileEggConfig MYSTERY_HOSTILE_EGG;
   public static VaultRuneConfig VAULT_RUNE;
   public static TooltipConfig TOOLTIP;
   public static OldNotesConfig HINT_PAPER;
   public static EyesoreConfig EYESORE;
   public static FighterConfig FIGHTER;
   public static ShopPedestalConfig SHOP_PEDESTAL;
   public static CatalystInfusionTableConfig CATALYST_INFUSION_TABLE;
   public static GearModelRollRaritiesConfig GEAR_MODEL_ROLL_RARITIES;
   public static LegacyMagnetConfigs MAGNET_CONFIG;
   public static PaxelConfigs PAXEL_CONFIGS;
   public static ManaConfig MANA;
   public static EtchingConfig ETCHING;
   public static TrinketConfig TRINKET;
   public static CharmConfig CHARM;
   public static EliteSpawnerConfig ELITE_SPAWNER;
   public static WildSpawnerConfig WILD_SPAWNER;
   public static CustomEntitySpawnerConfig CUSTOM_ENTITY_SPAWNER;
   public static EternalsNamepoolConfig ETERNALS_NAMEPOOL;
   public static ScavengerConfig SCAVENGER;
   public static MonolithConfig MONOLITH;
   public static DivineParadoxConfig DIVINE_PARADOX;
   public static ElixirConfig ELIXIR;
   public static VaultStatsConfig VAULT_STATS;
   public static VaultRecyclerConfig VAULT_RECYCLER;
   public static VaultDiffuserConfig VAULT_DIFFUSER;
   public static SpiritConfig SPIRIT;
   public static VaultEntitiesConfig VAULT_ENTITIES;
   public static MenuPlayerStatDescriptionConfig MENU_PLAYER_STAT_DESCRIPTIONS;
   public static VaultModifierOverlayConfig VAULT_MODIFIER_OVERLAY;
   public static CrystalBuddingConfig CRYSTAL_BUDDING;
   public static LootInfoConfig LOOT_INFO_CONFIG;
   public static VaultGuardianConfig VAULT_GUARDIAN;
   public static ToolPulverizingConfig TOOL_PULVERIZING;
   public static InscriptionConfig INSCRIPTION;
   public static AugmentConfig AUGMENT;
   public static EntityGroupsConfig ENTITY_GROUPS;
   public static TileGroupsConfig TILE_GROUPS;
   public static ItemGroupsConfig ITEM_GROUPS;
   public static ArchitectConfig ARCHITECT;
   public static BestiaryConfig BESTIARY;
   public static HeraldTrophyConfig HERALD_TROPHY;
   public static VaultGearCommonConfig VAULT_GEAR_COMMON;
   public static Map<Item, VaultGearTierConfig> VAULT_GEAR_CONFIG;
   public static Map<Item, VaultGearWorkbenchConfig> VAULT_GEAR_WORKBENCH_CONFIG;
   public static AlchemyTableConfig VAULT_ALCHEMY_TABLE;
   public static VaultGearTypePoolConfig VAULT_GEAR_TYPE_POOL_CONFIG;
   public static VaultGearTypeConfig VAULT_GEAR_TYPE_CONFIG;
   public static VaultGearTagConfig VAULT_GEAR_TAG_CONFIG;
   public static BountyConfig BOUNTY_CONFIG;
   public static RewardConfig REWARD_CONFIG;
   public static BountyEntitiesConfig BOUNTY_ENTITIES;
   public static BountyOresConfig BOUNTY_ORES;
   public static VaultGearCraftingConfig VAULT_GEAR_CRAFTING_CONFIG;
   public static VaultGearModificationConfig VAULT_GEAR_MODIFICATION_CONFIG;
   public static VaultJewelCuttingConfig VAULT_JEWEL_CUTTING_CONFIG;
   public static VaultGearEnchantmentConfig VAULT_GEAR_ENCHANTMENT_CONFIG;
   public static GearRecipesConfig GEAR_RECIPES;
   public static JewelRecipesConfig JEWEL_RECIPES;
   public static TrinketRecipesConfig TRINKET_RECIPES;
   public static ToolRecipesConfig TOOL_RECIPES;
   public static InscriptionRecipesConfig INSCRIPTION_RECIPES;
   public static VaultAltarIngredientsConfig VAULT_ALTAR_INGREDIENTS;
   public static SkillAltarConfig SKILL_ALTAR;
   public static QuestConfig QUESTS;
   public static SkyVaultQuestConfig SKY_QUESTS;
   public static TraderExclusionsConfig TRADER_EXCLUSIONS;
   public static PlayerResearchTransferConfig PLAYER_RESEARCH_TRANSFER;
   public static PotionConfig POTION;
   public static VaultBossConfig BOSS;
   public static AncientCopperGolemConfig ANCIENT_COPPER_GOLEM;
   public static PlayerTitlesConfig PLAYER_TITLES;
   public static AscensionConfig ASCENSION;
   public static AscensionForgeConfig ASCENSION_FORGE;

   public static void register() {
      INVALID_CONFIGS.clear();
      if (!CONFIGS.isEmpty()) {
         CONFIGS.forEach(Config::onUnload);
         CONFIGS.clear();
      }

      COLORS = new ColorsConfig().readConfig();
      ABILITIES = new AbilitiesConfig().readConfig();
      ABILITIES_GUI = new AbilitiesGUIConfig().readConfig();
      ABILITIES_DESCRIPTIONS = new AbilitiesDescriptionsConfig().readConfig();
      ABILITIES_VIGNETTE = new AbilitiesVignetteConfig().readConfig();
      ABILITIES_VEIN_MINER_DENY_CONFIG = new AbilitiesVeinMinerDenyConfig().readConfig();
      ABILITIES_DRILL_DIG_DENY_CONFIG = new AbilitiesDrillDigDenyConfig().readConfig();
      ARCHETYPES = new ArchetypesConfig().readConfig();
      ARCHETYPES_GUI = new ArchetypeGUIConfig().readConfig();
      ARCHETYPE_DESCRIPTIONS = new ArchetypeDescriptionsConfig().readConfig();
      TALENTS = new TalentsConfig().readConfig();
      TALENTS_GUI = new TalentsGUIConfig().readConfig();
      EXPERTISES_GUI = new ExpertisesGUIConfig().readConfig();
      EXPERTISES = new ExpertisesConfig().readConfig();
      RESEARCHES = new ResearchConfig().readConfig();
      RESEARCHES_GUI = new ResearchesGUIConfig().readConfig();
      RESEARCH_GROUPS = new ResearchGroupConfig().readConfig();
      RESEARCH_GROUP_STYLES = new ResearchGroupStyleConfig().readConfig();
      SKILL_DESCRIPTIONS = new SkillDescriptionsConfig().readConfig();
      SKILL_GATES = new SkillGatesConfig().readConfig();
      LEVELS_META = new VaultLevelsConfig().readConfig();
      UNIDENTIFIED_RELIC_FRAGMENTS = new UnidentifiedRelicFragmentsConfig().readConfig();
      VAULT_MOBS = new VaultMobsConfig().readConfig();
      VAULT_MOBS_GEAR = new VaultMobGearConfig().readConfig();
      CHAMPIONS = new ChampionsConfig().readConfig();
      VAULT_ITEMS = new VaultItemsConfig().readConfig();
      VAULT_ALTAR = new VaultAltarConfig().readConfig();
      VAULT_GENERAL = new VaultGeneralConfig().readConfig();
      VAULT_PORTAL = new VaultPortalConfig().readConfig();
      LEGENDARY_TREASURE_NORMAL = new LegendaryTreasureNormalConfig().readConfig();
      LEGENDARY_TREASURE_RARE = new LegendaryTreasureRareConfig().readConfig();
      LEGENDARY_TREASURE_EPIC = new LegendaryTreasureEpicConfig().readConfig();
      LEGENDARY_TREASURE_OMEGA = new LegendaryTreasureOmegaConfig().readConfig();
      STATUE_LOOT = new StatueLootConfig().readConfig();
      CRYO_CHAMBER = new CryoChamberConfig().readConfig();
      OVERLEVEL_ENCHANT = new OverLevelEnchantConfig().readConfig();
      MYSTERY_BOX = new MysteryBoxConfig().readConfig();
      VAULT_MODIFIERS = new VaultModifiersConfig().readConfig();
      VAULT_MODIFIER_POOLS = new VaultModifierPoolsConfig().readConfig();
      VAULT_CRYSTAL = new VaultCrystalConfig().readConfig();
      PANDORAS_BOX = new PandorasBoxConfig().readConfig();
      ETERNAL = new EternalConfig().readConfig();
      VAULT_CHEST = new VaultChestConfig("vault_chest").readConfig();
      VAULT_TREASURE_CHEST = new VaultChestConfig("vault_treasure_chest").readConfig();
      VAULT_ALTAR_CHEST = new VaultChestConfig("vault_altar_chest").readConfig();
      VAULT_COOP_CHEST = new VaultChestConfig("vault_coop_chest").readConfig();
      VAULT_BONUS_CHEST = new VaultChestConfig("vault_bonus_chest").readConfig();
      VAULT_CHEST_META = new VaultMetaChestConfig().readConfig();
      STATUE_RECYCLING = new StatueRecyclingConfig().readConfig();
      UNKNOWN_EGG = new UnknownEggConfig().readConfig();
      LOOT_TABLES = new LegacyLootTablesConfig().readConfig();
      VAULT_CRYSTAL_CATALYST = new VaultCrystalCatalystConfig().readConfig();
      SAND_EVENT = new SandEventConfig().readConfig();
      DURABILITY = new DurabilityConfig().readConfig();
      MOD_BOX = new ModBoxConfig().readConfig();
      UNIDENTIFIED_TREASURE_KEY = new UnidentifiedTreasureKeyConfig().readConfig();
      SOUL_SHARD = new SoulShardConfig().readConfig();
      OMEGA_SOUL_SHARD = new OmegaSoulShardConfig().readConfig();
      BLACK_MARKET = new BlackMarketConfig().readConfig();
      ETERNAL_ATTRIBUTES = new EternalAttributeConfig().readConfig();
      ETERNAL_AURAS = new EternalAuraConfig().readConfig();
      VAULT_CHARM = new VaultCharmConfig().readConfig();
      MYSTERY_EGG = new MysteryEggConfig().readConfig();
      MYSTERY_HOSTILE_EGG = new MysteryHostileEggConfig().readConfig();
      VAULT_RUNE = new VaultRuneConfig().readConfig();
      TOOLTIP = new TooltipConfig().readConfig();
      HINT_PAPER = new OldNotesConfig().readConfig();
      EYESORE = new EyesoreConfig().readConfig();
      FIGHTER = new FighterConfig().readConfig();
      SHOP_PEDESTAL = new ShopPedestalConfig().readConfig();
      CATALYST_INFUSION_TABLE = new CatalystInfusionTableConfig().readConfig();
      GEAR_MODEL_ROLL_RARITIES = new GearModelRollRaritiesConfig().readConfig();
      MANA = new ManaConfig().readConfig();
      MAGNET_CONFIG = new LegacyMagnetConfigs().readConfig();
      PAXEL_CONFIGS = new PaxelConfigs().readConfig();
      ETCHING = new EtchingConfig().readConfig();
      TRINKET = new TrinketConfig().readConfig();
      CHARM = new CharmConfig().readConfig();
      ELITE_SPAWNER = new EliteSpawnerConfig().readConfig();
      WILD_SPAWNER = new WildSpawnerConfig().readConfig();
      CUSTOM_ENTITY_SPAWNER = new CustomEntitySpawnerConfig().readConfig();
      ETERNALS_NAMEPOOL = new EternalsNamepoolConfig().readConfig();
      SCAVENGER = new ScavengerConfig().readConfig();
      MONOLITH = new MonolithConfig().readConfig();
      DIVINE_PARADOX = new DivineParadoxConfig().readConfig();
      ELIXIR = new ElixirConfig().readConfig();
      VAULT_STATS = new VaultStatsConfig().readConfig();
      VAULT_RECYCLER = new VaultRecyclerConfig().readConfig();
      VAULT_DIFFUSER = new VaultDiffuserConfig().readConfig();
      SPIRIT = new SpiritConfig().readConfig();
      SKILL_ALTAR = new SkillAltarConfig().readConfig();
      VAULT_ENTITIES = new VaultEntitiesConfig().readConfig();
      MENU_PLAYER_STAT_DESCRIPTIONS = new MenuPlayerStatDescriptionConfig().readConfig();
      VAULT_MODIFIER_OVERLAY = new VaultModifierOverlayConfig().readConfig();
      CRYSTAL_BUDDING = new CrystalBuddingConfig().readConfig();
      LOOT_INFO_CONFIG = new LootInfoConfig().readConfig();
      VAULT_GUARDIAN = new VaultGuardianConfig().readConfig();
      INSCRIPTION = new InscriptionConfig().readConfig();
      AUGMENT = new AugmentConfig().readConfig();
      ENTITY_GROUPS = new EntityGroupsConfig().readConfig();
      TILE_GROUPS = new TileGroupsConfig().readConfig();
      ITEM_GROUPS = new ItemGroupsConfig().readConfig();
      ARCHITECT = new ArchitectConfig().readConfig();
      QUESTS = new QuestConfig().readConfig();
      SKY_QUESTS = new SkyVaultQuestConfig().readConfig();
      POTION = new PotionConfig().readConfig();
      BOSS = new VaultBossConfig().readConfig();
      BESTIARY = new BestiaryConfig().readConfig();
      HERALD_TROPHY = new HeraldTrophyConfig().readConfig();
      ANCIENT_COPPER_GOLEM = new AncientCopperGolemConfig().readConfig();
      PLAYER_TITLES = new PlayerTitlesConfig().readConfig();
      ASCENSION = new AscensionConfig().readConfig();
      ASCENSION_FORGE = new AscensionForgeConfig().readConfig();
      TOOL_PULVERIZING = new ToolPulverizingConfig().readConfig();
      VAULT_GEAR_COMMON = new VaultGearCommonConfig().readConfig();
      VAULT_GEAR_CONFIG = VaultGearTierConfig.registerConfigs();
      VAULT_GEAR_WORKBENCH_CONFIG = VaultGearWorkbenchConfig.registerConfigs();
      VAULT_ALCHEMY_TABLE = new AlchemyTableConfig().readConfig();
      VAULT_GEAR_TYPE_POOL_CONFIG = new VaultGearTypePoolConfig().readConfig();
      VAULT_GEAR_TYPE_CONFIG = new VaultGearTypeConfig().readConfig();
      VAULT_GEAR_TAG_CONFIG = new VaultGearTagConfig().readConfig();
      VAULT_GEAR_CRAFTING_CONFIG = new VaultGearCraftingConfig().readConfig();
      VAULT_GEAR_MODIFICATION_CONFIG = new VaultGearModificationConfig().readConfig();
      VAULT_GEAR_ENCHANTMENT_CONFIG = new VaultGearEnchantmentConfig().readConfig();
      VAULT_JEWEL_CUTTING_CONFIG = new VaultJewelCuttingConfig().readConfig();
      VAULT_ALTAR_INGREDIENTS = new VaultAltarIngredientsConfig().readConfig();
      GEAR_RECIPES = new GearRecipesConfig().readConfig();
      JEWEL_RECIPES = new JewelRecipesConfig().readConfig();
      TRINKET_RECIPES = new TrinketRecipesConfig().readConfig();
      TOOL_RECIPES = new ToolRecipesConfig().readConfig();
      INSCRIPTION_RECIPES = new InscriptionRecipesConfig().readConfig();
      TRADER_EXCLUSIONS = new TraderExclusionsConfig().readConfig();
      PLAYER_RESEARCH_TRANSFER = new PlayerResearchTransferConfig().readConfig();
      registerBountyConfigs();
      VaultMod.LOGGER.info("Vault Configs are loaded successfully!");
   }

   public static void registerGen() {
      VaultRegistry.TEMPLATE = new TemplatesConfig().<TemplatesConfig>readConfig().toRegistry();
      VaultRegistry.PALETTE = new PalettesConfig().<PalettesConfig>readConfig().toRegistry();
      VaultRegistry.TEMPLATE_POOL = new TemplatePoolsConfig().<TemplatePoolsConfig>readConfig().toRegistry();
      VaultRegistry.THEME = new ThemesConfig().<ThemesConfig>readConfig().toRegistry();
      VaultRegistry.LOOT_POOL = new LootPoolsConfig().<LootPoolsConfig>readConfig().toRegistry();
      VaultRegistry.LOOT_TABLE = new LootTablesConfig().<LootTablesConfig>readConfig().toRegistry();

      for (PaletteKey key : VaultRegistry.PALETTE.getKeys()) {
         for (Palette palette : key.getMap().values()) {
            for (TileProcessor processor : palette.getTileProcessors()) {
               if (processor instanceof ReferenceTileProcessor) {
                  ReferenceTileProcessor reference = (ReferenceTileProcessor)processor;
                  if (VaultRegistry.PALETTE.getKey(reference.getId()) == null) {
                     VaultMod.LOGGER.error("Palette " + key.getId() + " has processor with invalid reference " + reference.getId());
                  }
               }
            }
         }
      }

      for (TemplatePoolKey key : VaultRegistry.TEMPLATE_POOL.getKeys()) {
         for (TemplatePool pool : key.getMap().values()) {
            pool.iterate(entry -> {
               if (!entry.validate()) {
                  VaultMod.LOGGER.error("Template pool " + key.getId() + " contains invalid entry " + entry);
               }

               return true;
            });
         }
      }

      for (LootPoolKey key : VaultRegistry.LOOT_POOL.getKeys()) {
         for (LootPool pool : key.getMap().values()) {
            pool.iterate(entry -> {
               if (!entry.validate()) {
                  VaultMod.LOGGER.error("Loot pool " + key.getId() + " contains invalid entry " + entry);
               }

               return true;
            });
         }
      }

      TieredLootTableGenerator.clearCache();
      LootTableInfo.clear();

      for (LootTableKey key : VaultRegistry.LOOT_TABLE.getKeys()) {
         for (LootTable table : key.getMap().values()) {
            if (TieredLootTableGenerator.supports(table)) {
               TieredLootTableGenerator.addCache(table);
            }
         }

         LootTableInfo.cache(key);
      }

      VaultMod.LOGGER.info("Vault Core Configs have finished loading!");
      initialized = true;
   }

   private static void registerBountyConfigs() {
      TaskConfig.registerTaskConfigs();
      BOUNTY_CONFIG = new BountyConfig().readConfig();
      REWARD_CONFIG = new RewardConfig().readConfig();
      BOUNTY_ENTITIES = new BountyEntitiesConfig().readConfig();
      BOUNTY_ORES = new BountyOresConfig().readConfig();
   }

   public static boolean isInitialized() {
      return initialized;
   }
}
