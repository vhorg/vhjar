package iskallia.vault.patreon;

import java.util.List;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class PatreonPlayerData {
   private final List<PatreonTier> tiers;

   public PatreonPlayerData(List<PatreonTier> tiers) {
      this.tiers = tiers;
   }

   public boolean isAtLeastTier(PatreonTier tier) {
      if (!FMLEnvironment.production) {
         return true;
      } else {
         for (PatreonTier playerTier : this.tiers) {
            if (playerTier.isThisEqualOrHigherThan(tier)) {
               return true;
            }
         }

         return false;
      }
   }
}
