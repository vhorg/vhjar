package iskallia.vault.util;

import com.google.common.collect.Lists;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.server.ServerLifecycleHooks;

public class SidedHelper {
   public static int getVaultLevel(Player player) {
      return player instanceof ServerPlayer
         ? PlayerVaultStatsData.get(((ServerPlayer)player).getLevel()).getVaultStats(player).getVaultLevel()
         : getClientVaultLevel();
   }

   @OnlyIn(Dist.CLIENT)
   private static int getClientVaultLevel() {
      return VaultBarOverlay.vaultLevel;
   }

   public static List<Player> getSidedPlayers() {
      if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
         MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
         return srv.getPlayerList().getPlayers();
      } else {
         return getClientSidePlayers();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static List<Player> getClientSidePlayers() {
      return Lists.newArrayList(new Player[]{Minecraft.getInstance().player});
   }
}
