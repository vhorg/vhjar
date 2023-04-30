package iskallia.vault.core.data.key;

import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import net.minecraft.resources.ResourceLocation;

public class VaultModifiersKey extends NamedKey<VaultModifiersKey, VaultModifierRegistry> {
   protected VaultModifiersKey(ResourceLocation id, String name) {
      super(id, name);
   }

   public static VaultModifiersKey empty() {
      return new VaultModifiersKey(null, null);
   }
}
