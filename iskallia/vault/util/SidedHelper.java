package iskallia.vault.util;

import com.google.common.collect.Lists;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

public class SidedHelper {
   public static int getVaultLevel(PlayerEntity player) {
      return player instanceof ServerPlayerEntity
         ? PlayerVaultStatsData.get(((ServerPlayerEntity)player).func_71121_q()).getVaultStats(player).getVaultLevel()
         : getClientVaultLevel();
   }

   @OnlyIn(Dist.CLIENT)
   private static int getClientVaultLevel() {
      return VaultBarOverlay.vaultLevel;
   }

   public static List<PlayerEntity> getSidedPlayers() {
      if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
         MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
         return srv.func_184103_al().func_181057_v();
      } else {
         return getClientSidePlayers();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static List<PlayerEntity> getClientSidePlayers() {
      return Lists.newArrayList(new PlayerEntity[]{Minecraft.func_71410_x().field_71439_g});
   }
}
