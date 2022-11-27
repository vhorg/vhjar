package iskallia.vault.client;

import iskallia.vault.gear.crafting.ProficiencyType;
import java.util.HashMap;
import java.util.Map;

public class ClientProficiencyData {
   private static Map<ProficiencyType, Float> proficiencyPercentages = new HashMap<>();

   public static float getProficiency(ProficiencyType type) {
      return proficiencyPercentages.getOrDefault(type, 0.0F);
   }

   public static void updateProficiencies(Map<ProficiencyType, Float> proficiencies) {
      proficiencyPercentages = proficiencies;
   }
}
