package iskallia.vault.world.legacy.raid;

import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class ArenaSpawner implements INBTSerializable<CompoundNBT> {
   private static final String[] DEV_NICKS = new String[]{
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
      "Douwsky"
   };

   public static String getRandomDevName(Random rand) {
      return DEV_NICKS[rand.nextInt(DEV_NICKS.length)];
   }

   public CompoundNBT serializeNBT() {
      return null;
   }

   public void deserializeNBT(CompoundNBT nbt) {
   }
}
