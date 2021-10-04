package iskallia.vault.client;

import iskallia.vault.Vault;
import iskallia.vault.client.vault.VaultMusicHandler;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.network.message.BossMusicMessage;
import iskallia.vault.network.message.VaultModifierMessage;
import iskallia.vault.network.message.VaultOverlayMessage;
import iskallia.vault.world.vault.modifier.VaultModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ClientVaultRaidData {
   private static int remainingTicks = 0;
   private static boolean canGetRecordTime = false;
   private static VaultOverlayMessage.OverlayType type = VaultOverlayMessage.OverlayType.NONE;
   private static VaultModifiers modifiers = new VaultModifiers();
   private static boolean inBossFight = false;

   public static int getRemainingTicks() {
      return remainingTicks;
   }

   public static boolean canGetRecordTime() {
      return canGetRecordTime;
   }

   public static VaultOverlayMessage.OverlayType getOverlayType() {
      return type;
   }

   public static VaultModifiers getModifiers() {
      return modifiers;
   }

   public static boolean isInBossFight() {
      return inBossFight;
   }

   @SubscribeEvent
   public static void onDisconnect(LoggedOutEvent event) {
      inBossFight = false;
      VaultMusicHandler.stopBossLoop();
      type = VaultOverlayMessage.OverlayType.NONE;
      VaultGoalData.CURRENT_DATA = null;
   }

   @SubscribeEvent
   public static void onTick(ClientTickEvent event) {
      if (event.phase != Phase.END) {
         World clientWorld = Minecraft.func_71410_x().field_71441_e;
         if (clientWorld != null && clientWorld.func_234923_W_() != Vault.VAULT_KEY) {
            type = VaultOverlayMessage.OverlayType.NONE;
            modifiers = new VaultModifiers();
            inBossFight = false;
            VaultMusicHandler.stopBossLoop();
            VaultGoalData.CURRENT_DATA = null;
         }
      }
   }

   public static void receiveBossUpdate(BossMusicMessage bossMessage) {
      inBossFight = bossMessage.isInFight();
   }

   public static void receiveOverlayUpdate(VaultOverlayMessage overlayMessage) {
      remainingTicks = overlayMessage.getRemainingTicks();
      canGetRecordTime = overlayMessage.canGetRecordTime();
      type = overlayMessage.getOverlayType();
   }

   public static void receiveModifierUpdate(VaultModifierMessage message) {
      modifiers = new VaultModifiers();
      message.getGlobalModifiers().forEach(modifier -> modifiers.addTemporaryModifier(modifier, 0));
      message.getPlayerModifiers().forEach(modifier -> modifiers.addTemporaryModifier(modifier, 0));
   }
}
