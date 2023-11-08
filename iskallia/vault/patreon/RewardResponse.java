package iskallia.vault.patreon;

import java.util.List;

public class RewardResponse {
   private String uuid;
   private List<RewardResponse.Tier> tier;

   public List<PatreonTier> convertTiers() {
      return this.tier.stream().map(tier1 -> PatreonTier.fromName(tier1.name)).toList();
   }

   public static class Tier {
      private String name;
      private String id;
   }
}
