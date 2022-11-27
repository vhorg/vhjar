package iskallia.vault.network.message;

import iskallia.vault.client.ClientInvalidConfigAlert;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class InvalidConfigsMessage {
   private final Collection<String> invalidConfigList;

   public InvalidConfigsMessage(Collection<String> invalidConfigList) {
      this.invalidConfigList = invalidConfigList;
   }

   public static void encode(InvalidConfigsMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.invalidConfigList.size());

      for (String config : message.invalidConfigList) {
         buffer.writeUtf(config);
      }
   }

   public static InvalidConfigsMessage decode(FriendlyByteBuf buffer) {
      int size = buffer.readInt();
      List<String> invalidConfigList = new ArrayList<>(size);

      for (int i = 0; i < size; i++) {
         invalidConfigList.add(buffer.readUtf());
      }

      return new InvalidConfigsMessage(invalidConfigList);
   }

   public static void handle(InvalidConfigsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientInvalidConfigAlert.showAlert(message.invalidConfigList));
      context.setPacketHandled(true);
   }
}
