package iskallia.vault.gear.attribute.ability.special.base;

import iskallia.vault.gear.attribute.ability.special.EntropyPoisonModification;
import iskallia.vault.gear.attribute.ability.special.FrostNovaVulnerabilityModification;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class SpecialAbilityModificationRegistry {
   private static final Map<ResourceLocation, SpecialAbilityModification<?, ?>> specialModifications = new HashMap<>();

   public static <T extends SpecialAbilityModification<?, ?>> T getAbilityModification(ResourceLocation key) {
      T modification = (T)specialModifications.get(key);
      if (modification == null) {
         throw new IllegalArgumentException("Unknown special modification: " + key);
      } else {
         return modification;
      }
   }

   public static void init() {
      register(new FrostNovaVulnerabilityModification());
      register(new EntropyPoisonModification());
   }

   private static void register(SpecialAbilityModification<?, ?> modification) {
      specialModifications.put(modification.getKey(), modification);
   }
}
