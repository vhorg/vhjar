package iskallia.vault.item.crystal.data.adapter;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;

public interface IAdapter<T, N extends Tag, J extends JsonElement, C>
   extends IBitAdapter<T, C>,
   IByteAdapter<T, C>,
   IDataAdapter<T, C>,
   INbtAdapter<T, N, C>,
   IJsonAdapter<T, J, C> {
   @Override
   default void writeBits(@Nullable T value, BitBuffer buffer, C context) {
      throw new UnsupportedOperationException();
   }

   @Override
   default Optional<T> readBits(BitBuffer buffer, C context) {
      throw new UnsupportedOperationException();
   }

   @Override
   default void writeBytes(@Nullable T value, ByteBuf buffer, C context) {
      throw new UnsupportedOperationException();
   }

   @Override
   default Optional<T> readBytes(ByteBuf buffer, C context) {
      throw new UnsupportedOperationException();
   }

   @Override
   default void writeData(@Nullable T value, DataOutput data, C context) throws IOException {
      throw new UnsupportedOperationException();
   }

   @Override
   default Optional<T> readData(DataInput data, C context) throws IOException {
      throw new UnsupportedOperationException();
   }

   @Override
   default Optional<N> writeNbt(@Nullable T value, C context) {
      throw new UnsupportedOperationException();
   }

   @Override
   default Optional<T> readNbt(@Nullable N nbt, C context) {
      throw new UnsupportedOperationException();
   }

   @Override
   default Optional<J> writeJson(@Nullable T value, C context) {
      throw new UnsupportedOperationException();
   }

   @Override
   default Optional<T> readJson(@Nullable J json, C context) {
      throw new UnsupportedOperationException();
   }
}
