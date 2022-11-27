package iskallia.vault.core.data.key;

import iskallia.vault.core.world.template.Template;
import net.minecraft.resources.ResourceLocation;

public class TemplateKey extends NamedKey<TemplateKey, Template> {
   protected TemplateKey(ResourceLocation id, String name) {
      super(id, name);
   }

   public static TemplateKey empty() {
      return new TemplateKey(null, null);
   }

   public static TemplateKey create(ResourceLocation id, String name) {
      return new TemplateKey(id, name);
   }
}
