package iskallia.vault.skill.ability;

import java.util.Map;

public class LegacyAbilityMapper {
   private static final Map<String, String> LEGACY_ABILITY_MAP = Map.of(
      "Tank", "Empower", "Tank_Projectile", "Empower_Ice_Armour", "Tank_Reflect", "Shell_Porcupine"
   );

   public static String mapAbilityName(String abilityName) {
      return LEGACY_ABILITY_MAP.getOrDefault(abilityName, abilityName);
   }
}
