package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public abstract class NbtAdapter<T extends Tag> implements ISimpleAdapter<T, Tag, JsonElement> {
   private final boolean nullable;

   public NbtAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   protected abstract void writeTagBits(T var1, BitBuffer var2);

   protected abstract T readTagBits(BitBuffer var1);

   protected abstract void writeTagBytes(T var1, ByteBuf var2);

   protected abstract T readTagBytes(ByteBuf var1);

   protected abstract void writeTagData(T var1, DataOutput var2) throws IOException;

   protected abstract T readTagData(DataInput var1) throws IOException;

   protected abstract Tag writeTagNbt(T var1);

   @Nullable
   protected abstract T readTagNbt(Tag var1);

   protected abstract JsonElement writeTagJson(T var1);

   @Nullable
   protected abstract T readTagJson(JsonElement var1);

   public void writeBits(@Nullable T value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeTagBits(value, buffer);
      }
   }

   @Override
   public Optional<T> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.readTagBits(buffer));
   }

   public void writeBytes(@Nullable T value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeTagBytes(value, buffer);
      }
   }

   @Override
   public Optional<T> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.readTagBytes(buffer));
   }

   public void writeData(@Nullable T value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeTagData(value, data);
      }
   }

   @Override
   public Optional<T> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(this.readTagData(data));
   }

   public Optional<Tag> writeNbt(@Nullable T value) {
      return value == null ? Optional.empty() : Optional.ofNullable(this.writeTagNbt(value));
   }

   @Override
   public Optional<T> readNbt(@Nullable Tag nbt) {
      return nbt == null ? Optional.empty() : Optional.ofNullable(this.readTagNbt(nbt));
   }

   public Optional<JsonElement> writeJson(@Nullable T value) {
      return value == null ? Optional.empty() : Optional.ofNullable(this.writeTagJson(value));
   }

   @Override
   public Optional<T> readJson(@Nullable JsonElement json) {
      return json != null && !(json instanceof JsonNull) ? Optional.ofNullable(this.readTagJson(json)) : Optional.empty();
   }
}
