package iskallia.vault.event;

import iskallia.vault.event.event.VaultJoinForgeEvent;
import iskallia.vault.event.event.VaultLeaveForgeEvent;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.TabListNameFormat;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerTabNameEvent {
   @SubscribeEvent
   public static void onTabListNameFormat(TabListNameFormat event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         int vaultLevel = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player).getVaultLevel();
         MutableComponent display = new TextComponent("");
         MutableComponent level = new TextComponent(String.valueOf(vaultLevel)).withStyle(ChatFormatting.YELLOW);
         MutableComponent space = new TextComponent(" ");
         MutableComponent playerName = player.getDisplayName().copy();
         display.append(level).append(space).append(playerName);
         if (ServerVaults.isInVault(player)) {
            display.append(new TextComponent(" (Vault)").withStyle(ChatFormatting.DARK_GRAY));
         }

         event.setDisplayName(display);
      }
   }

   @SubscribeEvent
   public static void onJoinVault(VaultJoinForgeEvent event) {
      event.getPlayers().forEach(ServerPlayer::refreshTabListName);
   }

   @SubscribeEvent
   public static void onLeaveVault(VaultLeaveForgeEvent event) {
      event.getPlayer().refreshTabListName();
   }
}
