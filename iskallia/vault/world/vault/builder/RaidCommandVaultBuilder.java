package iskallia.vault.world.vault.builder;

import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.VaultLogic;
import iskallia.vault.world.vault.player.VaultPlayerType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class RaidCommandVaultBuilder extends VaultRaidBuilder {
   private RaidCommandVaultBuilder() {
   }

   public static RaidCommandVaultBuilder get() {
      return new RaidCommandVaultBuilder();
   }

   @Override
   public VaultRaid.Builder initializeBuilder(ServerLevel world, ServerPlayer player, CrystalData crystal) {
      return VaultRaid.builder(VaultLogic.CLASSIC, 0, VaultRaid.SUMMON_AND_KILL_BOSS.get())
         .setInitializer(this.getDefaultInitializer())
         .addEvents(this.getDefaultEvents())
         .addPlayer(VaultPlayerType.RUNNER, player);
   }
}
