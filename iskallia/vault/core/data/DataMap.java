package iskallia.vault.core.data;

import iskallia.vault.core.data.action.ListAction;
import iskallia.vault.core.data.action.ListTracker;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class DataMap<D extends DataMap<D, K, V>, K, V> extends AbstractMap<K, V> implements ICompound<D> {
   protected final Map<K, V> delegate;
   private final Adapter<K> keyAdapter;
   private final Supplier<K> keySupplier;
   private final Adapter<V> valueAdapter;
   private final Supplier<V> valueSupplier;
   protected final List<K> keys = new ArrayList<>();
   private final ListTracker tracker = new ListTracker();

   public DataMap(Map<K, V> delegate, Adapter<K> keyAdapter, Supplier<K> keySupplier, Adapter<V> valueAdapter, Supplier<V> valueSupplier) {
      this.keyAdapter = keyAdapter;
      this.keySupplier = keySupplier;
      this.valueAdapter = valueAdapter;
      this.valueSupplier = valueSupplier;
      this.delegate = delegate;
   }

   public DataMap(Map<K, V> delegate, Adapter<K> keyAdapter, Adapter<V> valueAdapter) {
      this(delegate, keyAdapter, () -> null, valueAdapter, () -> null);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.delegate.entrySet();
   }

   @Override
   public V put(K key, V value) {
      if (this.containsKey(key)) {
         this.tracker.addAction(ListAction.ofSet(this.keys.indexOf(key), key, this.keys.size()));
      } else {
         this.tracker.addAction(ListAction.ofAppend(this.keys.size(), key));
         this.keys.add(key);
      }

      return this.delegate.put(key, value);
   }

   @Override
   public V remove(Object key) {
      int index = this.keys.indexOf(key);
      if (index >= 0) {
         this.tracker.addAction(ListAction.ofRemove(index, this.keys.size()));
         this.keys.remove(index);
      }

      return this.delegate.remove(key);
   }

   public D write(BitBuffer buffer, SyncContext context) {
      buffer.writeIntSegmented(this.keys.size(), 4);

      for (K key : this.keys) {
         this.keyAdapter.writeValue(buffer, context, key);
         this.valueAdapter.writeValue(buffer, context, this.delegate.get(key));
      }

      return (D)this;
   }

   public D read(BitBuffer buffer, SyncContext context) {
      this.delegate.clear();
      this.keys.clear();
      int size = buffer.readIntSegmented(4);

      for (int i = 0; i < size; i++) {
         K key = this.keyAdapter.readValue(buffer, context, this.keySupplier.get());
         V value = this.valueAdapter.readValue(buffer, context, this.valueSupplier.get());
         this.delegate.put(key, value);
         this.keys.add(key);
      }

      this.resetDiff();
      return (D)this;
   }

   @Override
   public boolean isDirty(SyncContext context) {
      return !this.tracker.getActions().isEmpty();
   }

   public D writeDiff(BitBuffer packet, SyncContext context) {
      List<ListAction> actions = this.tracker.getActions();
      packet.writeBoolean(actions.isEmpty());
      if (actions.isEmpty()) {
         return (D)this;
      } else {
         boolean doClear = actions.get(0).type == ListAction.Type.CLEAR;
         packet.writeBoolean(doClear);
         packet.writeIntSegmented(actions.size() - (doClear ? 1 : 0), 4);

         for (int i = doClear ? 1 : 0; i < actions.size(); i++) {
            ListAction action = actions.get(i);
            packet.writeIntBits(action.type.ordinal(), 2);
            if (action.type != ListAction.Type.APPEND) {
               packet.writeIntBounded(action.index, 0, action.size - 1);
            } else {
               this.keyAdapter.writeValue(packet, context, this.keyAdapter.validate((K)action.value, context));
            }

            if (action.type != ListAction.Type.REMOVE) {
               this.valueAdapter.writeValue(packet, context, this.valueAdapter.validate(this.delegate.get(action.value), context));
            }
         }

         return (D)this;
      }
   }

   public D readDiff(BitBuffer packet, SyncContext context) {
      if (packet.readBoolean()) {
         return (D)this;
      } else {
         if (packet.readBoolean()) {
            this.delegate.clear();
         }

         int size = packet.readIntSegmented(4);

         for (int i = 0; i < size; i++) {
            ListAction.Type type = ListAction.Type.values()[packet.readIntSegmented(2)];
            int index = -1;
            K key = null;
            V value = null;
            if (type != ListAction.Type.APPEND) {
               index = packet.readIntBounded(0, this.delegate.size() - 1);
            } else {
               key = this.keyAdapter.readValue(packet, context, this.keySupplier.get());
            }

            if (type != ListAction.Type.REMOVE) {
               value = this.valueAdapter.readValue(packet, context, this.valueSupplier.get());
            }

            switch (type) {
               case APPEND:
                  this.delegate.put(key, value);
                  this.keys.add(key);
                  break;
               case SET:
                  this.delegate.put(this.keys.get(index), value);
                  break;
               case REMOVE:
                  this.delegate.remove(this.keys.get(index));
                  this.keys.remove(index);
            }
         }

         return (D)this;
      }
   }

   public D resetDiff() {
      this.tracker.getActions().clear();
      return (D)this;
   }

   @Override
   public boolean isDirtyTree(SyncContext context) {
      if (this.isDirty(context)) {
         return true;
      } else {
         for (V value : this.delegate.values()) {
            if (value instanceof DataObject && ((DataObject)value).isDirty(context)) {
               return true;
            }
         }

         return false;
      }
   }

   public D writeDiffTree(BitBuffer packet, SyncContext context) {
      this.writeDiff(packet, context);

      for (K key : this.keys) {
         V value = this.delegate.get(key);
         if (value instanceof ICompound) {
            ICompound<?> compound = (ICompound<?>)value;
            if (compound.isDirtyTree(context)) {
               packet.writeBoolean(true);
               compound.writeDiffTree(packet, context);
            } else {
               packet.writeBoolean(false);
            }
         }
      }

      return (D)this;
   }

   public D readDiffTree(BitBuffer packet, SyncContext context) {
      this.readDiff(packet, context);

      for (K key : this.keys) {
         V value = this.delegate.get(key);
         if (value instanceof ICompound) {
            ICompound<?> compound = (ICompound<?>)value;
            if (packet.readBoolean()) {
               compound.readDiffTree(packet, context);
            }
         }
      }

      return (D)this;
   }

   public D resetDiffTree() {
      this.resetDiff();

      for (V value : this.delegate.values()) {
         if (value instanceof ICompound) {
            ((ICompound)value).resetDiffTree();
         }
      }

      return (D)this;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("{");
      Iterator<Entry<K, V>> it = this.delegate.entrySet().iterator();

      while (it.hasNext()) {
         Entry<K, V> entry = it.next();
         sb.append(entry.getKey()).append(":").append(entry.getValue());
         if (it.hasNext()) {
            sb.append(",");
         }
      }

      return sb.append("}").toString();
   }
}
