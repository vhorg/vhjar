package iskallia.vault.core.world.template.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.iterator.MappingIterator;
import iskallia.vault.core.vault.VaultRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class DirectTemplateEntry implements TemplateEntry {
   protected ResourceLocation template;
   protected List<ResourceLocation> palettes;

   public DirectTemplateEntry() {
      this(null, new ArrayList<>());
   }

   public DirectTemplateEntry(ResourceLocation template) {
      this.template = template;
      this.palettes = new ArrayList<>();
   }

   public DirectTemplateEntry(ResourceLocation template, List<ResourceLocation> palettes) {
      this.template = template;
      this.palettes = new ArrayList<>(palettes);
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
   public void addPalettes(Iterable<ResourceLocation> palettes) {
      for (ResourceLocation palette : palettes) {
         this.palettes.add(palette);
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.IDENTIFIER.writeBits(this.template, buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.palettes.size()), buffer);

      for (ResourceLocation palette : this.palettes) {
         Adapters.IDENTIFIER.writeBits(palette, buffer);
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.template = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElse(0);
      this.palettes = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         this.palettes.add(Adapters.IDENTIFIER.readBits(buffer).orElseThrow());
      }
   }

   @Override
   public Optional<Tag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.IDENTIFIER.writeNbt(this.template).ifPresent(value -> nbt.put("template", value));
      ListTag list = new ListTag();

      for (ResourceLocation palette : this.palettes) {
         Adapters.IDENTIFIER.writeNbt(palette).ifPresent(list::add);
      }

      nbt.put("palettes", list);
      return Optional.of(nbt);
   }

   @Override
   public void readNbt(Tag nbt) {
      if (nbt instanceof CompoundTag compound) {
         Adapters.IDENTIFIER.readNbt(compound.get("template")).ifPresent(value -> this.template = value);
         if (compound.contains("palettes")) {
            ListTag list = compound.getList("palettes", 8);
            this.palettes = new ArrayList<>();

            for (Tag tag : list) {
               Adapters.IDENTIFIER.readNbt(tag).ifPresent(this.palettes::add);
            }
         }
      }
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
      return this.copy();
   }

   @Override
   public boolean validate() {
      return VaultRegistry.TEMPLATE.getKey(this.template) != null;
   }

   @Override
   public TemplateEntry copy() {
      return new DirectTemplateEntry(this.template, new ArrayList<>(this.palettes));
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
