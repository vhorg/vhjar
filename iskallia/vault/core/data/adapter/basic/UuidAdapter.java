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
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class UuidAdapter implements ISimpleAdapter<UUID, Tag, JsonElement> {
   private final boolean nullable;

   public UuidAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public UuidAdapter asNullable() {
      return new UuidAdapter(true);
   }

   public void writeBits(@Nullable UUID value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeLong(value.getMostSignificantBits());
         buffer.writeLong(value.getLeastSignificantBits());
      }
   }

   @Override
   public Optional<UUID> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(new UUID(buffer.readLong(), buffer.readLong()));
   }

   public void writeBytes(@Nullable UUID value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeLong(value.getMostSignificantBits());
         buffer.writeLong(value.getLeastSignificantBits());
      }
   }

   @Override
   public Optional<UUID> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(new UUID(buffer.readLong(), buffer.readLong()));
   }

   public void writeData(@Nullable UUID value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         data.writeLong(value.getMostSignificantBits());
         data.writeLong(value.getLeastSignificantBits());
      }
   }

   @Override
   public Optional<UUID> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(new UUID(data.readLong(), data.readLong()));
   }

   public Optional<Tag> writeNbt(@Nullable UUID value) {
      return value == null
         ? Optional.empty()
         : Optional.of(
            new IntArrayTag(
               new int[]{
                  (int)(value.getMostSignificantBits() >>> 32),
                  (int)value.getMostSignificantBits(),
                  (int)(value.getLeastSignificantBits() >>> 32),
                  (int)value.getLeastSignificantBits()
               }
            )
         );
   }

   @Override
   public Optional<UUID> readNbt(@Nullable Tag nbt) {
      if (nbt instanceof CollectionTag<?> array && array.size() == 4) {
         return Optional.of(
            new UUID(
               (long)Adapters.INT.readNbt((Tag)array.get(0)).orElse(0).intValue() << 32
                  | Integer.toUnsignedLong(Adapters.INT.readNbt((Tag)array.get(1)).orElse(0)),
               (long)Adapters.INT.readNbt((Tag)array.get(2)).orElse(0).intValue() << 32
                  | Integer.toUnsignedLong(Adapters.INT.readNbt((Tag)array.get(3)).orElse(0))
            )
         );
      } else if (nbt instanceof CollectionTag<?> array && array.size() == 2) {
         return Optional.of(new UUID(Adapters.LONG.readNbt((Tag)array.get(0)).orElse(0L), Adapters.LONG.readNbt((Tag)array.get(1)).orElse(0L)));
      } else if (nbt instanceof StringTag string) {
         try {
            return Optional.of(UUID.fromString(string.getAsString()));
         } catch (IllegalStateException var6) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public Optional<JsonElement> writeJson(@Nullable UUID value) {
      return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value.toString()));
   }

   @Override
   public Optional<UUID> readJson(@Nullable JsonElement json) {
      if (json instanceof JsonArray array && array.size() == 4) {
         return Optional.of(
            new UUID(
               (long)Adapters.INT.readJson(array.get(0)).orElse(0).intValue() << 32 | Integer.toUnsignedLong(Adapters.INT.readJson(array.get(1)).orElse(0)),
               (long)Adapters.INT.readJson(array.get(2)).orElse(0).intValue() << 32 | Integer.toUnsignedLong(Adapters.INT.readJson(array.get(3)).orElse(0))
            )
         );
      } else if (json instanceof JsonArray array && array.size() == 2) {
         return Optional.of(new UUID(Adapters.LONG.readJson(array.get(0)).orElse(0L), Adapters.LONG.readJson(array.get(1)).orElse(0L)));
      } else if (json instanceof JsonPrimitive primitive && primitive.isString()) {
         try {
            return Optional.of(UUID.fromString(primitive.getAsString()));
         } catch (IllegalStateException var6) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }
}
