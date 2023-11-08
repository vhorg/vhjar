package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.source.TaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class AchievementTask extends ConfiguredTask<AchievementTask.Config> {
   private Task delegate;

   public AchievementTask() {
      super(new AchievementTask.Config());
   }

   public AchievementTask(AchievementTask.Config config, Task delegate) {
      super(config);
      this.delegate = delegate;
   }

   @Override
   public boolean isCompleted(TaskSource source) {
      return !super.isCompleted(source) ? false : this.delegate.isCompleted(source);
   }

   @Override
   public void onStart(TaskSource source) {
      super.onStart(source);
      this.delegate.onStart(source);
   }

   @Override
   public void onAttach(TaskSource source) {
      super.onAttach(source);
      this.delegate.onAttach(source);
   }

   @Override
   public void onTick(TaskSource source) {
      super.onTick(source);
      this.delegate.onTick(source);
   }

   @Override
   public void onStop(TaskSource source) {
      this.delegate.onStop(source);
      super.onStop(source);
   }

   @Override
   public void onDetach() {
      this.delegate.onDetach();
      super.onDetach();
   }

   @Override
   public void onPopulate(TaskSource source) {
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.TASK.writeBits(this.delegate, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.delegate = Adapters.TASK.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.TASK.writeNbt(this.delegate).ifPresent(value -> nbt.put("delegate", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.delegate = Adapters.TASK.readNbt(nbt.getCompound("delegate")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.TASK.writeJson(this.delegate).ifPresent(value -> json.add("delegate", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.delegate = Adapters.TASK.readJson(json.getAsJsonObject("delegate")).orElseThrow();
   }

   public static class Config extends ConfiguredTask.Config {
      private ResourceLocation id;
      private String name;
      private String description;

      public Config() {
      }

      public Config(ResourceLocation id, String name, String description) {
         this.id = id;
         this.name = name;
         this.description = description;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public String getDescription() {
         return this.description;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         buffer.writeIdentifier(this.id);
         buffer.writeString(this.name);
         buffer.writeString(this.description);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.id = buffer.readIdentifier();
         this.name = buffer.readString();
         this.description = buffer.readString();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.IDENTIFIER.writeNbt(this.id).ifPresent(value -> nbt.put("id", value));
            nbt.putString("name", this.name);
            nbt.putString("description", this.description);
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag tag) {
         super.readNbt(tag);
         this.id = Adapters.IDENTIFIER.readNbt(tag.get("id")).orElseThrow();
         this.name = tag.getString("name");
         this.description = tag.getString("description");
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = super.writeJson().orElse(new JsonObject());
         Adapters.IDENTIFIER.writeJson(this.id).ifPresent(element -> json.add("id", element));
         json.addProperty("name", this.name);
         json.addProperty("description", this.description);
         return Optional.of(json);
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         Adapters.IDENTIFIER.readJson(json.get("id")).ifPresent(id -> this.id = id);
         this.name = json.get("name").getAsString();
         this.description = json.get("description").getAsString();
      }
   }
}
