package iskallia.vault.task.condition;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.task.ProgressConfiguredTask;
import iskallia.vault.task.TaskContext;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class TaskCondition<C extends TaskCondition.Config> implements ISerializable<CompoundTag, JsonObject> {
   private final C config;

   protected TaskCondition(C config) {
      this.config = config;
   }

   public C getConfig() {
      return this.config;
   }

   public abstract boolean isConditionFulfilled(ProgressConfiguredTask<?, ?> var1, TaskContext var2);

   @Override
   public void writeBits(BitBuffer buffer) {
      this.config.writeBits(buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.config.readBits(buffer);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return this.config.writeNbt();
   }

   public void readNbt(CompoundTag nbt) {
      this.config.readNbt(nbt);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return this.config.writeJson();
   }

   public void readJson(JsonObject json) {
      this.config.readJson(json);
   }

   public static class Adapter extends TypeSupplierAdapter<TaskCondition<?>> {
      public Adapter() {
         super("type", true);
         this.register("room_generation", RoomGenerationCondition.class, RoomGenerationCondition::new);
         this.register("items_available", ItemsAvailableCondition.class, ItemsAvailableCondition::new);
      }
   }

   public abstract static class Config implements ISerializable<CompoundTag, JsonObject> {
      @Override
      public abstract void writeBits(BitBuffer var1);

      @Override
      public abstract void readBits(BitBuffer var1);

      @Override
      public abstract Optional<CompoundTag> writeNbt();

      public abstract void readNbt(CompoundTag var1);

      @Override
      public abstract Optional<JsonObject> writeJson();

      public abstract void readJson(JsonObject var1);
   }
}
