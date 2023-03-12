package iskallia.vault.core.data.adapter.basic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class StringAdapter implements ISimpleAdapter<String, Tag, JsonElement> {
   private final Charset charset;
   private final boolean nullable;

   public StringAdapter(Charset charset, boolean nullable) {
      this.charset = charset;
      this.nullable = nullable;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public StringAdapter asNullable() {
      return new StringAdapter(this.charset, true);
   }

   public void writeBits(@Nullable String value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeString(value, this.charset);
      }
   }

   @Override
   public Optional<String> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(buffer.readString(this.charset));
   }

   public void writeBytes(@Nullable String value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         byte[] bytes = value.getBytes(this.charset);
         Adapters.INT_SEGMENTED_7.writeBytes(Integer.valueOf(bytes.length), buffer);
         buffer.writeBytes(bytes);
      }
   }

   @Override
   public Optional<String> readBytes(ByteBuf buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         byte[] bytes = new byte[Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow()];
         buffer.readBytes(bytes);
         return Optional.of(new String(bytes, this.charset));
      }
   }

   public void writeData(@Nullable String value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         byte[] bytes = value.getBytes(this.charset);
         Adapters.INT_SEGMENTED_7.writeData(Integer.valueOf(bytes.length), data);

         for (byte b : bytes) {
            data.writeByte(b);
         }
      }
   }

   @Override
   public Optional<String> readData(DataInput data) throws IOException {
      if (this.nullable && data.readBoolean()) {
         return Optional.empty();
      } else {
         byte[] bytes = new byte[Adapters.INT_SEGMENTED_7.readData(data).orElseThrow()];

         for (int i = 0; i < bytes.length; i++) {
            bytes[i] = data.readByte();
         }

         return Optional.of(new String(bytes, this.charset));
      }
   }

   public Optional<Tag> writeNbt(@Nullable String value) {
      return value == null ? Optional.empty() : Optional.of(StringTag.valueOf(value));
   }

   @Override
   public Optional<String> readNbt(@Nullable Tag nbt) {
      return nbt instanceof StringTag string ? Optional.of(string.getAsString()) : Optional.empty();
   }

   public Optional<JsonElement> writeJson(@Nullable String value) {
      return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value));
   }

   @Override
   public Optional<String> readJson(@Nullable JsonElement json) {
      if (json instanceof JsonArray array && array.size() == 1) {
         return this.readJson(array.get(0));
      } else {
         return json instanceof JsonPrimitive primitive ? Optional.of(primitive.getAsString()) : Optional.empty();
      }
   }
}
