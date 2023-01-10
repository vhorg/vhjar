package iskallia.vault.network.message;

import iskallia.vault.world.data.PlayerHistoricFavoritesData;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundAddHistoricFavoriteMessage {
   private final UUID uuid;

   public ServerboundAddHistoricFavoriteMessage(UUID uuid) {
      this.uuid = uuid;
   }

   public static void encode(ServerboundAddHistoricFavoriteMessage message, FriendlyByteBuf buffer) {
      buffer.writeUUID(message.uuid);
   }

   public static ServerboundAddHistoricFavoriteMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundAddHistoricFavoriteMessage(buffer.readUUID());
   }

   public static void handle(ServerboundAddHistoricFavoriteMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            PlayerHistoricFavoritesData.HistoricFavorites historicFavorites = PlayerHistoricFavoritesData.get(sender.server).getHistoricFavorites(sender);
            List<UUID> favorites = PlayerHistoricFavoritesData.get(sender.server).getHistoricFavorites(sender).getFavorites();
            if (favorites.contains(message.uuid)) {
               favorites.remove(message.uuid);
            } else {
               favorites.add(message.uuid);
            }

            PlayerHistoricFavoritesData.get(sender.server).setDirty();
            historicFavorites.syncToClient(sender.server);
         }
      });
      context.setPacketHandled(true);
   }
}
