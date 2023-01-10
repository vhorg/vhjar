package iskallia.vault.network.message;

import iskallia.vault.client.ClientHistoricFavoritesData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class HistoricFavoritesMessage {
   private final List<UUID> favorites;

   public HistoricFavoritesMessage(List<UUID> favorites) {
      this.favorites = favorites;
   }

   public static void encode(HistoricFavoritesMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.favorites.size());

      for (int i = 0; i < message.favorites.size(); i++) {
         buffer.writeUUID(message.favorites.get(i));
      }
   }

   public static HistoricFavoritesMessage decode(FriendlyByteBuf buffer) {
      int size = buffer.readInt();
      List<UUID> favorites = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         favorites.add(buffer.readUUID());
      }

      return new HistoricFavoritesMessage(favorites);
   }

   public List<UUID> getFavorites() {
      return this.favorites;
   }

   public static void handle(HistoricFavoritesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientHistoricFavoritesData.receive(message));
      context.setPacketHandled(true);
   }
}
