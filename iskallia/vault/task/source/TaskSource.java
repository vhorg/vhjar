package iskallia.vault.task.source;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public abstract class TaskSource implements ISerializable<CompoundTag, JsonObject> {
   public abstract RandomSource getRandom();

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

   public <T extends TaskSource> T copy() {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      Adapters.TASK_SOURCE.writeBits(this, buffer);
      buffer.setPosition(0);
      return (T)Adapters.TASK_SOURCE.readBits(buffer).orElseThrow();
   }

   public static class Adapter extends TypeSupplierAdapter<TaskSource> {
      public Adapter() {
         super("type", false);
         this.register("entity", EntityTaskSource.class, EntityTaskSource::empty);
      }
   }

   public static class NbtAdapter implements ISimpleAdapter<TaskSource, Tag, JsonElement> {
      public void writeBits(@Nullable TaskSource source, BitBuffer buffer) {
         Adapters.GENERIC_NBT.asNullable().writeBits(this.writeNbt(source).orElse(null), buffer);
      }

      @Override
      public Optional<TaskSource> readBits(BitBuffer buffer) {
         return Adapters.GENERIC_NBT.asNullable().readBits(buffer).flatMap(this::readNbt);
      }

      public Optional<Tag> writeNbt(@Nullable TaskSource source) {
         return Adapters.TASK_SOURCE.writeNbt(source);
      }

      @Override
      public Optional<TaskSource> readNbt(@Nullable Tag nbt) {
         return Adapters.TASK_SOURCE.readNbt(nbt);
      }

      public Optional<JsonElement> writeJson(@Nullable TaskSource source) {
         return Adapters.GENERIC_NBT.asNullable().writeJson(this.writeNbt(source).orElse(null));
      }

      @Override
      public Optional<TaskSource> readJson(@Nullable JsonElement json) {
         return Adapters.GENERIC_NBT.asNullable().readJson(json).flatMap(this::readNbt);
      }
   }
}
