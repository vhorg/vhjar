package iskallia.vault.core.data.adapter.util;

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
import java.util.Optional;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class IdentifierAdapter implements ISimpleAdapter<ResourceLocation, Tag, JsonElement> {
   private final boolean nullable;

   public IdentifierAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public IdentifierAdapter asNullable() {
      return new IdentifierAdapter(true);
   }

   public void writeBits(@Nullable ResourceLocation value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeIdentifier(value);
      }
   }

   @Override
   public Optional<ResourceLocation> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(buffer.readIdentifier());
   }

   public void writeBytes(@Nullable ResourceLocation value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         String string = value.getNamespace().equals("minecraft") ? value.getPath() : value.toString();
         Adapters.UTF_8.writeBytes(string, buffer);
      }
   }

   @Override
   public Optional<ResourceLocation> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(new ResourceLocation(Adapters.UTF_8.readBytes(buffer).orElseThrow()));
   }

   public void writeData(@Nullable ResourceLocation value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         String string = value.getNamespace().equals("minecraft") ? value.getPath() : value.toString();
         Adapters.UTF_8.writeData(string, data);
      }
   }

   @Override
   public Optional<ResourceLocation> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(new ResourceLocation(Adapters.UTF_8.readData(data).orElseThrow()));
   }

   public Optional<Tag> writeNbt(@Nullable ResourceLocation value) {
      if (value == null) {
         return Optional.empty();
      } else {
         String string = value.getNamespace().equals("minecraft") ? value.getPath() : value.toString();
         return Optional.of(StringTag.valueOf(string));
      }
   }

   @Override
   public Optional<ResourceLocation> readNbt(@Nullable Tag nbt) {
      if (nbt instanceof StringTag string) {
         return Optional.ofNullable(ResourceLocation.tryParse(string.getAsString()));
      } else if (nbt instanceof CollectionTag<?> array && array.size() == 1) {
         return this.readNbt((Tag)array.get(0));
      } else if (nbt instanceof CollectionTag<?> array && array.size() == 2) {
         String namespace = Adapters.UTF_8.readNbt((Tag)array.get(0)).orElse(null);
         String path = Adapters.UTF_8.readNbt((Tag)array.get(1)).orElse(null);
         return namespace != null && path != null ? Optional.ofNullable(ResourceLocation.tryParse(namespace + ":" + path)) : Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   public Optional<JsonElement> writeJson(@Nullable ResourceLocation value) {
      if (value == null) {
         return Optional.empty();
      } else {
         String string = value.getNamespace().equals("minecraft") ? value.getPath() : value.toString();
         return Optional.of(new JsonPrimitive(string));
      }
   }

   @Override
   public Optional<ResourceLocation> readJson(@Nullable JsonElement json) {
      if (json instanceof JsonPrimitive primitive && primitive.isString()) {
         return Optional.ofNullable(ResourceLocation.tryParse(primitive.getAsString()));
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readJson(array.get(0));
      } else if (json instanceof JsonArray array && array.size() == 2) {
         String namespace = Adapters.UTF_8.readJson(array.get(0)).orElse(null);
         String path = Adapters.UTF_8.readJson(array.get(1)).orElse(null);
         return namespace != null && path != null ? Optional.ofNullable(ResourceLocation.tryParse(namespace + ":" + path)) : Optional.empty();
      } else {
         return Optional.empty();
      }
   }
}
