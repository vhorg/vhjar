package iskallia.vault.core.world.template.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.iterator.MappingIterator;
import iskallia.vault.core.vault.VaultRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class IndirectTemplateEntry implements TemplateEntry {
   protected ResourceLocation reference;
   protected List<ResourceLocation> palettes;

   public IndirectTemplateEntry() {
      this(null, new ArrayList<>());
   }

   public IndirectTemplateEntry(ResourceLocation reference, List<ResourceLocation> palettes) {
      this.reference = reference;
      this.palettes = new ArrayList<>(palettes);
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
      Adapters.IDENTIFIER.writeBits(this.reference, buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.palettes.size()), buffer);

      for (ResourceLocation palette : this.palettes) {
         Adapters.IDENTIFIER.writeBits(palette, buffer);
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.reference = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElse(0);
      this.palettes = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         this.palettes.add(Adapters.IDENTIFIER.readBits(buffer).orElseThrow());
      }
   }

   @Override
   public Optional<Tag> writeNbt() {
      if (this.palettes.isEmpty()) {
         return Adapters.IDENTIFIER.writeNbt(this.reference);
      } else {
         CompoundTag nbt = new CompoundTag();
         Adapters.IDENTIFIER.writeNbt(this.reference).ifPresent(value -> nbt.put("id", value));
         ListTag list = new ListTag();

         for (ResourceLocation palette : this.palettes) {
            Adapters.IDENTIFIER.writeNbt(palette).ifPresent(list::add);
         }

         nbt.put("palettes", list);
         return Optional.of(nbt);
      }
   }

   @Override
   public void readNbt(Tag nbt) {
      if (nbt instanceof CompoundTag compound) {
         Adapters.IDENTIFIER.readNbt(compound.get("id")).ifPresent(value -> this.reference = value);
         if (!compound.contains("palettes")) {
            return;
         }

         ListTag list = compound.getList("palettes", 8);
         this.palettes = new ArrayList<>();

         for (Tag tag : list) {
            Adapters.IDENTIFIER.readNbt(tag).ifPresent(this.palettes::add);
         }
      } else {
         if (!(nbt instanceof StringTag string)) {
            throw new UnsupportedOperationException();
         }

         this.reference = new ResourceLocation(string.getAsString());
      }
   }

   @Override
   public Optional<JsonElement> writeJson() {
      if (this.palettes.isEmpty()) {
         return Adapters.IDENTIFIER.writeJson(this.reference);
      } else {
         JsonObject json = new JsonObject();
         Adapters.IDENTIFIER.writeJson(this.reference).ifPresent(element -> json.add("id", element));
         JsonArray array = new JsonArray();

         for (ResourceLocation palette : this.palettes) {
            Adapters.IDENTIFIER.writeJson(palette).ifPresent(array::add);
         }

         json.add("palettes", array);
         return Optional.of(json);
      }
   }

   @Override
   public void readJson(JsonElement json) {
      if (json instanceof JsonObject object) {
         Adapters.IDENTIFIER.readJson(object.get("id")).ifPresent(value -> this.reference = value);
         if (!object.has("palettes")) {
            return;
         }

         JsonArray array = object.getAsJsonArray("palettes");
         if (array == null) {
            return;
         }

         this.palettes = new ArrayList<>();

         for (JsonElement element : array) {
            Adapters.IDENTIFIER.readJson(element).ifPresent(this.palettes::add);
         }
      } else {
         if (!(json instanceof JsonPrimitive element)) {
            throw new UnsupportedOperationException();
         }

         Adapters.IDENTIFIER.readJson(element).ifPresent(value -> this.reference = value);
      }
   }

   @Override
   public TemplateEntry flatten(Version version, RandomSource random) {
      TemplateEntry flattened = this.getReference().get(version).getRandom(random).orElse(null);
      if (flattened == null) {
         return null;
      } else {
         flattened = flattened.copy().flatten(version, random);
         flattened.addPalettes(this.palettes);
         return flattened;
      }
   }

   @Override
   public boolean validate() {
      return VaultRegistry.TEMPLATE_POOL.getKey(this.reference) != null;
   }

   @Override
   public TemplateEntry copy() {
      return new IndirectTemplateEntry(this.reference, new ArrayList<>(this.palettes));
   }

   @Override
   public String toString() {
      return "{reference=" + (this.reference == null ? "null" : this.reference.toString()) + "}";
   }
}
