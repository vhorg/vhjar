package iskallia.vault.world.vault.logic.objective.ancient;

import com.google.common.collect.Sets;
import iskallia.vault.world.data.EternalsData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;

public class AncientEternalArchive {
   private static final Map<UUID, Set<AncientEternalReference>> playerAncients = new HashMap<>();
   private static final Set<AncientEternalReference> defaultAncients = new HashSet<>();

   public static List<AncientEternalReference> getAncients(MinecraftServer server, UUID playerId) {
      Set<AncientEternalReference> ancients = playerAncients.getOrDefault(playerId, Collections.emptySet());
      if (ancients.isEmpty()) {
         ancients = defaultAncients;
      }

      EternalsData.EternalGroup playerEternals = EternalsData.get(server).getEternals(playerId);
      List<AncientEternalReference> filtered = new ArrayList<>(ancients);
      Collections.shuffle(filtered);
      filtered.removeIf(ancientRef -> playerEternals.containsOriginalEternal(ancientRef.getName(), true));
      return filtered;
   }

   private static void init() {
      playerAncients.put(
         UUID.fromString("cfaefb14-46d5-473b-9e8e-67ecbf119df7"),
         Sets.newHashSet(
            new AncientEternalReference[]{
               new AncientEternalReference("ScofieX"),
               new AncientEternalReference("BONNe1704"),
               new AncientEternalReference("SaltyFox104"),
               new AncientEternalReference("heighleybaily"),
               new AncientEternalReference("xPebbles"),
               new AncientEternalReference("sirwoodsyxxv"),
               new AncientEternalReference("DaemonArcane"),
               new AncientEternalReference("DerpyDiscord"),
               new AncientEternalReference("kimmers_mouse"),
               new AncientEternalReference("darthxander314"),
               new AncientEternalReference("Th3W4rD3n"),
               new AncientEternalReference("mag_ring_23"),
               new AncientEternalReference("CoercriSeareach"),
               new AncientEternalReference("Silentfoxxy")
            }
         )
      );
      playerAncients.put(
         UUID.fromString("5f820c39-5883-4392-b174-3125ac05e38c"),
         Sets.newHashSet(
            new AncientEternalReference[]{
               new AncientEternalReference("divisorofzero"),
               new AncientEternalReference("icy_butterfly"),
               new AncientEternalReference("AlduwinsBane20"),
               new AncientEternalReference("escsora"),
               new AncientEternalReference("hbeant"),
               new AncientEternalReference("tabicattv"),
               new AncientEternalReference("lordopf3rwurst"),
               new AncientEternalReference("n3e4d5"),
               new AncientEternalReference("cruisinkiy"),
               new AncientEternalReference("msapw1z"),
               new AncientEternalReference("Kevin"),
               new AncientEternalReference("dotsidious"),
               new AncientEternalReference("nelphyta"),
               new AncientEternalReference("bemawe4"),
               new AncientEternalReference("cringycronan"),
               new AncientEternalReference("wet_fart_"),
               new AncientEternalReference("papercoder64"),
               new AncientEternalReference("themightyderp64"),
               new AncientEternalReference("crumbl"),
               new AncientEternalReference("fierce_fairy"),
               new AncientEternalReference("HolyWaffles"),
               new AncientEternalReference("aw3som3n3ss")
            }
         )
      );
      playerAncients.put(
         UUID.fromString("d974cbae-e62b-4e34-a1b8-0175a2d41d9a"),
         Sets.newHashSet(
            new AncientEternalReference[]{
               new AncientEternalReference("rebelzedgaming"),
               new AncientEternalReference("LHFRGamer"),
               new AncientEternalReference("aquanamria"),
               new AncientEternalReference("chrystalizer93")
            }
         )
      );
      playerAncients.put(
         UUID.fromString("7ed3587b-e656-4689-90d6-08e11daaf907"),
         Sets.newHashSet(
            new AncientEternalReference[]{
               new AncientEternalReference("ShieldmanH"),
               new AncientEternalReference("Lokisis718"),
               new AncientEternalReference("Mag_Ring_23"),
               new AncientEternalReference("Lazeralis"),
               new AncientEternalReference("Twolf999"),
               new AncientEternalReference("mayaicefire"),
               new AncientEternalReference("zenzykai"),
               new AncientEternalReference("Kurtis521"),
               new AncientEternalReference("romanwarrior111"),
               new AncientEternalReference("Joumie"),
               new AncientEternalReference("shulvorak"),
               new AncientEternalReference("a12helton"),
               new AncientEternalReference("CoercriSeareach"),
               new AncientEternalReference("culveerin"),
               new AncientEternalReference("justicefool"),
               new AncientEternalReference("coit0529"),
               new AncientEternalReference("SilentFoxxy"),
               new AncientEternalReference("Quinn_Toast"),
               new AncientEternalReference("svenlu"),
               new AncientEternalReference("mefallit"),
               new AncientEternalReference("sarinablueberry"),
               new AncientEternalReference("Dylan"),
               new AncientEternalReference("junyhc"),
               new AncientEternalReference("Ghoztly"),
               new AncientEternalReference("og_darkman"),
               new AncientEternalReference("BONNe1704"),
               new AncientEternalReference("sboggsie02"),
               new AncientEternalReference("sArAh0hA"),
               new AncientEternalReference("ryanwhite91"),
               new AncientEternalReference("greatlakesgirl"),
               new AncientEternalReference("wheelslro"),
               new AncientEternalReference("superstud777"),
               new AncientEternalReference("windoelicker001"),
               new AncientEternalReference("Naiya_Subete"),
               new AncientEternalReference("IthKamkazi"),
               new AncientEternalReference("Damnsecci"),
               new AncientEternalReference("Aliysium"),
               new AncientEternalReference("UnshavenCraig"),
               new AncientEternalReference("Trefzz"),
               new AncientEternalReference("Ariany231"),
               new AncientEternalReference("jackfrost9879"),
               new AncientEternalReference("Lochlainnible"),
               new AncientEternalReference("Bink82")
            }
         )
      );
      playerAncients.put(
         UUID.fromString("ad425147-a229-48a0-930b-ec58f9c5dd84"),
         Sets.newHashSet(
            new AncientEternalReference[]{
               new AncientEternalReference("ShieldManH"),
               new AncientEternalReference("kelseym3"),
               new AncientEternalReference("aquanamria"),
               new AncientEternalReference("Cosmovoli"),
               new AncientEternalReference("Ashleafy"),
               new AncientEternalReference("dtgkosh"),
               new AncientEternalReference("gemsongrs"),
               new AncientEternalReference("KubyTuby"),
               new AncientEternalReference("EvelienRain"),
               new AncientEternalReference("Lazeralis"),
               new AncientEternalReference("celinabena"),
               new AncientEternalReference("Samasina"),
               new AncientEternalReference("sgwonderer")
            }
         )
      );
      defaultAncients.add(new AncientEternalReference("purplefuzzyhippo"));
      defaultAncients.add(new AncientEternalReference("bartomaximus"));
      defaultAncients.add(new AncientEternalReference("ocsisor"));
      defaultAncients.add(new AncientEternalReference("Puffball920"));
   }

   static {
      init();
   }
}
