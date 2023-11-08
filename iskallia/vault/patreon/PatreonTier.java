package iskallia.vault.patreon;

public enum PatreonTier {
   DWELLER("Vault Dweller"),
   CHEESER("Vault Cheeser"),
   GOBLIN("Vault Goblin"),
   CHAMPION("Vault Champion");

   private final String name;

   private PatreonTier(String name) {
      this.name = name;
   }

   public static PatreonTier fromName(String name) {
      for (PatreonTier tier : values()) {
         if (tier.name.equals(name)) {
            return tier;
         }
      }

      return null;
   }

   public boolean isThisEqualOrHigherThan(PatreonTier other) {
      return this.ordinal() >= other.ordinal();
   }
}
