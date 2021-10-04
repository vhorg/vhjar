package iskallia.vault.init;

import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.client.gui.overlay.AbilityVignetteOverlay;
import iskallia.vault.client.gui.overlay.PlayerArmorOverlay;
import iskallia.vault.client.gui.overlay.PlayerRageOverlay;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.overlay.VaultGoalBossBarOverlay;
import iskallia.vault.client.gui.overlay.VaultPartyOverlay;
import iskallia.vault.client.gui.overlay.VaultRaidOverlay;
import iskallia.vault.client.gui.overlay.goal.ObeliskGoalOverlay;
import iskallia.vault.client.gui.screen.AdvancedVendingMachineScreen;
import iskallia.vault.client.gui.screen.CatalystDecryptionScreen;
import iskallia.vault.client.gui.screen.CryochamberScreen;
import iskallia.vault.client.gui.screen.GlobalDifficultyScreen;
import iskallia.vault.client.gui.screen.KeyPressScreen;
import iskallia.vault.client.gui.screen.OmegaStatueScreen;
import iskallia.vault.client.gui.screen.RenameScreen;
import iskallia.vault.client.gui.screen.ShardPouchScreen;
import iskallia.vault.client.gui.screen.ShardTradeScreen;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.screen.TransmogTableScreen;
import iskallia.vault.client.gui.screen.VaultCrateScreen;
import iskallia.vault.client.gui.screen.VendingMachineScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ModScreens {
   public static void register(FMLClientSetupEvent event) {
      ScreenManager.func_216911_a(ModContainers.SKILL_TREE_CONTAINER, SkillTreeScreen::new);
      ScreenManager.func_216911_a(ModContainers.VAULT_CRATE_CONTAINER, VaultCrateScreen::new);
      ScreenManager.func_216911_a(ModContainers.VENDING_MACHINE_CONTAINER, VendingMachineScreen::new);
      ScreenManager.func_216911_a(ModContainers.ADVANCED_VENDING_MACHINE_CONTAINER, AdvancedVendingMachineScreen::new);
      ScreenManager.func_216911_a(ModContainers.RENAMING_CONTAINER, RenameScreen::new);
      ScreenManager.func_216911_a(ModContainers.KEY_PRESS_CONTAINER, KeyPressScreen::new);
      ScreenManager.func_216911_a(ModContainers.OMEGA_STATUE_CONTAINER, OmegaStatueScreen::new);
      ScreenManager.func_216911_a(ModContainers.TRANSMOG_TABLE_CONTAINER, TransmogTableScreen::new);
      ScreenManager.func_216911_a(ModContainers.SCAVENGER_CHEST_CONTAINER, ChestScreen::new);
      ScreenManager.func_216911_a(ModContainers.CATALYST_DECRYPTION_CONTAINER, CatalystDecryptionScreen::new);
      ScreenManager.func_216911_a(ModContainers.SHARD_POUCH_CONTAINER, ShardPouchScreen::new);
      ScreenManager.func_216911_a(ModContainers.SHARD_TRADE_CONTAINER, ShardTradeScreen::new);
      ScreenManager.func_216911_a(ModContainers.CRYOCHAMBER_CONTAINER, CryochamberScreen::new);
      ScreenManager.func_216911_a(ModContainers.GLOBAL_DIFFICULTY_CONTAINER, GlobalDifficultyScreen::new);
   }

   public static void registerOverlays() {
      MinecraftForge.EVENT_BUS.register(VaultBarOverlay.class);
      MinecraftForge.EVENT_BUS.register(AbilitiesOverlay.class);
      MinecraftForge.EVENT_BUS.register(AbilityVignetteOverlay.class);
      MinecraftForge.EVENT_BUS.register(VaultRaidOverlay.class);
      MinecraftForge.EVENT_BUS.register(VaultPartyOverlay.class);
      MinecraftForge.EVENT_BUS.register(PlayerRageOverlay.class);
      MinecraftForge.EVENT_BUS.register(PlayerArmorOverlay.class);
      MinecraftForge.EVENT_BUS.register(VaultGoalBossBarOverlay.class);
      MinecraftForge.EVENT_BUS.register(ObeliskGoalOverlay.class);
   }
}
