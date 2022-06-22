package iskallia.vault.world.vault.builder;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.data.VaultPartyData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayerType;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class FinalLobbyBuilder extends VaultRaidBuilder {
   private static final FinalLobbyBuilder INSTANCE = new FinalLobbyBuilder();

   private FinalLobbyBuilder() {
   }

   public static FinalLobbyBuilder getInstance() {
      return INSTANCE;
   }

   @Override
   public VaultRaid.Builder initializeBuilder(ServerWorld world, ServerPlayerEntity player, CrystalData crystal) {
      VaultRaid.Builder builder = this.getDefaultBuilder(crystal, world, player);
      Set<UUID> players = crystal.getFrameData()
         .tiles
         .stream()
         .filter(tile -> tile.block == ModBlocks.FINAL_VAULT_FRAME)
         .filter(tile -> tile.data.func_150297_b("OwnerUUID", 8))
         .map(tile -> UUID.fromString(tile.data.func_74779_i("OwnerUUID")))
         .collect(Collectors.toSet());
      if (!players.contains(player.func_110124_au())) {
         return null;
      } else {
         for (UUID uuid : players) {
            ServerPlayerEntity vaultPlayer = world.func_73046_m().func_184103_al().func_177451_a(uuid);
            if (vaultPlayer == null) {
               return null;
            }
         }

         if (world.func_82736_K().func_223586_b(ModGameRules.FINAL_VAULT_ALLOW_PARTY)) {
            VaultPartyData data = VaultPartyData.get(world);

            for (UUID uuidx : new ArrayList<>(players)) {
               data.getParty(uuidx).ifPresent(party -> players.addAll(party.getMembers()));
            }
         }

         for (UUID uuidx : players) {
            ServerPlayerEntity partyPlayer = world.func_73046_m().func_184103_al().func_177451_a(uuidx);
            if (partyPlayer != null) {
               builder.addPlayer(VaultPlayerType.RUNNER, partyPlayer);
            }
         }

         builder.set(VaultRaid.HOST, player.func_110124_au());
         builder.setGenerator(VaultRaid.FINAL_LOBBY);
         return builder;
      }
   }
}
