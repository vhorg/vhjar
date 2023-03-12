package iskallia.vault.core.data.adapter.primitive;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class LongAdapter extends NumberAdapter<Long> {
   public LongAdapter(boolean nullable) {
      super(nullable);
   }

   public LongAdapter asNullable() {
      return new LongAdapter(true);
   }

   protected void writeNumberBits(Long value, BitBuffer buffer) {
      buffer.writeLong(value);
   }

   protected Long readNumberBits(BitBuffer buffer) {
      return buffer.readLong();
   }

   protected void writeNumberBytes(Long value, ByteBuf buffer) {
      buffer.writeLong(value);
   }

   protected Long readNumberBytes(ByteBuf buffer) {
      return buffer.readLong();
   }

   protected void writeNumberData(Long value, DataOutput data) throws IOException {
      data.writeLong(value);
   }

   protected Long readNumberData(DataInput data) throws IOException {
      return data.readLong();
   }

   protected Tag writeNumberNbt(Long value) {
      if (value.byteValue() == value) {
         return ByteTag.valueOf(value.byteValue());
      } else if (value.shortValue() == value) {
         return ShortTag.valueOf(value.shortValue());
      } else {
         return (Tag)(value.intValue() == value ? IntTag.valueOf(value.intValue()) : LongTag.valueOf(value));
      }
   }

   @Nullable
   protected Long readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return numeric.getAsLong();
      } else if (nbt instanceof ListTag list && list.size() == 1) {
         return this.readNumberNbt(list.get(0));
      } else if (nbt instanceof StringTag string) {
         String value = string.getAsString();
         String exception = value.substring(0, 2);

         int radix = switch (exception) {
            case "0x" -> 16;
            case "0o" -> 8;
            case "0b" -> 2;
            default -> 10;
         };

         try {
            return Long.parseLong(value.substring(2), radix);
         } catch (NumberFormatException var9) {
            return null;
         }
      } else {
         return null;
      }
   }

   protected JsonElement writeNumberJson(Long value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   protected Long readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
               return primitive.getAsLong();
            }

            if (primitive.isString()) {
               String value = primitive.getAsString();
               String exception = value.substring(0, 2);

               int radix = switch (exception) {
                  case "0x" -> 16;
                  case "0o" -> 8;
                  case "0b" -> 2;
                  default -> 10;
               };

               try {
                  return Long.parseLong(value.substring(2), radix);
               } catch (NumberFormatException var8) {
                  return null;
               }
            }
         }

         return null;
      }
   }
}
