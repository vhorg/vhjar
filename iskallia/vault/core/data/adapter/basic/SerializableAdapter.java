package iskallia.vault.core.data.adapter.basic;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class SerializableAdapter<T extends ISerializable<N, J>, N extends Tag, J extends JsonElement> implements ISimpleAdapter<T, N, J> {
   private final Supplier<T> constructor;
   private final boolean nullable;

   public SerializableAdapter(Supplier<T> constructor, boolean nullable) {
      this.constructor = constructor;
      this.nullable = nullable;
   }

   public Supplier<T> getConstructor() {
      return this.constructor;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public SerializableAdapter<T, N, J> asNullable() {
      return new SerializableAdapter<>(this.constructor, true);
   }

   public void writeBits(@Nullable T value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         value.writeBits(buffer);
      }
   }

   @Override
   public Optional<T> readBits(BitBuffer buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         T value = this.constructor.get();
         value.readBits(buffer);
         return Optional.of(value);
      }
   }

   public void writeBytes(@Nullable T value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         value.writeBytes(buffer);
      }
   }

   @Override
   public Optional<T> readBytes(ByteBuf buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         T value = this.constructor.get();
         value.readBytes(buffer);
         return Optional.of(value);
      }
   }

   public void writeData(@Nullable T value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         value.writeData(data);
      }
   }

   @Override
   public Optional<T> readData(DataInput data) throws IOException {
      if (this.nullable && data.readBoolean()) {
         return Optional.empty();
      } else {
         T value = this.constructor.get();
         value.readData(data);
         return Optional.of(value);
      }
   }

   public Optional<N> writeNbt(@Nullable T value) {
      return value == null ? Optional.empty() : value.writeNbt();
   }

   @Override
   public Optional<T> readNbt(@Nullable N nbt) {
      if (nbt == null) {
         return Optional.empty();
      } else {
         T value = this.constructor.get();
         value.readNbt(nbt);
         return Optional.of(value);
      }
   }

   public Optional<J> writeJson(@Nullable T value) {
      return value == null ? Optional.empty() : value.writeJson();
   }

   @Override
   public Optional<T> readJson(@Nullable J json) {
      if (json == null) {
         return Optional.empty();
      } else {
         T value = this.constructor.get();
         value.readJson(json);
         return Optional.of(value);
      }
   }
}
