package iskallia.vault.world.vault.logic.objective.ancient;

import iskallia.vault.world.data.EternalsData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;

public class AncientEternalArchive {
   private static final Set<String> defaultAncients = new HashSet<>();

   public static List<String> getAncients(MinecraftServer server, UUID playerId) {
      EternalsData.EternalGroup playerEternals = EternalsData.get(server).getEternals(playerId);
      List<String> filtered = new ArrayList<>(defaultAncients);
      Collections.shuffle(filtered);
      filtered.removeIf(ancientRef -> playerEternals.containsOriginalEternal(ancientRef, true));
      return filtered;
   }

   private static void init() {
      defaultAncients.add("purplefuzzyhippo");
      defaultAncients.add("bartomaximus");
      defaultAncients.add("ocsisor");
      defaultAncients.add("Puffball920");
   }

   static {
      init();
   }
}
