package iskallia.vault.client;

import iskallia.vault.entity.eternal.ActiveEternalData;
import iskallia.vault.network.message.ActiveEternalMessage;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClientActiveEternalData {
   private static final Set<ActiveEternalData.ActiveEternal> activeEternals = new LinkedHashSet<>();

   public static Set<ActiveEternalData.ActiveEternal> getActiveEternals() {
      return Collections.unmodifiableSet(activeEternals);
   }

   public static void receive(ActiveEternalMessage message) {
      Set<ActiveEternalData.ActiveEternal> updatedEternals = message.getActiveEternals();
      Set<ActiveEternalData.ActiveEternal> processed = new HashSet<>();
      activeEternals.removeIf(activeEternal -> {
         ActiveEternalData.ActiveEternal updated = null;

         for (ActiveEternalData.ActiveEternal eternal : updatedEternals) {
            if (eternal.equals(activeEternal)) {
               updated = eternal;
               break;
            }
         }

         if (updated == null) {
            return true;
         } else {
            activeEternal.updateFrom(updated);
            processed.add(updated);
            return false;
         }
      });
      updatedEternals.removeIf(processed::contains);
      activeEternals.addAll(updatedEternals);
   }

   public static void clearClientCache() {
      activeEternals.clear();
   }
}
