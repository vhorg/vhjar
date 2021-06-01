package iskallia.vault.init;

import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.client.gui.overlay.AbilityVignetteOverlay;
import iskallia.vault.client.gui.overlay.GiftBombOverlay;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.overlay.VaultRaidOverlay;
import iskallia.vault.client.gui.screen.AdvancedVendingMachineScreen;
import iskallia.vault.client.gui.screen.GlobalTraderScreen;
import iskallia.vault.client.gui.screen.KeyPressScreen;
import iskallia.vault.client.gui.screen.RenameScreen;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.screen.VaultCrateScreen;
import iskallia.vault.client.gui.screen.VendingMachineScreen;
import net.minecraft.client.gui.ScreenManager;
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
      ScreenManager.func_216911_a(ModContainers.TRADER_CONTAINER, GlobalTraderScreen::new);
   }

   public static void registerOverlays() {
      MinecraftForge.EVENT_BUS.register(VaultBarOverlay.class);
      MinecraftForge.EVENT_BUS.register(AbilitiesOverlay.class);
      MinecraftForge.EVENT_BUS.register(AbilityVignetteOverlay.class);
      MinecraftForge.EVENT_BUS.register(VaultRaidOverlay.class);
      MinecraftForge.EVENT_BUS.register(GiftBombOverlay.class);
   }
}
