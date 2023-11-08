package iskallia.vault.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.task.source.TaskSource;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public abstract class ConfiguredTask<C extends ConfiguredTask.Config> extends Task {
   private C config;
   private boolean populated;

   public ConfiguredTask() {
   }

   protected ConfiguredTask(C config) {
      this.config = config;
   }

   public C getConfig() {
      return this.config;
   }

   public boolean isPopulated() {
      return this.populated;
   }

   public abstract void onPopulate(TaskSource var1);

   @Override
   public boolean isCompleted(TaskSource source) {
      if (!this.populated && source != null) {
         this.onPopulate(source);
         this.populated = true;
      }

      return true;
   }

   @Override
   public void onAttach(TaskSource source) {
      if (!this.populated) {
         this.onPopulate(source);
         this.populated = true;
      }

      super.onAttach(source);
   }

   @Override
   public void onStart(TaskSource source) {
      if (!this.populated) {
         this.onPopulate(source);
         this.populated = true;
      }

      super.onStart(source);
   }

   @Override
   public void onTick(TaskSource source) {
      if (!this.populated) {
         this.onPopulate(source);
         this.populated = true;
      }

      super.onTick(source);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      this.config.writeBits(buffer);
      Adapters.BOOLEAN.writeBits(this.populated, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.config.readBits(buffer);
      this.populated = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         if (!this.populated) {
            CompoundTag other = this.config.writeNbt().orElseThrow();
            other.getAllKeys().forEach(key -> nbt.put(key, Objects.requireNonNull(other.get(key))));
         } else {
            nbt.put("config", (Tag)this.config.writeNbt().orElseThrow());
         }

         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      if (!nbt.contains("config")) {
         this.config.readNbt(nbt);
         this.populated = false;
      } else {
         this.config.readNbt(nbt.getCompound("config"));
         this.populated = true;
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         if (!this.populated) {
            this.config.writeJson().orElseThrow().entrySet().forEach(entry -> json.add((String)entry.getKey(), (JsonElement)entry.getValue()));
         } else {
            json.add("config", (JsonElement)this.config.writeJson().orElseThrow());
         }

         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      if (!json.has("config")) {
         this.config.readJson(json);
         this.populated = false;
      } else {
         this.config.readJson(json.getAsJsonObject("config"));
         this.populated = true;
      }
   }

   public abstract static class Config implements ISerializable<CompoundTag, JsonObject> {
      @Override
      public void writeBits(BitBuffer buffer) {
      }

      @Override
      public void readBits(BitBuffer buffer) {
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag());
      }

      public void readNbt(CompoundTag nbt) {
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return Optional.of(new JsonObject());
      }

      public void readJson(JsonObject json) {
      }
   }
}
