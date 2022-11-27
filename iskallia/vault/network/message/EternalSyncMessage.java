package iskallia.vault.network.message;

import iskallia.vault.client.ClientEternalData;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class EternalSyncMessage {
   private final Map<UUID, List<EternalDataSnapshot>> eternalData;

   public EternalSyncMessage(Map<UUID, List<EternalDataSnapshot>> eternalData) {
      this.eternalData = eternalData;
   }

   public Map<UUID, List<EternalDataSnapshot>> getEternalData() {
      return this.eternalData;
   }

   public static void encode(EternalSyncMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeInt(pkt.eternalData.size());
      pkt.eternalData.forEach((playerUUID, playerEternals) -> {
         buffer.writeUUID(playerUUID);
         buffer.writeInt(playerEternals.size());
         playerEternals.forEach(eternalData -> eternalData.serialize(buffer, true));
      });
   }

   public static EternalSyncMessage decode(FriendlyByteBuf buffer) {
      Map<UUID, List<EternalDataSnapshot>> eternalData = new HashMap<>();
      int playerEternals = buffer.readInt();

      for (int i = 0; i < playerEternals; i++) {
         UUID playerUUID = buffer.readUUID();
         List<EternalDataSnapshot> snapshots = new ArrayList<>();
         int eternals = buffer.readInt();

         for (int j = 0; j < eternals; j++) {
            snapshots.add(EternalDataSnapshot.deserialize(buffer));
         }

         eternalData.put(playerUUID, snapshots);
      }

      return new EternalSyncMessage(eternalData);
   }

   public static void handle(EternalSyncMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientEternalData.receiveUpdate(pkt));
      context.setPacketHandled(true);
   }
}
