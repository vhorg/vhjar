package iskallia.vault.core.world.template.data;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.random.RandomSource;

public interface TemplateEntry {
   TemplateKey getTemplate();

   Iterable<PaletteKey> getPalettes();

   TemplateEntry flatten(Version var1, RandomSource var2);

   boolean validate();
}
