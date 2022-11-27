package iskallia.vault.core.world.template.data;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.VaultRegistry;
import net.minecraft.resources.ResourceLocation;

public class IndirectTemplateEntry implements TemplateEntry {
   protected ResourceLocation reference;

   public IndirectTemplateEntry(ResourceLocation reference) {
      this.reference = reference;
   }

   public ResourceLocation getReferenceId() {
      return this.reference;
   }

   public TemplatePoolKey getReference() {
      return VaultRegistry.TEMPLATE_POOL.getKey(this.reference);
   }

   @Override
   public TemplateKey getTemplate() {
      throw new UnsupportedOperationException("Indirect entry, flatten first");
   }

   @Override
   public Iterable<PaletteKey> getPalettes() {
      throw new UnsupportedOperationException("Indirect entry, flatten first");
   }

   @Override
   public TemplateEntry flatten(Version version, RandomSource random) {
      TemplateEntry flattened = this.getReference().get(version).getRandom(random).orElse(null);
      return flattened == null ? null : flattened.flatten(version, random);
   }

   @Override
   public boolean validate() {
      return VaultRegistry.TEMPLATE_POOL.getKey(this.reference) != null;
   }

   @Override
   public String toString() {
      return "{reference=" + this.reference.toString() + "}";
   }
}
