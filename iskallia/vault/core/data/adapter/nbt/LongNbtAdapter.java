package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class LongNbtAdapter extends NbtAdapter<LongTag> {
   public LongNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public LongNbtAdapter asNullable() {
      return new LongNbtAdapter(this.isNullable());
   }

   protected void writeTagBits(LongTag value, BitBuffer buffer) {
      Adapters.LONG.writeBits(Long.valueOf(value.getAsLong()), buffer);
   }

   protected LongTag readTagBits(BitBuffer buffer) {
      return LongTag.valueOf(Adapters.LONG.readBits(buffer).orElseThrow());
   }

   protected void writeTagBytes(LongTag value, ByteBuf buffer) {
      Adapters.LONG.writeBytes(Long.valueOf(value.getAsLong()), buffer);
   }

   protected LongTag readTagBytes(ByteBuf buffer) {
      return LongTag.valueOf(Adapters.LONG.readBytes(buffer).orElseThrow());
   }

   protected void writeTagData(LongTag value, DataOutput data) throws IOException {
      Adapters.LONG.writeData(Long.valueOf(value.getAsLong()), data);
   }

   protected LongTag readTagData(DataInput data) throws IOException {
      return LongTag.valueOf(Adapters.LONG.readData(data).orElseThrow());
   }

   protected Tag writeTagNbt(LongTag value) {
      return value;
   }

   @Nullable
   protected LongTag readTagNbt(Tag nbt) {
      return nbt instanceof LongTag tag ? tag : null;
   }

   protected JsonElement writeTagJson(LongTag value) {
      return Adapters.LONG.writeJson(Long.valueOf(value.getAsLong())).orElseThrow();
   }

   @Nullable
   protected LongTag readTagJson(JsonElement json) {
      return Adapters.LONG.readJson(json).<LongTag>map(LongTag::valueOf).orElse(null);
   }
}
