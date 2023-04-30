package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class StringNbtAdapter extends NbtAdapter<StringTag> {
   public StringNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public StringNbtAdapter asNullable() {
      return new StringNbtAdapter(this.isNullable());
   }

   protected void writeTagBits(StringTag value, BitBuffer buffer) {
      Adapters.UTF_8.writeBits(value.getAsString(), buffer);
   }

   protected StringTag readTagBits(BitBuffer buffer) {
      return StringTag.valueOf(Adapters.UTF_8.readBits(buffer).orElseThrow());
   }

   protected void writeTagBytes(StringTag value, ByteBuf buffer) {
      Adapters.UTF_8.writeBytes(value.getAsString(), buffer);
   }

   protected StringTag readTagBytes(ByteBuf buffer) {
      return StringTag.valueOf(Adapters.UTF_8.readBytes(buffer).orElseThrow());
   }

   protected void writeTagData(StringTag value, DataOutput data) throws IOException {
      Adapters.UTF_8.writeData(value.getAsString(), data);
   }

   protected StringTag readTagData(DataInput data) throws IOException {
      return StringTag.valueOf(Adapters.UTF_8.readData(data).orElseThrow());
   }

   protected Tag writeTagNbt(StringTag value) {
      return value;
   }

   @Nullable
   protected StringTag readTagNbt(Tag nbt) {
      return nbt instanceof StringTag tag ? tag : null;
   }

   protected JsonElement writeTagJson(StringTag value) {
      return Adapters.UTF_8.writeJson(value.getAsString()).orElseThrow();
   }

   @Nullable
   protected StringTag readTagJson(JsonElement json) {
      return Adapters.UTF_8.readJson(json).<StringTag>map(StringTag::valueOf).orElse(null);
   }
}
