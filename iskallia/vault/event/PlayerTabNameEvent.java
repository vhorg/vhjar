package iskallia.vault.event;

import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.TabListNameFormat;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerTabNameEvent {
   private static final Set<UUID> IN_VAULT = new HashSet<>();

   @SubscribeEvent
   public static void onTabListNameFormat(TabListNameFormat event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         int vaultLevel = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player).getVaultLevel();
         MutableComponent display = new TextComponent("");
         MutableComponent level = new TextComponent(String.valueOf(vaultLevel)).withStyle(ChatFormatting.YELLOW);
         MutableComponent space = new TextComponent(" ");
         MutableComponent playerName = player.getName().copy();
         display.append(level).append(space).append(playerName);
         if (IN_VAULT.contains(player.getUUID())) {
            display.append(new TextComponent(" (Vault)").withStyle(ChatFormatting.DARK_GRAY));
         } else {
            display.append(new TextComponent(""));
         }

         event.setDisplayName(display);
      }
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      if (event.player instanceof ServerPlayer serverPlayer) {
         if (event.phase != Phase.END && serverPlayer.server.overworld().getGameTime() % 100L != 0L) {
            return;
         }

         boolean updated;
         if (MiscUtils.getVault(serverPlayer).isPresent()) {
            updated = IN_VAULT.add(serverPlayer.getUUID());
         } else {
            updated = IN_VAULT.remove(serverPlayer.getUUID());
         }

         if (updated) {
            serverPlayer.refreshTabListName();
         }
      }
   }
}
