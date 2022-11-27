package iskallia.vault.network.message;

import iskallia.vault.client.ClientActiveEternalData;
import iskallia.vault.entity.eternal.ActiveEternalData;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class ActiveEternalMessage {
   private final Set<ActiveEternalData.ActiveEternal> activeEternals;

   public ActiveEternalMessage(Set<ActiveEternalData.ActiveEternal> activeEternals) {
      this.activeEternals = activeEternals;
   }

   public Set<ActiveEternalData.ActiveEternal> getActiveEternals() {
      return this.activeEternals;
   }

   public static void encode(ActiveEternalMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.activeEternals.size());
      message.activeEternals.forEach(activeEternal -> activeEternal.write(buffer));
   }

   public static ActiveEternalMessage decode(FriendlyByteBuf buffer) {
      int eternalCount = buffer.readInt();
      Set<ActiveEternalData.ActiveEternal> activeEternals = new LinkedHashSet<>();

      for (int i = 0; i < eternalCount; i++) {
         activeEternals.add(ActiveEternalData.ActiveEternal.read(buffer));
      }

      return new ActiveEternalMessage(activeEternals);
   }

   public static void handle(ActiveEternalMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientActiveEternalData.receive(message));
      context.setPacketHandled(true);
   }
}
