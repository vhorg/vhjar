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
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;

public abstract class SupplierAdapter<T extends ISerializable<?, ?>> implements ISimpleAdapter<T, Tag, JsonElement> {
   private final boolean nullable;

   public SupplierAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   protected abstract void writeSuppliedBits(T var1, BitBuffer var2);

   protected abstract T readSuppliedBits(BitBuffer var1);

   protected abstract void writeSuppliedBytes(T var1, ByteBuf var2);

   protected abstract T readSuppliedBytes(ByteBuf var1);

   protected abstract void writeSuppliedData(T var1, DataOutput var2) throws IOException;

   protected abstract T readSuppliedData(DataInput var1) throws IOException;

   @Nullable
   protected abstract Tag writeSuppliedNbt(T var1);

   @Nullable
   protected abstract T readSuppliedNbt(Tag var1);

   @Nullable
   protected abstract JsonElement writeSuppliedJson(T var1);

   @Nullable
   protected abstract T readSuppliedJson(JsonElement var1);

   public void writeBits(@Nullable T value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeSuppliedBits(value, buffer);
      }
   }

   @Override
   public Optional<T> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.ofNullable(this.readSuppliedBits(buffer));
   }

   public void writeBytes(@Nullable T value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeSuppliedBytes(value, buffer);
      }
   }

   @Override
   public Optional<T> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.ofNullable(this.readSuppliedBytes(buffer));
   }

   public void writeData(@Nullable T value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeSuppliedData(value, data);
      }
   }

   @Override
   public Optional<T> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.ofNullable(this.readSuppliedData(data));
   }

   public Optional<Tag> writeNbt(@Nullable T value) {
      return value == null ? Optional.empty() : Optional.ofNullable(this.writeSuppliedNbt(value));
   }

   @Override
   public Optional<T> readNbt(@Nullable Tag nbt) {
      return nbt == null ? Optional.empty() : Optional.ofNullable(this.readSuppliedNbt(nbt));
   }

   public Optional<JsonElement> writeJson(@Nullable T value) {
      return value == null ? Optional.empty() : Optional.ofNullable(this.writeSuppliedJson(value));
   }

   @Override
   public Optional<T> readJson(@Nullable JsonElement json) {
      return json == null ? Optional.empty() : Optional.ofNullable(this.readSuppliedJson(json));
   }
}
