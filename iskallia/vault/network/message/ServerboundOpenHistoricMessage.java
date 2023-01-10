package iskallia.vault.network.message;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.world.data.PlayerHistoricFavoritesData;
import iskallia.vault.world.data.VaultSnapshots;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundOpenHistoricMessage {
   public static final ServerboundOpenHistoricMessage INSTANCE = new ServerboundOpenHistoricMessage();

   public static void encode(ServerboundOpenHistoricMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundOpenHistoricMessage decode(FriendlyByteBuf buffer) {
      return INSTANCE;
   }

   public static void handle(ServerboundOpenHistoricMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
               List<VaultSnapshot> snapshots = VaultSnapshots.getAll();
               List<UUID> list = PlayerHistoricFavoritesData.get(sender.server).getHistoricFavorites(sender).getFavorites();
               List<VaultSnapshot> copy = new ArrayList<>(snapshots);
               Collections.reverse(copy);
               List<VaultSnapshot> prev50 = new ArrayList<>();

               for (VaultSnapshot snapshot : copy) {
                  if (snapshot.getEnd() != null && snapshot.getEnd().get(Vault.STATS).getMap().containsKey(sender.getUUID())) {
                     prev50.add(snapshot);
                     if (prev50.size() >= 50) {
                        break;
                     }
                  }
               }

               List<VaultSnapshot> favorites = new ArrayList<>(
                  copy.stream()
                     .filter(
                        snapshotx -> list.stream().anyMatch(uuid -> uuid.equals(snapshotx.getEnd().get(Vault.ID)))
                           && prev50.stream().noneMatch(vaultSnapshot -> vaultSnapshot.getEnd().get(Vault.ID).equals(snapshotx.getEnd().get(Vault.ID)))
                     )
                     .toList()
               );
               prev50.addAll(favorites);
               ModNetwork.CHANNEL.sendTo(new VaultPlayerHistoricDataMessage.S2C(prev50), sender.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
         }
      );
      context.setPacketHandled(true);
   }
}
