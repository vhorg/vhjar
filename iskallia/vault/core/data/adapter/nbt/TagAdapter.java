package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class TagAdapter extends NbtAdapter<Tag> {
   public TagAdapter(boolean nullable) {
      super(Tag.class, nullable);
   }

   public TagAdapter asNullable() {
      return new TagAdapter(true);
   }

   @Override
   protected void writeTagBits(Tag value, BitBuffer buffer) {
      buffer.writeIntBounded(value.getId(), 0, 12);
      ADAPTERS[value.getId()].writeBits(value, buffer);
   }

   @Override
   protected Tag readTagBits(BitBuffer buffer) {
      return (Tag)ADAPTERS[buffer.readIntBounded(0, 12)].readBits(buffer).orElseThrow();
   }

   @Override
   protected void writeTagBytes(Tag value, ByteBuf buffer) {
      buffer.writeByte(value.getId());
      ADAPTERS[value.getId()].writeBytes(value, buffer);
   }

   @Override
   protected Tag readTagBytes(ByteBuf buffer) {
      return (Tag)ADAPTERS[buffer.readByte()].readBytes(buffer).orElseThrow();
   }

   @Override
   protected void writeTagData(Tag value, DataOutput data) throws IOException {
      data.writeByte(value.getId());
      ADAPTERS[value.getId()].writeData(value, data);
   }

   @Override
   protected Tag readTagData(DataInput data) throws IOException {
      return (Tag)ADAPTERS[data.readByte()].readData(data).orElseThrow();
   }

   @Override
   protected JsonElement writeTagJson(Tag value) {
      throw new UnsupportedOperationException();
   }

   @Nullable
   @Override
   protected Tag readTagJson(JsonElement json) {
      throw new UnsupportedOperationException();
   }
}
