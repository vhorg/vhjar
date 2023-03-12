package iskallia.vault.core.data.adapter.basic;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class VoidAdapter<T> implements ISimpleAdapter<T, Tag, JsonElement> {
   @Override
   public void writeBits(@Nullable T value, BitBuffer buffer) {
   }

   @Override
   public Optional<T> readBits(BitBuffer buffer) {
      return Optional.empty();
   }

   @Override
   public void writeBytes(@Nullable T value, ByteBuf buffer) {
   }

   @Override
   public Optional<T> readBytes(ByteBuf buffer) {
      return Optional.empty();
   }

   @Override
   public void writeData(@Nullable T value, DataOutput data) throws IOException {
   }

   @Override
   public Optional<T> readData(DataInput data) throws IOException {
      return Optional.empty();
   }

   @Override
   public Optional<Tag> writeNbt(@Nullable T value) {
      return Optional.empty();
   }

   @Override
   public Optional<T> readNbt(@Nullable Tag nbt) {
      return Optional.empty();
   }

   @Override
   public Optional<JsonElement> writeJson(@Nullable T value) {
      return Optional.empty();
   }

   @Override
   public Optional<T> readJson(@Nullable JsonElement json) {
      return Optional.empty();
   }
}
