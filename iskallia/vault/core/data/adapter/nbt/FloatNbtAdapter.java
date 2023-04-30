package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class FloatNbtAdapter extends NbtAdapter<FloatTag> {
   public FloatNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public FloatNbtAdapter asNullable() {
      return new FloatNbtAdapter(this.isNullable());
   }

   protected void writeTagBits(FloatTag value, BitBuffer buffer) {
      Adapters.FLOAT.writeBits(Float.valueOf(value.getAsFloat()), buffer);
   }

   protected FloatTag readTagBits(BitBuffer buffer) {
      return FloatTag.valueOf(Adapters.FLOAT.readBits(buffer).orElseThrow());
   }

   protected void writeTagBytes(FloatTag value, ByteBuf buffer) {
      Adapters.FLOAT.writeBytes(Float.valueOf(value.getAsFloat()), buffer);
   }

   protected FloatTag readTagBytes(ByteBuf buffer) {
      return FloatTag.valueOf(Adapters.FLOAT.readBytes(buffer).orElseThrow());
   }

   protected void writeTagData(FloatTag value, DataOutput data) throws IOException {
      Adapters.FLOAT.writeData(Float.valueOf(value.getAsFloat()), data);
   }

   protected FloatTag readTagData(DataInput data) throws IOException {
      return FloatTag.valueOf(Adapters.FLOAT.readData(data).orElseThrow());
   }

   protected Tag writeTagNbt(FloatTag value) {
      return value;
   }

   @Nullable
   protected FloatTag readTagNbt(Tag nbt) {
      return nbt instanceof FloatTag tag ? tag : null;
   }

   protected JsonElement writeTagJson(FloatTag value) {
      return Adapters.FLOAT.writeJson(Float.valueOf(value.getAsFloat())).orElseThrow();
   }

   @Nullable
   protected FloatTag readTagJson(JsonElement json) {
      return Adapters.FLOAT.readJson(json).<FloatTag>map(FloatTag::valueOf).orElse(null);
   }
}
