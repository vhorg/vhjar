package iskallia.vault.core.data.key;

import net.minecraft.resources.ResourceLocation;

public class NamedKey<K extends NamedKey<K, T>, T> extends VersionedKey<K, T> {
   protected String name;

   protected NamedKey(ResourceLocation id, String name) {
      super(id);
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
