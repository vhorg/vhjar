package iskallia.vault.client;

import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.network.message.EternalSyncMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class ClientEternalData {
   private static Map<UUID, List<EternalDataSnapshot>> eternalSnapshots = new HashMap<>();

   @Nullable
   public static EternalDataSnapshot getSnapshot(UUID eternalId) {
      for (UUID playerId : eternalSnapshots.keySet()) {
         for (EternalDataSnapshot snapshot : eternalSnapshots.get(playerId)) {
            if (snapshot.getId().equals(eternalId)) {
               return snapshot;
            }
         }
      }

      return null;
   }

   public static List<EternalDataSnapshot> getPlayerEternals(UUID playerId) {
      return eternalSnapshots.getOrDefault(playerId, new ArrayList<>());
   }

   public static void receiveUpdate(EternalSyncMessage pkt) {
      eternalSnapshots = pkt.getEternalData();
   }
}
