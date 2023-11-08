package iskallia.vault.core.data.adapter.basic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class TypeSupplierAdapter<T extends ISerializable<?, ?>> extends SupplierAdapter<T> {
   protected Map<String, Supplier<? extends T>> typeToSupplier = new HashMap<>();
   protected Map<Class<? extends T>, String> classToType = new HashMap<>();
   protected final String key;

   public TypeSupplierAdapter(String key, boolean nullable) {
      super(nullable);
      this.key = key;
   }

   public String getKey() {
      return this.key;
   }

   public Set<Class<? extends T>> getClasses() {
      return this.classToType.keySet();
   }

   public <A extends TypeSupplierAdapter<T>> A register(String id, Class<? extends T> type, Supplier<? extends T> supplier) {
      this.typeToSupplier.put(id, supplier);
      this.classToType.put(type, id);
      return (A)this;
   }

   public String getType(T value) {
      return value == null ? null : this.classToType.get(value.getClass());
   }

   public T getValue(String key) {
      return (T)(this.typeToSupplier.containsKey(key) ? this.typeToSupplier.get(key).get() : null);
   }

   @Override
   protected void writeSuppliedBits(T value, BitBuffer buffer) {
      if (this.isNullable()) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.UTF_8.writeBits(this.getType(value), buffer);
         value.writeBits(buffer);
      }
   }

   @Override
   protected T readSuppliedBits(BitBuffer buffer) {
      if (this.isNullable() && buffer.readBoolean()) {
         return null;
      } else {
         T value = this.getValue(Adapters.UTF_8.readBits(buffer).orElseThrow());
         value.readBits(buffer);
         return value;
      }
   }

   @Override
   protected void writeSuppliedBytes(T value, ByteBuf buffer) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected T readSuppliedBytes(ByteBuf buffer) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void writeSuppliedData(T value, DataOutput data) throws IOException {
      throw new UnsupportedOperationException();
   }

   @Override
   protected T readSuppliedData(DataInput data) throws IOException {
      throw new UnsupportedOperationException();
   }

   @Nullable
   @Override
   protected Tag writeSuppliedNbt(T value) {
      String type = this.getType(value);
      return type == null ? null : value.writeNbt().map(nbt -> {
         if (nbt instanceof CompoundTag compound) {
            compound.putString(this.key, this.getType(value));
         }

         return nbt;
      }).orElse(null);
   }

   @Nullable
   @Override
   protected T readSuppliedNbt(Tag nbt) {
      if (nbt instanceof CompoundTag compound) {
         T value = this.getValue(compound.getString(this.key));
         if (value != null) {
            ((ISerializable<Tag, ?>)value).readNbt(nbt);
         } else if (!compound.isEmpty()) {
            VaultMod.LOGGER.error("Could not deserialize " + nbt);
         }

         return value;
      } else {
         return null;
      }
   }

   @Nullable
   @Override
   protected JsonElement writeSuppliedJson(T value) {
      String type = this.getType(value);
      return type == null ? null : value.writeJson().map(nbt -> {
         if (nbt instanceof JsonObject object) {
            object.addProperty(this.key, this.getType(value));
         }

         return nbt;
      }).orElse(null);
   }

   @Nullable
   @Override
   protected T readSuppliedJson(JsonElement json) {
      if (json instanceof JsonObject object) {
         T value = this.getValue(object.get(this.key).getAsString());
         if (value != null) {
            ((ISerializable<?, JsonElement>)value).readJson(json);
         } else if (object.keySet().size() != 0) {
            VaultMod.LOGGER.error("Could not deserialize " + json);
         }

         return value;
      } else {
         return null;
      }
   }
}
