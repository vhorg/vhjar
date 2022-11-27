package iskallia.vault.core.world.template.data;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.VaultRegistry;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class DirectTemplateEntry implements TemplateEntry {
   protected ResourceLocation template;
   protected List<PaletteKey> palettes;

   public DirectTemplateEntry(ResourceLocation template, List<PaletteKey> palettes) {
      this.template = template;
      this.palettes = palettes;
   }

   public ResourceLocation getTemplateId() {
      return this.template;
   }

   @Override
   public TemplateKey getTemplate() {
      return VaultRegistry.TEMPLATE.getKey(this.template);
   }

   @Override
   public Iterable<PaletteKey> getPalettes() {
      return this.palettes;
   }

   @Override
   public TemplateEntry flatten(Version version, RandomSource random) {
      return this;
   }

   @Override
   public boolean validate() {
      return VaultRegistry.TEMPLATE.getKey(this.template) != null;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append('{');
      sb.append("template=");
      sb.append(this.template.toString());
      sb.append(",palettes=[");

      for (PaletteKey palette : this.palettes) {
         sb.append(palette.getId()).append(',');
      }

      sb.append("]}");
      return sb.toString();
   }
}
