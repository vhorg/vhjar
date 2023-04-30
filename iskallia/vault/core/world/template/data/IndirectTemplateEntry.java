package iskallia.vault.core.world.template.data;

import com.google.gson.JsonElement;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.VaultRegistry;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public class IndirectTemplateEntry implements TemplateEntry {
   protected ResourceLocation reference;

   public IndirectTemplateEntry() {
      this(null);
   }

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
   public Optional<JsonElement> writeJson() {
      return Adapters.IDENTIFIER.writeJson(this.reference);
   }

   @Override
   public void readJson(JsonElement json) {
      Adapters.IDENTIFIER.readJson(json).ifPresent(value -> this.reference = value);
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
