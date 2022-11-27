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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FinalLobbyBuilder extends VaultRaidBuilder {
   private static final FinalLobbyBuilder INSTANCE = new FinalLobbyBuilder();

   private FinalLobbyBuilder() {
   }

   public static FinalLobbyBuilder getInstance() {
      return INSTANCE;
   }

   @Override
   public VaultRaid.Builder initializeBuilder(ServerLevel world, ServerPlayer player, CrystalData crystal) {
      VaultRaid.Builder builder = this.getDefaultBuilder(crystal, world, player);
      Set<UUID> players = crystal.getFrameData()
         .tiles
         .stream()
         .filter(tile -> tile.block == ModBlocks.FINAL_VAULT_FRAME)
         .filter(tile -> tile.data.contains("OwnerUUID", 8))
         .map(tile -> UUID.fromString(tile.data.getString("OwnerUUID")))
         .collect(Collectors.toSet());
      if (!players.contains(player.getUUID())) {
         return null;
      } else {
         for (UUID uuid : players) {
            ServerPlayer vaultPlayer = world.getServer().getPlayerList().getPlayer(uuid);
            if (vaultPlayer == null) {
               return null;
            }
         }

         if (world.getGameRules().getBoolean(ModGameRules.FINAL_VAULT_ALLOW_PARTY)) {
            VaultPartyData data = VaultPartyData.get(world);

            for (UUID uuidx : new ArrayList<>(players)) {
               data.getParty(uuidx).ifPresent(party -> players.addAll(party.getMembers()));
            }
         }

         for (UUID uuidx : players) {
            ServerPlayer partyPlayer = world.getServer().getPlayerList().getPlayer(uuidx);
            if (partyPlayer != null) {
               builder.addPlayer(VaultPlayerType.RUNNER, partyPlayer);
            }
         }

         builder.set(VaultRaid.HOST, player.getUUID());
         builder.setGenerator(VaultRaid.FINAL_LOBBY);
         return builder;
      }
   }
}
