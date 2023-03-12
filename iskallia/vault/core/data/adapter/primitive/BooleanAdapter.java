package iskallia.vault.core.data.adapter.primitive;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;

public class BooleanAdapter implements ISimpleAdapter<Boolean, Tag, JsonElement> {
   private final boolean nullable;

   public BooleanAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public BooleanAdapter asNullable() {
      return new BooleanAdapter(true);
   }

   public final void writeBits(@Nullable Boolean value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeBoolean(value);
      }
   }

   @Override
   public final Optional<Boolean> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(buffer.readBoolean());
   }

   public final void writeBytes(@Nullable Boolean value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeBoolean(value);
      }
   }

   @Override
   public final Optional<Boolean> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(buffer.readBoolean());
   }

   public void writeData(@Nullable Boolean value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         data.writeBoolean(value);
      }
   }

   @Override
   public Optional<Boolean> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(data.readBoolean());
   }

   public final Optional<Tag> writeNbt(@Nullable Boolean value) {
      return value == null ? Optional.empty() : Optional.of(ByteTag.valueOf(value));
   }

   @Override
   public final Optional<Boolean> readNbt(@Nullable Tag nbt) {
      return nbt instanceof ByteTag tag ? Optional.of(tag.getAsByte() != 0) : Optional.empty();
   }

   public final Optional<JsonElement> writeJson(@Nullable Boolean value) {
      return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value));
   }

   @Override
   public final Optional<Boolean> readJson(@Nullable JsonElement json) {
      if (json instanceof JsonArray array && array.size() == 1) {
         return this.readJson(array.get(0));
      } else if (json instanceof JsonPrimitive primitive && !primitive.isBoolean()) {
         try {
            return Optional.of(primitive.getAsBoolean());
         } catch (NumberFormatException var5) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }
}
