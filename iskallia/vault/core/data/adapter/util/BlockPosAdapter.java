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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class BlockPosAdapter implements ISimpleAdapter<BlockPos, Tag, JsonElement> {
   private final boolean nullable;

   public BlockPosAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public BlockPosAdapter asNullable() {
      return new BlockPosAdapter(true);
   }

   public void writeBits(@Nullable BlockPos value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeLong(value.asLong());
      }
   }

   @Override
   public Optional<BlockPos> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(BlockPos.of(buffer.readLong()));
   }

   public void writeBytes(@Nullable BlockPos value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeLong(value.asLong());
      }
   }

   @Override
   public Optional<BlockPos> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(BlockPos.of(buffer.readLong()));
   }

   public void writeData(@Nullable BlockPos value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         data.writeLong(value.asLong());
      }
   }

   @Override
   public Optional<BlockPos> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(BlockPos.of(data.readLong()));
   }

   public Optional<Tag> writeNbt(@Nullable BlockPos value) {
      return value == null ? Optional.empty() : Optional.of(LongTag.valueOf(value.asLong()));
   }

   @Override
   public Optional<BlockPos> readNbt(@Nullable Tag nbt) {
      if (nbt == null) {
         return Optional.empty();
      } else if (nbt instanceof NumericTag numeric) {
         return Adapters.LONG.readNbt(numeric).map(BlockPos::of);
      } else {
         if (nbt instanceof CollectionTag<?> list && list.size() == 3) {
            Integer x = Adapters.INT.readNbt((Tag)list.get(0)).orElse(null);
            Integer y = Adapters.INT.readNbt((Tag)list.get(1)).orElse(null);
            Integer z = Adapters.INT.readNbt((Tag)list.get(2)).orElse(null);
            if (x != null && y != null && z != null) {
               return Optional.of(new BlockPos(x, y, z));
            }
         }

         return Optional.empty();
      }
   }

   public Optional<JsonElement> writeJson(@Nullable BlockPos value) {
      return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value.asLong()));
   }

   @Override
   public Optional<BlockPos> readJson(@Nullable JsonElement json) {
      if (json == null) {
         return Optional.empty();
      } else if (json instanceof JsonPrimitive primitive) {
         return Adapters.LONG.readJson(primitive).map(BlockPos::of);
      } else {
         if (json instanceof JsonArray array && array.size() == 3) {
            Integer x = Adapters.INT.readJson(array.get(0)).orElse(null);
            Integer y = Adapters.INT.readJson(array.get(1)).orElse(null);
            Integer z = Adapters.INT.readJson(array.get(2)).orElse(null);
            if (x != null && y != null && z != null) {
               return Optional.of(new BlockPos(x, y, z));
            }
         }

         return Optional.empty();
      }
   }
}
