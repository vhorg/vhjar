package iskallia.vault.client;

import iskallia.vault.gear.crafting.ProficiencyType;

public class ClientProficiencyData {
   private static int proficiency = 0;

   public static int getProficiency() {
      return proficiency;
   }

   public static float getProficiency(ProficiencyType type) {
      return proficiency;
   }

   public static void updateProficiency(int proficiency) {
      ClientProficiencyData.proficiency = proficiency;
   }
}
