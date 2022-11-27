package iskallia.vault.skill.archetype.archetype;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.config.DefaultConfig;
import net.minecraft.resources.ResourceLocation;

public class DefaultArchetype extends AbstractArchetype<DefaultConfig> {
   public DefaultArchetype(ResourceLocation id) {
      super(() -> ModConfigs.ARCHETYPES.DEFAULT, id);
   }
}
