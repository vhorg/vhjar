package iskallia.vault.core.world.template.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.iterator.MappingIterator;
import iskallia.vault.core.vault.VaultRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public class DirectTemplateEntry implements TemplateEntry {
   protected ResourceLocation template;
   protected List<ResourceLocation> palettes;

   public DirectTemplateEntry() {
      this(null, new ArrayList<>());
   }

   public DirectTemplateEntry(ResourceLocation template, List<ResourceLocation> palettes) {
      this.template = template;
      this.palettes = palettes;
   }

   @Override
   public TemplateKey getTemplate() {
      return VaultRegistry.TEMPLATE.getKey(this.template);
   }

   @Override
   public Iterable<PaletteKey> getPalettes() {
      return () -> new MappingIterator<>(this.palettes.iterator(), id -> VaultRegistry.PALETTE.getKey(id));
   }

   @Override
   public Optional<JsonElement> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.IDENTIFIER.writeJson(this.template).ifPresent(element -> json.add("template", element));
      JsonArray array = new JsonArray();

      for (ResourceLocation palette : this.palettes) {
         Adapters.IDENTIFIER.writeJson(palette).ifPresent(array::add);
      }

      json.add("palettes", array);
      return Optional.of(json);
   }

   @Override
   public void readJson(JsonElement json) {
      if (json instanceof JsonObject object) {
         Adapters.IDENTIFIER.readJson(object.get("template")).ifPresent(value -> this.template = value);
         if (object.has("palettes")) {
            JsonArray array = object.getAsJsonArray("palettes");
            if (array != null) {
               this.palettes = new ArrayList<>();

               for (JsonElement element : array) {
                  Adapters.IDENTIFIER.readJson(element).ifPresent(this.palettes::add);
               }
            }
         }
      }
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

      for (ResourceLocation palette : this.palettes) {
         sb.append(palette).append(',');
      }

      sb.append("]}");
      return sb.toString();
   }
}
