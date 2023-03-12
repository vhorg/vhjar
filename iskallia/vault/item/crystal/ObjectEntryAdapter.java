package iskallia.vault.item.crystal;

import com.google.gson.JsonObject;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class ObjectEntryAdapter<T extends ISerializable<CompoundTag, JsonObject>> extends EntryAdapter<T, CompoundTag, JsonObject, ObjectEntryAdapter<T>> {
   private final String key;

   public ObjectEntryAdapter(String key) {
      this.key = key;
   }

   public Optional<CompoundTag> writeNbt(T value) {
      return value != null && this.getId(value) != null ? value.writeNbt().map(nbt -> {
         nbt.putString(this.key, this.getId(value));
         return (CompoundTag)nbt;
      }) : Optional.empty();
   }

   public Optional<T> readNbt(CompoundTag nbt) {
      if (nbt != null && nbt.get(this.key) != null) {
         T value = this.getValue(nbt.getString(this.key));
         if (value != null) {
            value.readNbt(nbt);
         }

         return Optional.ofNullable(value);
      } else {
         return Optional.empty();
      }
   }

   public Optional<JsonObject> writeJson(T value) {
      return value != null && this.getId(value) != null ? value.writeJson().map(json -> {
         json.addProperty(this.key, this.getId(value));
         return (JsonObject)json;
      }) : Optional.empty();
   }

   public Optional<T> readJson(JsonObject json) {
      if (json != null && json.has(this.key)) {
         T value = this.getValue(json.get(this.key).getAsString());
         if (value != null) {
            value.readJson(json);
         }

         return Optional.ofNullable(value);
      } else {
         return Optional.empty();
      }
   }
}
