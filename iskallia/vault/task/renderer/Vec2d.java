package iskallia.vault.task.renderer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class Vec2d implements ISerializable<Tag, JsonElement> {
   public static final Vec2d ZERO = new Vec2d();
   private double x;
   private double y;

   public Vec2d() {
      this(0.0, 0.0);
   }

   public Vec2d(double x, double y) {
      this.x = x;
      this.y = y;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public Vec2d add(double x, double y) {
      return new Vec2d(this.x + x, this.y + y);
   }

   public Vec2d add(Vec2d other) {
      return this.add(other.x, other.y);
   }

   public Vec2d subtract(double x, double y) {
      return this.add(-x, -y);
   }

   public Vec2d subtract(Vec2d other) {
      return this.subtract(other.x, other.y);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.DOUBLE.writeBits(Double.valueOf(this.x), buffer);
      Adapters.DOUBLE.writeBits(Double.valueOf(this.y), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.x = Adapters.DOUBLE.readBits(buffer).orElseThrow();
      this.y = Adapters.DOUBLE.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<Tag> writeNbt() {
      return Adapters.DOUBLE_ARRAY.writeNbt(new double[]{this.x, this.y});
   }

   @Override
   public void readNbt(Tag nbt) {
      if (nbt instanceof CollectionTag) {
         double[] array = Adapters.DOUBLE_ARRAY.readNbt(nbt).orElseThrow();
         this.x = array[0];
         this.y = array[1];
      } else {
         if (!(nbt instanceof CompoundTag compound)) {
            throw new UnsupportedOperationException();
         }

         this.x = compound.getDouble("x");
         this.y = compound.getDouble("y");
      }
   }

   @Override
   public Optional<JsonElement> writeJson() {
      return Adapters.DOUBLE_ARRAY.writeJson(new double[]{this.x, this.y});
   }

   @Override
   public void readJson(JsonElement json) {
      if (json instanceof JsonArray array) {
         double[] value = Adapters.DOUBLE_ARRAY.readJson(array).orElseThrow();
         this.x = value[0];
         this.y = value[1];
      } else {
         if (!(json instanceof JsonObject object)) {
            throw new UnsupportedOperationException();
         }

         this.x = object.has("x") ? object.get("x").getAsDouble() : 0.0;
         this.y = object.has("y") ? object.get("y").getAsDouble() : 0.0;
      }
   }
}
