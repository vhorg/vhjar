package iskallia.vault.core.data.adapter.primitive;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;

public abstract class NumberAdapter<N extends Number> implements ISimpleAdapter<N, Tag, JsonElement> {
   private final boolean nullable;

   public NumberAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   protected abstract void writeNumberBits(N var1, BitBuffer var2);

   protected abstract N readNumberBits(BitBuffer var1);

   protected abstract void writeNumberBytes(N var1, ByteBuf var2);

   protected abstract N readNumberBytes(ByteBuf var1);

   protected abstract void writeNumberData(N var1, DataOutput var2) throws IOException;

   protected abstract N readNumberData(DataInput var1) throws IOException;

   protected abstract Tag writeNumberNbt(N var1);

   @Nullable
   protected abstract N readNumberNbt(Tag var1);

   protected abstract JsonElement writeNumberJson(N var1);

   @Nullable
   protected abstract N readNumberJson(JsonElement var1);

   public final void writeBits(@Nullable N value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeNumberBits(value, buffer);
      }
   }

   @Override
   public final Optional<N> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.readNumberBits(buffer));
   }

   public final void writeBytes(@Nullable N value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeNumberBytes(value, buffer);
      }
   }

   @Override
   public final Optional<N> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.readNumberBytes(buffer));
   }

   public void writeData(@Nullable N value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeNumberData(value, data);
      }
   }

   @Override
   public Optional<N> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(this.readNumberData(data));
   }

   public final Optional<Tag> writeNbt(@Nullable N value) {
      return value == null ? Optional.empty() : Optional.of(this.writeNumberNbt(value));
   }

   @Override
   public final Optional<N> readNbt(@Nullable Tag nbt) {
      return nbt == null ? Optional.empty() : Optional.ofNullable(this.readNumberNbt(nbt));
   }

   public final Optional<JsonElement> writeJson(@Nullable N value) {
      return value == null ? Optional.empty() : Optional.of(this.writeNumberJson(value));
   }

   @Override
   public final Optional<N> readJson(@Nullable JsonElement json) {
      return json != null && !(json instanceof JsonNull) ? Optional.ofNullable(this.readNumberJson(json)) : Optional.empty();
   }
}
