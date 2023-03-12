package iskallia.vault.item.crystal.data.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;

public interface ISimpleAdapter<T, N extends Tag, J extends JsonElement> extends IAdapter<T, N, J, Object> {
   default void writeBits(@Nullable T value, BitBuffer buffer) {
      throw new UnsupportedOperationException();
   }

   default Optional<T> readBits(BitBuffer buffer) {
      throw new UnsupportedOperationException();
   }

   default void writeBytes(@Nullable T value, ByteBuf buffer) {
      throw new UnsupportedOperationException();
   }

   default Optional<T> readBytes(ByteBuf buffer) {
      throw new UnsupportedOperationException();
   }

   default void writeData(@Nullable T value, DataOutput data) throws IOException {
      throw new UnsupportedOperationException();
   }

   default Optional<T> readData(DataInput data) throws IOException {
      throw new UnsupportedOperationException();
   }

   default Optional<N> writeNbt(@Nullable T value) {
      throw new UnsupportedOperationException();
   }

   default Optional<T> readNbt(@Nullable N nbt) {
      throw new UnsupportedOperationException();
   }

   default Optional<J> writeJson(@Nullable T value) {
      throw new UnsupportedOperationException();
   }

   default Optional<T> readJson(@Nullable J json) {
      throw new UnsupportedOperationException();
   }

   @Override
   default void writeBits(@Nullable T value, BitBuffer buffer, Object context) {
      this.writeBits(value, buffer);
   }

   @Override
   default Optional<T> readBits(BitBuffer buffer, Object context) {
      return this.readBits(buffer);
   }

   @Override
   default void writeBytes(@Nullable T value, ByteBuf buffer, Object context) {
      this.writeBytes(value, buffer);
   }

   @Override
   default Optional<T> readBytes(ByteBuf buffer, Object context) {
      return this.readBytes(buffer);
   }

   @Override
   default void writeData(@Nullable T value, DataOutput data, Object context) throws IOException {
      this.writeData(value, data);
   }

   @Override
   default Optional<T> readData(DataInput data, Object context) throws IOException {
      return this.readData(data);
   }

   @Override
   default Optional<N> writeNbt(@Nullable T value, Object context) {
      return this.writeNbt(value);
   }

   @Override
   default Optional<T> readNbt(@Nullable N nbt, Object context) {
      return this.readNbt(nbt);
   }

   @Override
   default Optional<J> writeJson(@Nullable T value, Object context) {
      return this.writeJson(value);
   }

   @Override
   default Optional<T> readJson(@Nullable J json, Object context) {
      return this.readJson(json);
   }

   @Override
   default JsonElement serialize(T value, Type source, JsonSerializationContext context) {
      return this.writeJson(value, null).map(json -> (JsonElement)json).orElse(JsonNull.INSTANCE);
   }

   @Override
   default T deserialize(JsonElement json, Type source, JsonDeserializationContext context) {
      return this.readJson((J)json, null).orElse(null);
   }
}
