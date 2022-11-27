package iskallia.vault.client;

import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.ArchetypeRegistry;
import net.minecraft.resources.ResourceLocation;

public class ClientArchetypeData {
   private static AbstractArchetype<?> currentArchetype;

   public static AbstractArchetype<?> getCurrentArchetype() {
      return currentArchetype;
   }

   public static void setCurrentArchetype(ResourceLocation resourceLocation) {
      currentArchetype = ArchetypeRegistry.getArchetype(resourceLocation);
   }
}
