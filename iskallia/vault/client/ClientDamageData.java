package iskallia.vault.client;

import iskallia.vault.network.message.PlayerDamageMultiplierMessage;

public class ClientDamageData {
   private static float damageMultiplier = 1.0F;

   public static float getCurrentDamageMultiplier() {
      return damageMultiplier;
   }

   public static void receiveUpdate(PlayerDamageMultiplierMessage message) {
      damageMultiplier = message.getMultiplier();
   }

   public static void clearClientCache() {
      damageMultiplier = 1.0F;
   }
}
