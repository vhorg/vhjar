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
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class IntAdapter extends NumberAdapter<Integer> {
   public IntAdapter(boolean nullable) {
      super(nullable);
   }

   public IntAdapter asNullable() {
      return new IntAdapter(true);
   }

   protected void writeNumberBits(Integer value, BitBuffer buffer) {
      buffer.writeInt(value);
   }

   protected Integer readNumberBits(BitBuffer buffer) {
      return buffer.readInt();
   }

   protected void writeNumberBytes(Integer value, ByteBuf buffer) {
      buffer.writeInt(value);
   }

   protected Integer readNumberBytes(ByteBuf buffer) {
      return buffer.readInt();
   }

   protected void writeNumberData(Integer value, DataOutput data) throws IOException {
      data.writeInt(value);
   }

   protected Integer readNumberData(DataInput data) throws IOException {
      return data.readInt();
   }

   protected Tag writeNumberNbt(Integer value) {
      if (value.byteValue() == value) {
         return ByteTag.valueOf(value.byteValue());
      } else {
         return (Tag)(value.shortValue() == value ? ShortTag.valueOf(value.shortValue()) : IntTag.valueOf(value));
      }
   }

   @Nullable
   protected Integer readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return numeric.getAsInt();
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
            return Integer.parseInt(value.substring(2), radix);
         } catch (NumberFormatException var9) {
            return null;
         }
      } else {
         return null;
      }
   }

   protected JsonElement writeNumberJson(Integer value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   protected Integer readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
               return primitive.getAsInt();
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
                  return Integer.parseInt(value.substring(2), radix);
               } catch (NumberFormatException var8) {
                  return null;
               }
            }
         }

         return null;
      }
   }
}
