package iskallia.vault.core.data.adapter.basic;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.primitive.BoundedIntAdapter;
import iskallia.vault.core.data.adapter.primitive.IntAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.function.ToIntFunction;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class OrdinalAdapter<T> implements ISimpleAdapter<T, Tag, JsonElement> {
   private final ToIntFunction<T> mapper;
   private final boolean nullable;
   private final T[] array;
   private final IntAdapter intAdapter;

   public OrdinalAdapter(ToIntFunction<T> mapper, boolean nullable, T... array) {
      this.mapper = mapper;
      this.nullable = nullable;
      this.array = array;
      this.intAdapter = new BoundedIntAdapter(0, array.length - 1, false);
   }

   public OrdinalAdapter<T> asNullable() {
      return new OrdinalAdapter<>(this.mapper, true, this.array);
   }

   @Override
   public void writeBits(@Nullable T value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeOrdinal(value, this.mapper, this.array);
      }
   }

   @Override
   public Optional<T> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(buffer.readOrdinal(this.array));
   }

   @Override
   public void writeBytes(@Nullable T value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.intAdapter.writeBytes(Integer.valueOf(this.mapper.applyAsInt(value)), buffer);
      }
   }

   @Override
   public Optional<T> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.array[this.intAdapter.readBytes(buffer).orElseThrow()]);
   }

   @Override
   public void writeData(@Nullable T value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         this.intAdapter.writeData(Integer.valueOf(this.mapper.applyAsInt(value)), data);
      }
   }

   @Override
   public Optional<T> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(this.array[this.intAdapter.readData(data).orElseThrow()]);
   }

   @Override
   public Optional<Tag> writeNbt(@Nullable T value) {
      return value == null ? Optional.empty() : this.intAdapter.writeNbt(Integer.valueOf(this.mapper.applyAsInt(value)));
   }

   @Override
   public Optional<T> readNbt(@Nullable Tag nbt) {
      return this.intAdapter.readNbt(nbt).map(i -> this.array[i]);
   }

   @Override
   public Optional<JsonElement> writeJson(@Nullable T value) {
      return value == null ? Optional.empty() : this.intAdapter.writeJson(Integer.valueOf(this.mapper.applyAsInt(value)));
   }

   @Override
   public Optional<T> readJson(@Nullable JsonElement json) {
      return this.intAdapter.readJson(json).map(i -> this.array[i]);
   }
}
