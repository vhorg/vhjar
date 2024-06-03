package iskallia.vault.init;

import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.client.gui.overlay.BonkOverlay;
import iskallia.vault.client.gui.overlay.BountyProgressOverlay;
import iskallia.vault.client.gui.overlay.GodAltarOverlay;
import iskallia.vault.client.gui.overlay.HarmfulPotionOverlay;
import iskallia.vault.client.gui.overlay.PlayerArmorOverlay;
import iskallia.vault.client.gui.overlay.PlayerDamageOverlay;
import iskallia.vault.client.gui.overlay.PlayerRageOverlay;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.overlay.VaultPartyOverlay;
import iskallia.vault.client.gui.overlay.VignetteOverlay;
import iskallia.vault.client.gui.screen.CatalystInfusionTableScreen;
import iskallia.vault.client.gui.screen.CryochamberScreen;
import iskallia.vault.client.gui.screen.EtchingTradeScreen;
import iskallia.vault.client.gui.screen.LootStatueScreen;
import iskallia.vault.client.gui.screen.MagnetTableScreen;
import iskallia.vault.client.gui.screen.RenameScreen;
import iskallia.vault.client.gui.screen.ShardPouchScreen;
import iskallia.vault.client.gui.screen.ShardTradeScreen;
import iskallia.vault.client.gui.screen.ToolViseScreen;
import iskallia.vault.client.gui.screen.VaultCharmControllerScreen;
import iskallia.vault.client.gui.screen.VaultCrateScreen;
import iskallia.vault.client.gui.screen.block.AlchemyArchiveScreen;
import iskallia.vault.client.gui.screen.block.AlchemyTableScreen;
import iskallia.vault.client.gui.screen.block.AscensionForgeScreen;
import iskallia.vault.client.gui.screen.block.CrystalWorkbenchScreen;
import iskallia.vault.client.gui.screen.block.InscriptionTableScreen;
import iskallia.vault.client.gui.screen.block.ModifierDiscoveryScreen;
import iskallia.vault.client.gui.screen.block.ModifierWorkbenchScreen;
import iskallia.vault.client.gui.screen.block.RelicPedestalScreen;
import iskallia.vault.client.gui.screen.block.SkillAltarScreen;
import iskallia.vault.client.gui.screen.block.SpiritExtractorScreen;
import iskallia.vault.client.gui.screen.block.ToolStationScreen;
import iskallia.vault.client.gui.screen.block.TransmogTableScreen;
import iskallia.vault.client.gui.screen.block.VaultArtisanStationScreen;
import iskallia.vault.client.gui.screen.block.VaultDiffuserScreen;
import iskallia.vault.client.gui.screen.block.VaultDiffuserUpgradedScreen;
import iskallia.vault.client.gui.screen.block.VaultEnchanterScreen;
import iskallia.vault.client.gui.screen.block.VaultEnhancementAltarScreen;
import iskallia.vault.client.gui.screen.block.VaultForgeScreen;
import iskallia.vault.client.gui.screen.block.VaultJewelApplicationStationScreen;
import iskallia.vault.client.gui.screen.block.VaultJewelCuttingStationScreen;
import iskallia.vault.client.gui.screen.block.VaultRecyclerScreen;
import iskallia.vault.client.gui.screen.block.WardrobeScreen;
import iskallia.vault.client.gui.screen.bounty.BountyScreen;
import iskallia.vault.client.gui.screen.player.AbilitiesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.ArchetypesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.ExpertisesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.ResearchesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.StatisticsElementContainerScreen;
import iskallia.vault.client.gui.screen.player.TalentsElementContainerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

@OnlyIn(Dist.CLIENT)
public class ModScreens {
   public static void register() {
      MenuScreens.register(ModContainers.STATISTICS_TAB_CONTAINER, StatisticsElementContainerScreen::new);
      MenuScreens.register(ModContainers.ABILITY_TAB_CONTAINER, AbilitiesElementContainerScreen::new);
      MenuScreens.register(ModContainers.TALENT_TAB_CONTAINER, TalentsElementContainerScreen::new);
      MenuScreens.register(ModContainers.EXPERTISE_TAB_CONTAINER, ExpertisesElementContainerScreen::new);
      MenuScreens.register(ModContainers.ARCHETYPE_TAB_CONTAINER, ArchetypesElementContainerScreen::new);
      MenuScreens.register(ModContainers.RESEARCH_TAB_CONTAINER, ResearchesElementContainerScreen::new);
      MenuScreens.register(ModContainers.VAULT_CRATE_CONTAINER, VaultCrateScreen::new);
      MenuScreens.register(ModContainers.RENAMING_CONTAINER, RenameScreen::new);
      MenuScreens.register(ModContainers.LOOT_STATUE_CONTAINER, LootStatueScreen::new);
      MenuScreens.register(ModContainers.TRANSMOG_TABLE_CONTAINER, TransmogTableScreen::new);
      MenuScreens.register(ModContainers.CATALYST_INFUSION_TABLE_CONTAINER, CatalystInfusionTableScreen::new);
      MenuScreens.register(ModContainers.SHARD_POUCH_CONTAINER, ShardPouchScreen::new);
      MenuScreens.register(ModContainers.SHARD_TRADE_CONTAINER, ShardTradeScreen::new);
      MenuScreens.register(ModContainers.CRYOCHAMBER_CONTAINER, CryochamberScreen::new);
      MenuScreens.register(ModContainers.ETCHING_TRADE_CONTAINER, EtchingTradeScreen::new);
      MenuScreens.register(ModContainers.VAULT_CHARM_CONTROLLER_CONTAINER, VaultCharmControllerScreen::new);
      MenuScreens.register(ModContainers.TOOL_VISE_CONTAINER, ToolViseScreen::new);
      MenuScreens.register(ModContainers.MAGNET_TABLE_CONTAINER, MagnetTableScreen::new);
      MenuScreens.register(ModContainers.VAULT_FORGE_CONTAINER, VaultForgeScreen::new);
      MenuScreens.register(ModContainers.TOOL_STATION_CONTAINER, ToolStationScreen::new);
      MenuScreens.register(ModContainers.INSCRIPTION_TABLE_CONTAINER, InscriptionTableScreen::new);
      MenuScreens.register(ModContainers.VAULT_ARTISAN_STATION_CONTAINER, VaultArtisanStationScreen::new);
      MenuScreens.register(ModContainers.VAULT_JEWEL_CUTTING_STATION_CONTAINER, VaultJewelCuttingStationScreen::new);
      MenuScreens.register(ModContainers.VAULT_JEWEL_APPLICATION_STATION_CONTAINER, VaultJewelApplicationStationScreen::new);
      MenuScreens.register(ModContainers.CRYSTAL_MODIFICATION_STATION_CONTAINER, CrystalWorkbenchScreen::new);
      MenuScreens.register(ModContainers.VAULT_RECYCLER_CONTAINER, VaultRecyclerScreen::new);
      MenuScreens.register(ModContainers.VAULT_DIFFUSER_CONTAINER, VaultDiffuserScreen::new);
      MenuScreens.register(ModContainers.VAULT_HARVESTER_CONTAINER, VaultDiffuserUpgradedScreen::new);
      MenuScreens.register(ModContainers.RELIC_PEDESTAL_CONTAINER, RelicPedestalScreen::new);
      MenuScreens.register(ModContainers.SPIRIT_EXTRACTOR_CONTAINER, SpiritExtractorScreen::new);
      MenuScreens.register(ModContainers.WARDROBE_GEAR_CONTAINER, WardrobeScreen.Gear::new);
      MenuScreens.register(ModContainers.WARDROBE_HOTBAR_CONTAINER, WardrobeScreen.Hotbar::new);
      MenuScreens.register(ModContainers.BOUNTY_CONTAINER, BountyScreen::new);
      MenuScreens.register(ModContainers.ENHANCEMENT_ALTAR_CONTAINER, VaultEnhancementAltarScreen::new);
      MenuScreens.register(ModContainers.MODIFIER_WORKBENCH_CONTAINER, ModifierWorkbenchScreen::new);
      MenuScreens.register(ModContainers.ALCHEMY_TABLE_CONTAINER, AlchemyTableScreen::new);
      MenuScreens.register(ModContainers.ALCHEMY_ARCHIVE_CONTAINER, AlchemyArchiveScreen::new);
      MenuScreens.register(ModContainers.MODIFIER_DISCOVERY_CONTAINER, ModifierDiscoveryScreen::new);
      MenuScreens.register(ModContainers.VAULT_ENCHANTER_CONTAINER, VaultEnchanterScreen::new);
      MenuScreens.register(ModContainers.SKILL_ALTAR_CONTAINER, SkillAltarScreen.Default::new);
      MenuScreens.register(ModContainers.SKILL_ALTAR_IMPORT_CONTAINER, SkillAltarScreen.Import::new);
      MenuScreens.register(ModContainers.ASCENSION_FORGE_CONTAINER, AscensionForgeScreen::new);
   }

   public static void registerOverlayEvents() {
   }

   public static void registerOverlays() {
      registerTop(new VignetteOverlay());
      registerTop(new HarmfulPotionOverlay());
      registerAbove(new PlayerDamageOverlay(), ForgeIngameGui.ARMOR_LEVEL_ELEMENT);
      registerAbove(new PlayerRageOverlay(), ForgeIngameGui.EXPERIENCE_BAR_ELEMENT);
      registerAbove(new PlayerArmorOverlay(), ForgeIngameGui.ARMOR_LEVEL_ELEMENT);
      OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT, false);
      registerBottom(new AbilitiesOverlay());
      registerBottom(new BonkOverlay());
      registerBottom(new VaultBarOverlay());
      registerBottom(new VaultPartyOverlay());
      registerBottom(new BountyProgressOverlay());
      registerBottom(new GodAltarOverlay());
   }

   private static void registerTop(IIngameOverlay overlay) {
      OverlayRegistry.registerOverlayTop(overlay.getClass().getSimpleName(), overlay);
   }

   private static void registerBottom(IIngameOverlay overlay) {
      OverlayRegistry.registerOverlayBottom(overlay.getClass().getSimpleName(), overlay);
   }

   private static void registerAbove(IIngameOverlay overlay, IIngameOverlay other) {
      OverlayRegistry.registerOverlayAbove(other, overlay.getClass().getSimpleName(), overlay);
   }
}
