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

public interface IComplexAdapter<T, N extends Tag, J extends JsonElement, C> extends IAdapter<T, N, J, C> {
   default void writeBits(@Nullable T value, BitBuffer buffer) {
      this.writeBits(value, buffer, null);
   }

   default Optional<T> readBits(BitBuffer buffer) {
      return this.readBits(buffer, null);
   }

   default void writeBytes(@Nullable T value, ByteBuf buffer) {
      this.writeBytes(value, buffer, null);
   }

   default Optional<T> readBytes(ByteBuf buffer) {
      return this.readBytes(buffer, null);
   }

   default void writeData(@Nullable T value, DataOutput data) throws IOException {
      this.writeData(value, data, null);
   }

   default Optional<T> readData(DataInput data) throws IOException {
      return this.readData(data, null);
   }

   default Optional<N> writeNbt(@Nullable T value) {
      return this.writeNbt(value, null);
   }

   default Optional<T> readNbt(@Nullable N nbt) {
      return this.readNbt(nbt, null);
   }

   default Optional<J> writeJson(@Nullable T value) {
      return this.writeJson(value, null);
   }

   default Optional<T> readJson(@Nullable J json) {
      return this.readJson(json, null);
   }

   @Override
   void writeBits(@Nullable T var1, BitBuffer var2, @Nullable C var3);

   @Override
   Optional<T> readBits(BitBuffer var1, @Nullable C var2);

   @Override
   void writeBytes(@Nullable T var1, ByteBuf var2, @Nullable C var3);

   @Override
   Optional<T> readBytes(ByteBuf var1, @Nullable C var2);

   @Override
   void writeData(@Nullable T var1, DataOutput var2, @Nullable C var3) throws IOException;

   @Override
   Optional<T> readData(DataInput var1, @Nullable C var2) throws IOException;

   @Override
   Optional<N> writeNbt(@Nullable T var1, @Nullable C var2);

   @Override
   Optional<T> readNbt(@Nullable N var1, @Nullable C var2);

   @Override
   Optional<J> writeJson(@Nullable T var1, @Nullable C var2);

   @Override
   Optional<T> readJson(@Nullable J var1, @Nullable C var2);

   @Override
   default JsonElement serialize(T value, Type source, JsonSerializationContext context) {
      return this.writeJson(value).map(json -> (JsonElement)json).orElse(JsonNull.INSTANCE);
   }

   @Override
   default T deserialize(JsonElement json, Type source, JsonDeserializationContext context) {
      return this.readJson((J)json).orElse(null);
   }
}
