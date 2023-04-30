package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.number.NumberAdapter;
import iskallia.vault.core.data.adapter.number.NumericAdapter;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class NumericNbtAdapter extends NbtAdapter<NumericTag> {
   public NumericNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public NumericNbtAdapter asNullable() {
      return new NumericNbtAdapter(this.isNullable());
   }

   protected void writeTagBits(NumericTag value, BitBuffer buffer) {
      Adapters.NUMERIC.writeBits(value.getAsNumber(), buffer);
   }

   protected NumericTag readTagBits(BitBuffer buffer) {
      return NumericAdapter.wrap(Adapters.NUMERIC.readBits(buffer).orElseThrow()) instanceof NumericTag numeric ? numeric : null;
   }

   protected void writeTagBytes(NumericTag value, ByteBuf buffer) {
      Adapters.NUMERIC.writeBytes(value.getAsNumber(), buffer);
   }

   protected NumericTag readTagBytes(ByteBuf buffer) {
      return NumericAdapter.wrap(Adapters.NUMERIC.readBytes(buffer).orElseThrow()) instanceof NumericTag numeric ? numeric : null;
   }

   protected void writeTagData(NumericTag value, DataOutput data) throws IOException {
      Adapters.NUMERIC.writeData(value.getAsNumber(), data);
   }

   protected NumericTag readTagData(DataInput data) throws IOException {
      return NumericAdapter.wrap(Adapters.NUMERIC.readData(data).orElseThrow()) instanceof NumericTag numeric ? numeric : null;
   }

   protected Tag writeTagNbt(NumericTag value) {
      return value;
   }

   @Nullable
   protected NumericTag readTagNbt(Tag nbt) {
      return nbt instanceof NumericTag tag ? tag : null;
   }

   protected JsonElement writeTagJson(NumericTag value) {
      return Adapters.NUMERIC.writeJson(value.getAsNumber()).orElseThrow();
   }

   @Nullable
   protected NumericTag readTagJson(JsonElement json) {
      Tag tag = Adapters.NUMERIC.readJson(json).map(NumberAdapter::wrap).orElse(null);
      return tag instanceof NumericTag numeric ? numeric : null;
   }
}
