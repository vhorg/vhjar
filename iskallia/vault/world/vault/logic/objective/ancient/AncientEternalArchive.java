package iskallia.vault.world.vault.logic.objective.ancient;

import iskallia.vault.util.NameProviderPublic;
import iskallia.vault.world.data.EternalsData;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;

public class AncientEternalArchive {
   public static List<String> getAncients(MinecraftServer server, UUID playerId) {
      EternalsData.EternalGroup playerEternals = EternalsData.get(server).getEternals(playerId);
      List<String> ancients = NameProviderPublic.getVHSMPAssociates();
      Collections.shuffle(ancients);
      ancients.removeIf(ancientRef -> playerEternals.containsOriginalEternal(ancientRef, true));
      return ancients;
   }
}
