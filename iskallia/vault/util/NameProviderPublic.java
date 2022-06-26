package iskallia.vault.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraftforge.common.UsernameCache;

public class NameProviderPublic {
   private static final Random rand = new Random();
   private static final List<String> DEV_NAMES = Lists.newArrayList(
      new String[]{
         "KaptainWutax",
         "iGoodie",
         "jmilthedude",
         "Scalda",
         "Kumara22",
         "Goktwo",
         "Aolsen96",
         "Winter_Grave",
         "kimandjax",
         "Monni_21",
         "Starmute",
         "MukiTanuki",
         "RowanArtifex",
         "HellFirePvP",
         "Pau1_",
         "Douwsky",
         "pomodoko",
         "Damnsecci"
      }
   );
   private static final List<String> SMP_S2 = Lists.newArrayList(
      new String[]{
         "CaptainSparklez",
         "Stressmonster101",
         "CaptainPuffy",
         "AntonioAsh",
         "ItsFundy",
         "iskall85",
         "Tubbo_",
         "HBomb94",
         "5uppps",
         "X33N",
         "PeteZahHutt",
         "Seapeekay"
      }
   );

   public static String getRandomName() {
      return getRandomName(rand);
   }

   public static String getRandomName(Random random) {
      return MiscUtils.getRandomEntry(getAllAvailableNames(), random);
   }

   public static List<String> getAllAvailableNames() {
      List<String> names = new ArrayList<>();
      names.addAll(DEV_NAMES);
      names.addAll(SMP_S2);
      names.addAll(getKnownUsernames());
      return names;
   }

   public static List<String> getVHSMPAssociates() {
      List<String> names = new ArrayList<>();
      names.addAll(DEV_NAMES);
      names.addAll(SMP_S2);
      return names;
   }

   private static List<String> getKnownUsernames() {
      return new ArrayList<>(UsernameCache.getMap().values());
   }
}
