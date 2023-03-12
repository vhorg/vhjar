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
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;

public class CharAdapter implements ISimpleAdapter<Character, Tag, JsonElement> {
   private final boolean nullable;

   public CharAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public CharAdapter asNullable() {
      return new CharAdapter(true);
   }

   public final void writeBits(@Nullable Character value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeChar(value);
      }
   }

   @Override
   public final Optional<Character> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(buffer.readChar());
   }

   public final void writeBytes(@Nullable Character value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeChar(value);
      }
   }

   @Override
   public final Optional<Character> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(buffer.readChar());
   }

   public void writeData(Character value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         data.writeChar(value);
      }
   }

   @Override
   public Optional<Character> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(data.readChar());
   }

   public final Optional<Tag> writeNbt(@Nullable Character value) {
      return value == null ? Optional.empty() : Optional.of(ShortTag.valueOf((short)value.charValue()));
   }

   @Override
   public final Optional<Character> readNbt(@Nullable Tag nbt) {
      return nbt instanceof ShortTag tag ? Optional.of((char)Short.toUnsignedInt(tag.getAsShort())) : Optional.empty();
   }

   public final Optional<JsonElement> writeJson(@Nullable Character value) {
      return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value));
   }

   @Override
   public final Optional<Character> readJson(@Nullable JsonElement json) {
      if (json instanceof JsonArray array && array.size() == 1) {
         return this.readJson(array.get(0));
      } else {
         return json instanceof JsonPrimitive primitive && primitive.isString() ? Optional.of(primitive.getAsString().charAt(0)) : Optional.empty();
      }
   }
}
