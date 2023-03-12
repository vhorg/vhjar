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
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class ShortAdapter extends NumberAdapter<Short> {
   public ShortAdapter(boolean nullable) {
      super(nullable);
   }

   public ShortAdapter asNullable() {
      return new ShortAdapter(true);
   }

   protected void writeNumberBits(Short value, BitBuffer buffer) {
      buffer.writeShort(value);
   }

   protected Short readNumberBits(BitBuffer buffer) {
      return buffer.readShort();
   }

   protected void writeNumberBytes(Short value, ByteBuf buffer) {
      buffer.writeShort(value);
   }

   protected Short readNumberBytes(ByteBuf buffer) {
      return buffer.readShort();
   }

   protected void writeNumberData(Short value, DataOutput data) throws IOException {
      data.writeShort(value);
   }

   protected Short readNumberData(DataInput data) throws IOException {
      return data.readShort();
   }

   protected Tag writeNumberNbt(Short value) {
      return (Tag)(value.byteValue() == value ? ByteTag.valueOf(value.byteValue()) : ShortTag.valueOf(value));
   }

   @Nullable
   protected Short readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return numeric.getAsShort();
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
            return Short.parseShort(value.substring(2), radix);
         } catch (NumberFormatException var9) {
            return null;
         }
      } else {
         return null;
      }
   }

   protected JsonElement writeNumberJson(Short value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   protected Short readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
               return primitive.getAsShort();
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
                  return Short.parseShort(value.substring(2), radix);
               } catch (NumberFormatException var8) {
                  return null;
               }
            }
         }

         return null;
      }
   }
}
