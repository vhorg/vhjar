package iskallia.vault.world.vault.builder;

import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.VaultPartyData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayerType;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class CoopVaultBuilder extends VaultRaidBuilder {
   private static final CoopVaultBuilder INSTANCE = new CoopVaultBuilder();

   private CoopVaultBuilder() {
   }

   public static CoopVaultBuilder getInstance() {
      return INSTANCE;
   }

   @Override
   public VaultRaid.Builder initializeBuilder(ServerLevel world, ServerPlayer player, CrystalData crystal) {
      VaultRaid.Builder builder = this.getDefaultBuilder(crystal, world, player);
      if (player != null) {
         Optional<VaultPartyData.Party> partyOpt = VaultPartyData.get(world).getParty(player.getUUID());
         if (partyOpt.isPresent() && partyOpt.get().getMembers().size() > 1) {
            VaultPartyData.Party party = partyOpt.get();
            UUID leader = party.getLeader() != null ? party.getLeader() : MiscUtils.getRandomEntry(party.getMembers(), world.getRandom());
            builder.set(VaultRaid.HOST, leader);
            party.getMembers().forEach(uuid -> {
               ServerPlayer partyPlayer = world.getServer().getPlayerList().getPlayer(uuid);
               if (partyPlayer != null) {
                  builder.addPlayer(VaultPlayerType.RUNNER, partyPlayer);
               }
            });
         } else {
            builder.addPlayer(VaultPlayerType.RUNNER, player);
            builder.set(VaultRaid.HOST, player.getUUID());
         }
      }

      builder.setLevelInitializer(VaultRaid.INIT_LEVEL_COOP);
      return builder;
   }

   @Override
   protected int getVaultLevelForObjective(ServerLevel world, ServerPlayer player) {
      return player == null ? 0 : VaultPartyData.get(world).getParty(player.getUUID()).map(party -> {
         UUID leader = party.getLeader() != null ? party.getLeader() : MiscUtils.getRandomEntry(party.getMembers(), world.getRandom());
         return PlayerVaultStatsData.get(world).getVaultStats(leader).getVaultLevel();
      }).orElse(super.getVaultLevelForObjective(world, player));
   }
}
