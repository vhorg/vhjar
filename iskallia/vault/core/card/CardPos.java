package iskallia.vault.core.card;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.SerializableAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class CardPos implements ISerializable<Tag, JsonElement> {
   public static final SerializableAdapter<CardPos, Tag, JsonElement> ADAPTER = Adapters.of(CardPos::new, true);
   public int x;
   public int y;

   protected CardPos() {
   }

   public CardPos(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public CardPos add(int x, int y) {
      return new CardPos(this.x + x, this.y + y);
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other != null && this.getClass() == other.getClass()) {
         CardPos pos = (CardPos)other;
         return this.x == pos.x && this.y == pos.y;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.x * 31 + this.y;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.x), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.y), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.x = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.y = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<Tag> writeNbt() {
      return Adapters.INT_ARRAY.writeNbt(new int[]{this.x, this.y});
   }

   @Override
   public void readNbt(Tag nbt) {
      if (nbt instanceof CollectionTag) {
         int[] array = Adapters.INT_ARRAY.readNbt(nbt).orElseThrow();
         this.x = array[0];
         this.y = array[1];
      } else {
         if (!(nbt instanceof CompoundTag compound)) {
            throw new UnsupportedOperationException();
         }

         this.x = Adapters.INT.readNbt(compound.get("x")).orElseThrow();
         this.y = Adapters.INT.readNbt(compound.get("y")).orElseThrow();
      }
   }

   @Override
   public Optional<JsonElement> writeJson() {
      return Adapters.INT_ARRAY.writeJson(new int[]{this.x, this.y}).map(array -> (JsonElement)array);
   }

   @Override
   public void readJson(JsonElement json) {
      if (json instanceof JsonArray array) {
         int[] value = Adapters.INT_ARRAY.readJson(array).orElseThrow();
         this.x = value[0];
         this.y = value[1];
      } else {
         if (!(json instanceof JsonObject object)) {
            throw new UnsupportedOperationException();
         }

         this.x = Adapters.INT.readJson(object.get("x")).orElse(0);
         this.y = Adapters.INT.readJson(object.get("y")).orElse(0);
      }
   }
}
