package iskallia.vault.core.world.template.data;

import com.google.gson.JsonElement;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import net.minecraft.nbt.Tag;

public interface TemplateEntry extends ISerializable<Tag, JsonElement> {
   TemplateKey getTemplate();

   Iterable<PaletteKey> getPalettes();

   TemplateEntry flatten(Version var1, RandomSource var2);

   boolean validate();
}
