package iskallia.vault.world.vault.builder;

import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.VaultPartyData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayerType;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class CoopVaultBuilder extends VaultRaidBuilder {
   private static final CoopVaultBuilder INSTANCE = new CoopVaultBuilder();

   private CoopVaultBuilder() {
   }

   public static CoopVaultBuilder getInstance() {
      return INSTANCE;
   }

   @Override
   public VaultRaid.Builder initializeBuilder(ServerWorld world, ServerPlayerEntity player, CrystalData crystal) {
      VaultRaid.Builder builder = this.getDefaultBuilder(crystal, world, player);
      Optional<VaultPartyData.Party> partyOpt = VaultPartyData.get(world).getParty(player.func_110124_au());
      if (partyOpt.isPresent() && partyOpt.get().getMembers().size() > 1) {
         VaultPartyData.Party party = partyOpt.get();
         UUID leader = party.getLeader() != null ? party.getLeader() : MiscUtils.getRandomEntry(party.getMembers(), world.func_201674_k());
         builder.set(VaultRaid.HOST, leader);
         party.getMembers().forEach(uuid -> {
            ServerPlayerEntity partyPlayer = world.func_73046_m().func_184103_al().func_177451_a(uuid);
            if (partyPlayer != null) {
               builder.addPlayer(VaultPlayerType.RUNNER, partyPlayer);
            }
         });
      } else {
         builder.addPlayer(VaultPlayerType.RUNNER, player);
         builder.set(VaultRaid.HOST, player.func_110124_au());
      }

      builder.setLevelInitializer(VaultRaid.INIT_LEVEL_COOP);
      return builder;
   }

   @Override
   protected int getVaultLevelForObjective(ServerWorld world, ServerPlayerEntity player) {
      return VaultPartyData.get(world).getParty(player.func_110124_au()).map(party -> {
         UUID leader = party.getLeader() != null ? party.getLeader() : MiscUtils.getRandomEntry(party.getMembers(), world.func_201674_k());
         return PlayerVaultStatsData.get(world).getVaultStats(leader).getVaultLevel();
      }).orElse(super.getVaultLevelForObjective(world, player));
   }
}
