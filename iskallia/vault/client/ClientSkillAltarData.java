package iskallia.vault.client;

import iskallia.vault.world.data.SkillAltarData;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClientSkillAltarData {
   private static final Map<UUID, List<SkillAltarData.SkillIcon>> playerAbilityIconKeys = new HashMap<>();

   public static List<SkillAltarData.SkillIcon> getAbilityIconKeys(UUID playerUUID) {
      return playerAbilityIconKeys.getOrDefault(playerUUID, Collections.emptyList());
   }

   public static void setAbilityIconKeys(UUID playerUUID, List<SkillAltarData.SkillIcon> abilityIconKeys) {
      playerAbilityIconKeys.put(playerUUID, abilityIconKeys);
   }
}
