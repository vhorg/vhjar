package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class ByteNbtAdapter extends NbtAdapter<ByteTag> {
   public ByteNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public ByteNbtAdapter asNullable() {
      return new ByteNbtAdapter(this.isNullable());
   }

   protected void writeTagBits(ByteTag value, BitBuffer buffer) {
      Adapters.BYTE.writeBits(Byte.valueOf(value.getAsByte()), buffer);
   }

   protected ByteTag readTagBits(BitBuffer buffer) {
      return ByteTag.valueOf(Adapters.BYTE.readBits(buffer).orElseThrow());
   }

   protected void writeTagBytes(ByteTag value, ByteBuf buffer) {
      Adapters.BYTE.writeBytes(Byte.valueOf(value.getAsByte()), buffer);
   }

   protected ByteTag readTagBytes(ByteBuf buffer) {
      return ByteTag.valueOf(Adapters.BYTE.readBytes(buffer).orElseThrow());
   }

   protected void writeTagData(ByteTag value, DataOutput data) throws IOException {
      Adapters.BYTE.writeData(Byte.valueOf(value.getAsByte()), data);
   }

   protected ByteTag readTagData(DataInput data) throws IOException {
      return ByteTag.valueOf(Adapters.BYTE.readData(data).orElseThrow());
   }

   protected Tag writeTagNbt(ByteTag value) {
      return value;
   }

   @Nullable
   protected ByteTag readTagNbt(Tag nbt) {
      return nbt instanceof ByteTag tag ? tag : null;
   }

   protected JsonElement writeTagJson(ByteTag value) {
      return Adapters.BYTE.writeJson(Byte.valueOf(value.getAsByte())).orElseThrow();
   }

   @Nullable
   protected ByteTag readTagJson(JsonElement json) {
      return Adapters.BYTE.readJson(json).<ByteTag>map(ByteTag::valueOf).orElse(null);
   }
}
