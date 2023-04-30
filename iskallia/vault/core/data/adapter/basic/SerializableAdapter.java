package iskallia.vault.core.data.adapter.basic;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.item.crystal.data.serializable.IBitSerializable;
import iskallia.vault.item.crystal.data.serializable.IByteSerializable;
import iskallia.vault.item.crystal.data.serializable.IDataSerializable;
import iskallia.vault.item.crystal.data.serializable.IJsonSerializable;
import iskallia.vault.item.crystal.data.serializable.INbtSerializable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class SerializableAdapter<T, N extends Tag, J extends JsonElement> implements ISimpleAdapter<T, N, J> {
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

   @Override
   public void writeBits(@Nullable T value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         ((IBitSerializable)value).writeBits(buffer);
      }
   }

   @Override
   public Optional<T> readBits(BitBuffer buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         T value = this.constructor.get();
         ((IBitSerializable)value).readBits(buffer);
         return Optional.of(value);
      }
   }

   @Override
   public void writeBytes(@Nullable T value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         ((IByteSerializable)value).writeBytes(buffer);
      }
   }

   @Override
   public Optional<T> readBytes(ByteBuf buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         T value = this.constructor.get();
         ((IByteSerializable)value).readBytes(buffer);
         return Optional.of(value);
      }
   }

   @Override
   public void writeData(@Nullable T value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         ((IDataSerializable)value).writeData(data);
      }
   }

   @Override
   public Optional<T> readData(DataInput data) throws IOException {
      if (this.nullable && data.readBoolean()) {
         return Optional.empty();
      } else {
         T value = this.constructor.get();
         ((IDataSerializable)value).readData(data);
         return Optional.of(value);
      }
   }

   @Override
   public Optional<N> writeNbt(@Nullable T value) {
      return value == null ? Optional.empty() : ((INbtSerializable)value).writeNbt();
   }

   @Override
   public Optional<T> readNbt(@Nullable N nbt) {
      if (nbt == null) {
         return Optional.empty();
      } else {
         T value = this.constructor.get();
         ((INbtSerializable)value).readNbt(nbt);
         return Optional.of(value);
      }
   }

   @Override
   public Optional<J> writeJson(@Nullable T value) {
      return value == null ? Optional.empty() : ((IJsonSerializable)value).writeJson();
   }

   @Override
   public Optional<T> readJson(@Nullable J json) {
      if (json == null) {
         return Optional.empty();
      } else {
         T value = this.constructor.get();
         ((IJsonSerializable)value).readJson(json);
         return Optional.of(value);
      }
   }
}
