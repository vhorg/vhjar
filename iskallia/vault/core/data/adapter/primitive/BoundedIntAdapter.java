package iskallia.vault.core.data.adapter.primitive;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;

public class BoundedIntAdapter extends IntAdapter {
   protected final int min;
   protected final int max;
   protected final int bits;

   public BoundedIntAdapter(int min, int max, boolean nullable) {
      super(nullable);
      this.min = min;
      this.max = max;
      this.bits = 32 - Integer.numberOfLeadingZeros(this.max - this.min);
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   public int getBits() {
      return this.bits;
   }

   @Override
   protected void writeNumberBits(Integer value, BitBuffer buffer) {
      buffer.writeIntBits(value - this.min, this.bits);
   }

   @Override
   protected Integer readNumberBits(BitBuffer buffer) {
      return this.min + buffer.readIntBits(this.bits);
   }

   @Override
   protected void writeNumberBytes(Integer value, ByteBuf buffer) {
      int number = value - this.min;

      for (int offset = 0; offset < this.bits; offset += 8) {
         buffer.writeByte(number >>> offset);
      }
   }

   @Override
   protected Integer readNumberBytes(ByteBuf buffer) {
      int number = 0;

      for (int offset = 0; offset < this.bits; offset += 8) {
         number |= buffer.readByte() << offset;
      }

      return number;
   }

   @Override
   protected void writeNumberData(Integer value, DataOutput data) throws IOException {
      int number = value - this.min;

      for (int offset = 0; offset < this.bits; offset += 8) {
         data.writeByte(number >>> offset);
      }
   }

   @Override
   protected Integer readNumberData(DataInput data) throws IOException {
      int number = 0;

      for (int offset = 0; offset < this.bits; offset += 8) {
         number |= data.readByte() << offset;
      }

      return number;
   }

   @Nullable
   @Override
   protected Tag writeNumberNbt(Integer value) {
      return super.writeNumberNbt(value - this.min);
   }

   @Nullable
   @Override
   protected Integer readNumberNbt(Tag nbt) {
      Integer value = super.readNumberNbt(nbt);
      return value == null ? null : value + this.min;
   }
}
