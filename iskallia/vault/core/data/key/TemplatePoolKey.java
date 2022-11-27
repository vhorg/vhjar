package iskallia.vault.core.data.key;

import iskallia.vault.core.world.template.data.TemplatePool;
import net.minecraft.resources.ResourceLocation;

public class TemplatePoolKey extends NamedKey<TemplatePoolKey, TemplatePool> {
   protected TemplatePoolKey(ResourceLocation id, String name) {
      super(id, name);
   }

   public static TemplatePoolKey empty() {
      return new TemplatePoolKey(null, null);
   }

   public static TemplatePoolKey create(ResourceLocation id, String name) {
      return new TemplatePoolKey(id, name);
   }
}
