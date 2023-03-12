package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.IntTag;
import org.jetbrains.annotations.Nullable;

public class IntTagAdapter extends NbtAdapter<IntTag> {
   public IntTagAdapter(boolean nullable) {
      super(IntTag.class, nullable);
   }

   public IntTagAdapter asNullable() {
      return new IntTagAdapter(this.isNullable());
   }

   protected void writeTagBits(IntTag value, BitBuffer buffer) {
      Adapters.INT.writeBits(Integer.valueOf(value.getAsInt()), buffer);
   }

   protected IntTag readTagBits(BitBuffer buffer) {
      return IntTag.valueOf(Adapters.INT.readBits(buffer).orElseThrow());
   }

   protected void writeTagBytes(IntTag value, ByteBuf buffer) {
      Adapters.INT.writeBytes(Integer.valueOf(value.getAsInt()), buffer);
   }

   protected IntTag readTagBytes(ByteBuf buffer) {
      return IntTag.valueOf(Adapters.INT.readBytes(buffer).orElseThrow());
   }

   protected void writeTagData(IntTag value, DataOutput data) throws IOException {
      value.write(data);
      Adapters.INT.writeData(Integer.valueOf(value.getAsInt()), data);
   }

   protected IntTag readTagData(DataInput data) throws IOException {
      return IntTag.valueOf(Adapters.INT.readData(data).orElseThrow());
   }

   @Nullable
   protected JsonElement writeTagJson(IntTag value) {
      return Adapters.INT.writeJson(Integer.valueOf(value.getAsInt())).orElseThrow();
   }

   @Nullable
   protected IntTag readTagJson(JsonElement json) {
      return Adapters.INT.readJson(json).<IntTag>map(IntTag::valueOf).orElse(null);
   }
}
