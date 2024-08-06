package iskallia.vault.core.data.adapter.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class GsonAdapter<T> implements ISimpleAdapter<T, Tag, JsonElement> {
   private final Class<T> type;
   private final Gson gson;
   private final boolean nullable;

   public GsonAdapter(Class<T> type, Gson gson, boolean nullable) {
      this.type = type;
      this.gson = gson;
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public GsonAdapter<T> asNullable() {
      return new GsonAdapter<>(this.type, this.gson, this.nullable);
   }

   @Override
   public void writeBits(@Nullable T value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.UTF_8.writeBits(this.gson.toJson(value, this.type), buffer);
      }
   }

   @Override
   public Optional<T> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean()
         ? Optional.empty()
         : Optional.of((T)this.gson.fromJson(Adapters.UTF_8.readBits(buffer).orElseThrow(), this.type));
   }

   @Override
   public void writeBytes(@Nullable T value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.UTF_8.writeBytes(this.gson.toJson(value, this.type), buffer);
      }
   }

   @Override
   public Optional<T> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean()
         ? Optional.empty()
         : Optional.of((T)this.gson.fromJson(Adapters.UTF_8.readBytes(buffer).orElseThrow(), this.type));
   }

   @Override
   public void writeData(@Nullable T value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.UTF_8.writeData(this.gson.toJson(value, this.type), data);
      }
   }

   @Override
   public Optional<T> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean()
         ? Optional.empty()
         : Optional.of((T)this.gson.fromJson(Adapters.UTF_8.readData(data).orElseThrow(), this.type));
   }

   @Override
   public Optional<Tag> writeNbt(@Nullable T value) {
      return value == null ? Optional.empty() : Adapters.UTF_8.writeNbt(this.gson.toJson(value, this.type));
   }

   @Override
   public Optional<T> readNbt(@Nullable Tag nbt) {
      return nbt == null ? Optional.empty() : Adapters.UTF_8.readNbt(nbt).map(string -> (T)this.gson.fromJson(string, this.type));
   }

   @Override
   public Optional<JsonElement> writeJson(@Nullable T value) {
      return value == null ? Optional.empty() : Optional.of(this.gson.toJsonTree(value, this.type));
   }

   @Override
   public Optional<T> readJson(@Nullable JsonElement json) {
      return json == null ? Optional.empty() : Optional.ofNullable((T)this.gson.fromJson(json, this.type));
   }
}
