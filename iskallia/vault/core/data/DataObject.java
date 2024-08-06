package iskallia.vault.core.data;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class DataObject<D extends DataObject<D>> implements ICompound<D> {
   private final Map<FieldKey<Object>, Object> values = new LinkedHashMap<>();
   private final Set<FieldKey<Object>> updatedKeys = new HashSet<>();
   private final Set<FieldKey<Object>> removedKeys = new HashSet<>();

   public abstract FieldRegistry getFields();

   public <T> boolean has(FieldKey<T> key) {
      return this.values.containsKey(key);
   }

   public <T> T get(FieldKey<T> key) {
      return (T)this.values.get(key);
   }

   public <T> Optional<T> getOptional(FieldKey<T> key) {
      return this.values.containsKey(key) ? Optional.ofNullable(this.get(key)) : Optional.empty();
   }

   public <T> T getOr(FieldKey<T> key, T value) {
      return !this.values.containsKey(key) ? value : this.get(key);
   }

   public <T> void ifPresent(FieldKey<T> key, Consumer<T> value) {
      if (this.values.containsKey(key)) {
         value.accept((T)this.values.get(key));
      }
   }

   public <T, V> V map(FieldKey<T> key, Function<T, V> mapper, V none) {
      return this.values.containsKey(key) ? mapper.apply((T)this.values.get(key)) : none;
   }

   public <T> D set(FieldKey<T> key, T value) {
      if (!this.getFields().contains(key)) {
         throw new UnsupportedOperationException("This object does not support key " + key.getId());
      } else {
         if (this.values.containsKey(key)) {
            Object oldValue = this.values.put(key, value);
            if (!Objects.equals(oldValue, value)) {
               this.updatedKeys.add(key);
            }

            this.removedKeys.remove(key);
         } else {
            this.values.put(key, value);
            this.updatedKeys.add(key);
            this.removedKeys.remove(key);
         }

         return (D)this;
      }
   }

   public <T> D set(FieldKey<T> key) {
      return this.set(key, null);
   }

   public <T> D modify(FieldKey<T> key, UnaryOperator<T> value) {
      return this.set(key, value.apply(this.get(key)));
   }

   public <T> D modifyIfPresent(FieldKey<T> key, UnaryOperator<T> value) {
      return (D)(this.values.containsKey(key) ? this.set(key, value.apply(this.get(key))) : this);
   }

   public <T> D setIfPresent(FieldKey<T> key, T value) {
      return (D)(this.values.containsKey(key) ? this.set(key, value) : this);
   }

   public <T> D setIfAbsent(FieldKey<T> key, Supplier<T> value) {
      return (D)(!this.values.containsKey(key) ? this.set(key, value.get()) : this);
   }

   public <T> D setIf(FieldKey<T> key, T value, Predicate<T> condition) {
      if (condition.test(value)) {
         this.set(key, value);
      }

      return (D)this;
   }

   public <T> D setIf(FieldKey<T> key, BooleanSupplier condition) {
      if (condition.getAsBoolean()) {
         this.set(key, null);
      }

      return (D)this;
   }

   public <T> D remove(FieldKey<T> key) {
      if (this.values.containsKey(key)) {
         this.values.remove(key);
         this.updatedKeys.remove(key);
         this.removedKeys.add(key);
      }

      return (D)this;
   }

   public D write(BitBuffer buffer, SyncContext context) {
      Version version = context.getVersion();
      FieldRegistry fields = this.getFields();
      int size = 0;

      for (FieldKey<Object> key : this.values.keySet()) {
         if (key.canSync(this, context) && fields.getIndex(key, version) >= 0) {
            size++;
         }
      }

      buffer.writeIntSegmented(size, 3);
      this.values.forEach((keyx, value) -> {
         if (keyx.canSync(this, context)) {
            buffer.writeIntBounded(fields.getIndex(keyx, version), 0, fields.getSize(version) - 1);
            keyx.writeValue(version, buffer, context, value);
         }
      });
      return (D)this;
   }

   public D read(BitBuffer buffer, SyncContext context) {
      Version version = context.getVersion();
      FieldRegistry fields = this.getFields();
      this.values.clear();
      int size = buffer.readIntSegmented(3);

      for (int i = 0; i < size; i++) {
         FieldKey<?> key = fields.getKey(buffer.readIntBounded(0, fields.getSize(version) - 1), version);
         Object value = key.readValue(version, buffer, context);
         this.values.put((FieldKey<Object>)key, value);
      }

      this.resetDiff();
      return (D)this;
   }

   @Override
   public boolean isDirty(SyncContext context) {
      return !this.updatedKeys.isEmpty() || !this.removedKeys.isEmpty();
   }

   public D writeDiff(BitBuffer buffer, SyncContext context) {
      Version version = context.getVersion();
      FieldRegistry fields = this.getFields();

      for (FieldKey<Object> removedKey : this.removedKeys) {
         if (removedKey.canSync(this, context)) {
            buffer.writeBoolean(true);
            buffer.writeIntBounded(fields.getIndex(removedKey, version), fields.getSize(version));
         }
      }

      buffer.writeBoolean(false);

      for (FieldKey<Object> updatedKey : this.updatedKeys) {
         if (updatedKey.canSync(this, context)) {
            buffer.writeBoolean(true);
            buffer.writeIntBounded(fields.getIndex(updatedKey, version), fields.getSize(version));
            updatedKey.writeValue(version, buffer, context, this.values.get(updatedKey));
         }
      }

      buffer.writeBoolean(false);
      return (D)this;
   }

   public D readDiff(BitBuffer buffer, SyncContext context) {
      Version version = context.getVersion();
      FieldRegistry fields = this.getFields();

      while (buffer.readBoolean()) {
         FieldKey<?> key = fields.getKey(buffer.readIntBounded(fields.getSize(version)), version);
         this.values.remove(key);
      }

      while (buffer.readBoolean()) {
         FieldKey<?> key = fields.getKey(buffer.readIntBounded(fields.getSize(version)), version);
         this.values.put((FieldKey<Object>)key, key.readValue(context.getVersion(), buffer, context));
      }

      return (D)this;
   }

   public D resetDiff() {
      this.updatedKeys.clear();
      this.removedKeys.clear();
      return (D)this;
   }

   @Override
   public boolean isDirtyTree(SyncContext context) {
      if (this.isDirty(context)) {
         return true;
      } else {
         for (Entry<FieldKey<Object>, Object> entry : this.values.entrySet()) {
            if (entry.getKey().canSync(this, context) && entry.getValue() instanceof DataObject && ((DataObject)entry.getValue()).isDirty(context)) {
               return true;
            }
         }

         return false;
      }
   }

   public D writeDiffTree(BitBuffer buffer, SyncContext context) {
      Version version = context.getVersion();
      FieldRegistry fields = this.getFields();
      this.writeDiff(buffer, context);

      for (Entry<FieldKey<Object>, Object> entry : this.values.entrySet()) {
         if (entry.getKey().canSync(this, context)) {
            Object var8 = entry.getValue();
            if (var8 instanceof ICompound) {
               ICompound<?> compound = (ICompound<?>)var8;
               if (compound.isDirtyTree(context)) {
                  buffer.writeBoolean(true);
                  buffer.writeIntBounded(fields.getIndex(entry.getKey(), version), fields.getSize(version));
                  System.out
                     .println(this.getClass().getSimpleName() + " changed field [" + fields.getIndex(entry.getKey(), version) + "] " + entry.getKey().getId());
                  compound.writeDiffTree(buffer, context);
               }
            }
         }
      }

      buffer.writeBoolean(false);
      return (D)this;
   }

   public D readDiffTree(BitBuffer buffer, SyncContext context) {
      Version version = context.getVersion();
      FieldRegistry fields = this.getFields();
      this.readDiff(buffer, context);

      while (buffer.readBoolean()) {
         FieldKey<?> key = fields.getKey(buffer.readIntBounded(fields.getSize(version)), version);
         ((ICompound)this.values.get(key)).readDiffTree(buffer, context);
      }

      return (D)this;
   }

   public D resetDiffTree() {
      this.resetDiff();

      for (Object value : this.values.values()) {
         if (value instanceof ICompound) {
            ((ICompound)value).resetDiffTree();
         }
      }

      return (D)this;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("{");
      Iterator<Entry<FieldKey<Object>, Object>> it = this.values.entrySet().iterator();

      while (it.hasNext()) {
         Entry<FieldKey<Object>, Object> e = it.next();
         sb.append("\"").append(e.getKey().getId()).append("\":").append(e.getValue());
         if (it.hasNext()) {
            sb.append(",");
         }
      }

      return sb.append("}").toString();
   }
}
