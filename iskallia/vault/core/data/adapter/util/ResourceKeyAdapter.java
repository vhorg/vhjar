package iskallia.vault.core.data.adapter.util;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public class ResourceKeyAdapter<T> implements ISimpleAdapter<ResourceKey<T>, Tag, JsonElement> {
   private final ResourceKey<Registry<T>> registry;
   private final boolean nullable;

   public ResourceKeyAdapter(ResourceKey<Registry<T>> registry, boolean nullable) {
      this.registry = registry;
      this.nullable = nullable;
   }

   public ResourceKey<Registry<T>> getRegistry() {
      return this.registry;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public ResourceKeyAdapter<T> asNullable() {
      return new ResourceKeyAdapter<>(this.registry, true);
   }

   public void writeBits(@Nullable ResourceKey<T> value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.IDENTIFIER.writeBits(value.location(), buffer);
      }
   }

   @Override
   public Optional<ResourceKey<T>> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Adapters.IDENTIFIER.readBits(buffer).map(id -> ResourceKey.create(this.registry, id));
   }

   public void writeBytes(@Nullable ResourceKey<T> value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.IDENTIFIER.writeBytes(value.location(), buffer);
      }
   }

   @Override
   public Optional<ResourceKey<T>> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Adapters.IDENTIFIER.readBytes(buffer).map(id -> ResourceKey.create(this.registry, id));
   }

   public void writeData(@Nullable ResourceKey<T> value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.IDENTIFIER.writeData(value.location(), data);
      }
   }

   @Override
   public Optional<ResourceKey<T>> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Adapters.IDENTIFIER.readData(data).map(id -> ResourceKey.create(this.registry, id));
   }

   public Optional<Tag> writeNbt(@Nullable ResourceKey<T> value) {
      return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeNbt(value.location());
   }

   @Override
   public Optional<ResourceKey<T>> readNbt(@Nullable Tag nbt) {
      return Adapters.IDENTIFIER.readNbt(nbt).map(id -> ResourceKey.create(this.registry, id));
   }

   public Optional<JsonElement> writeJson(@Nullable ResourceKey<T> value) {
      return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeJson(value.location());
   }

   @Override
   public Optional<ResourceKey<T>> readJson(@Nullable JsonElement json) {
      return Adapters.IDENTIFIER.readJson(json).map(id -> ResourceKey.create(this.registry, id));
   }
}
