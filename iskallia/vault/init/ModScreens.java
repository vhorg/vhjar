package iskallia.vault.init;

import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.client.gui.overlay.ArenaScoreboardOverlay;
import iskallia.vault.client.gui.overlay.CheerOverlay;
import iskallia.vault.client.gui.overlay.GiftBombOverlay;
import iskallia.vault.client.gui.overlay.PlayerArmorOverlay;
import iskallia.vault.client.gui.overlay.PlayerDamageOverlay;
import iskallia.vault.client.gui.overlay.PlayerRageOverlay;
import iskallia.vault.client.gui.overlay.SandEventOverlay;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.overlay.VaultGoalBossBarOverlay;
import iskallia.vault.client.gui.overlay.VaultPartyOverlay;
import iskallia.vault.client.gui.overlay.VignetteOverlay;
import iskallia.vault.client.gui.overlay.goal.AncientGoalOverlay;
import iskallia.vault.client.gui.overlay.goal.CakeHuntOverlay;
import iskallia.vault.client.gui.overlay.goal.ObeliskGoalOverlay;
import iskallia.vault.client.gui.screen.CatalystInfusionTableScreen;
import iskallia.vault.client.gui.screen.CryochamberScreen;
import iskallia.vault.client.gui.screen.EtchingTradeScreen;
import iskallia.vault.client.gui.screen.KeyPressScreen;
import iskallia.vault.client.gui.screen.LootStatueScreen;
import iskallia.vault.client.gui.screen.MagnetTableScreen;
import iskallia.vault.client.gui.screen.RenameScreen;
import iskallia.vault.client.gui.screen.ShardPouchScreen;
import iskallia.vault.client.gui.screen.ShardTradeScreen;
import iskallia.vault.client.gui.screen.ToolViseScreen;
import iskallia.vault.client.gui.screen.VaultCharmControllerScreen;
import iskallia.vault.client.gui.screen.VaultCrateScreen;
import iskallia.vault.client.gui.screen.block.RelicPedestalScreen;
import iskallia.vault.client.gui.screen.block.SpiritExtractorScreen;
import iskallia.vault.client.gui.screen.block.TransmogTableScreen;
import iskallia.vault.client.gui.screen.block.VaultArtisanStationScreen;
import iskallia.vault.client.gui.screen.block.VaultForgeScreen;
import iskallia.vault.client.gui.screen.block.VaultRecyclerScreen;
import iskallia.vault.client.gui.screen.bounty.BountyScreen;
import iskallia.vault.client.gui.screen.player.AbilitiesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.ArchetypesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.ResearchesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.StatisticsElementContainerScreen;
import iskallia.vault.client.gui.screen.player.TalentsElementContainerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(Dist.CLIENT)
public class ModScreens {
   public static void register() {
      MenuScreens.register(ModContainers.STATISTICS_TAB_CONTAINER, StatisticsElementContainerScreen::new);
      MenuScreens.register(ModContainers.ABILITY_TAB_CONTAINER, AbilitiesElementContainerScreen::new);
      MenuScreens.register(ModContainers.TALENT_TAB_CONTAINER, TalentsElementContainerScreen::new);
      MenuScreens.register(ModContainers.ARCHETYPE_TAB_CONTAINER, ArchetypesElementContainerScreen::new);
      MenuScreens.register(ModContainers.RESEARCH_TAB_CONTAINER, ResearchesElementContainerScreen::new);
      MenuScreens.register(ModContainers.VAULT_CRATE_CONTAINER, VaultCrateScreen::new);
      MenuScreens.register(ModContainers.RENAMING_CONTAINER, RenameScreen::new);
      MenuScreens.register(ModContainers.KEY_PRESS_CONTAINER, KeyPressScreen::new);
      MenuScreens.register(ModContainers.LOOT_STATUE_CONTAINER, LootStatueScreen::new);
      MenuScreens.register(ModContainers.TRANSMOG_TABLE_CONTAINER, TransmogTableScreen::new);
      MenuScreens.register(ModContainers.SCAVENGER_CHEST_CONTAINER, ContainerScreen::new);
      MenuScreens.register(ModContainers.CATALYST_INFUSION_TABLE_CONTAINER, CatalystInfusionTableScreen::new);
      MenuScreens.register(ModContainers.SHARD_POUCH_CONTAINER, ShardPouchScreen::new);
      MenuScreens.register(ModContainers.SHARD_TRADE_CONTAINER, ShardTradeScreen::new);
      MenuScreens.register(ModContainers.CRYOCHAMBER_CONTAINER, CryochamberScreen::new);
      MenuScreens.register(ModContainers.ETCHING_TRADE_CONTAINER, EtchingTradeScreen::new);
      MenuScreens.register(ModContainers.VAULT_CHARM_CONTROLLER_CONTAINER, VaultCharmControllerScreen::new);
      MenuScreens.register(ModContainers.TOOL_VISE_CONTAINER, ToolViseScreen::new);
      MenuScreens.register(ModContainers.MAGNET_TABLE_CONTAINER, MagnetTableScreen::new);
      MenuScreens.register(ModContainers.VAULT_FORGE_CONTAINER, VaultForgeScreen::new);
      MenuScreens.register(ModContainers.VAULT_ARTISAN_STATION_CONTAINER, VaultArtisanStationScreen::new);
      MenuScreens.register(ModContainers.VAULT_RECYCLER_CONTAINER, VaultRecyclerScreen::new);
      MenuScreens.register(ModContainers.RELIC_PEDESTAL_CONTAINER, RelicPedestalScreen::new);
      MenuScreens.register(ModContainers.SPIRIT_EXTRACTOR_CONTAINER, SpiritExtractorScreen::new);
      MenuScreens.register(ModContainers.BOUNTY_CONTAINER, BountyScreen::new);
   }

   public static void registerOverlayEvents() {
      MinecraftForge.EVENT_BUS.register(ArenaScoreboardOverlay.class);
      MinecraftForge.EVENT_BUS.register(CheerOverlay.class);
      MinecraftForge.EVENT_BUS.register(GiftBombOverlay.class);
      MinecraftForge.EVENT_BUS.register(SandEventOverlay.class);
      MinecraftForge.EVENT_BUS.register(VaultGoalBossBarOverlay.class);
      MinecraftForge.EVENT_BUS.register(ObeliskGoalOverlay.class);
   }

   public static void registerOverlays() {
      registerTop(new VignetteOverlay());
      registerAbove(new PlayerDamageOverlay(), ForgeIngameGui.PLAYER_HEALTH_ELEMENT);
      registerAbove(new PlayerRageOverlay(), ForgeIngameGui.EXPERIENCE_BAR_ELEMENT);
      registerAbove(new PlayerArmorOverlay(), ForgeIngameGui.ARMOR_LEVEL_ELEMENT);
      OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT, false);
      registerBottom(new AbilitiesOverlay());
      registerBottom(new VaultBarOverlay());
      registerBottom(new VaultPartyOverlay());
      registerBottom(new AncientGoalOverlay());
      registerBottom(new CakeHuntOverlay());
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
