package iskallia.vault.core.data;

import iskallia.vault.core.data.action.ListAction;
import iskallia.vault.core.data.action.ListTracker;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class DataList<D extends DataList<D, E>, E> extends AbstractList<E> implements ICompound<D> {
   private final List<E> delegate;
   private final Adapter<E> adapter;
   private final Supplier<E> supplier;
   protected final ListTracker tracker = new ListTracker();

   public DataList(List<E> delegate, Adapter<E> adapter, Supplier<E> supplier) {
      this.delegate = delegate;
      this.adapter = adapter;
      this.supplier = supplier;
   }

   public DataList(List<E> delegate, Adapter<E> adapter) {
      this(delegate, adapter, () -> null);
   }

   @Override
   public int size() {
      return this.delegate.size();
   }

   @Override
   public boolean add(E element) {
      this.tracker.addAction(ListAction.ofAppend(this.delegate.size(), element));
      this.delegate.add(element);
      return true;
   }

   @Override
   public E get(int index) {
      return this.delegate.get(index);
   }

   @Override
   public E set(int index, E element) {
      this.tracker.addAction(ListAction.ofSet(index, element, this.delegate.size()));
      return this.delegate.set(index, element);
   }

   @Override
   public void add(int index, E element) {
      this.tracker.addAction(ListAction.ofAdd(index, element, this.delegate.size()));
      this.delegate.add(index, element);
   }

   @Override
   public E remove(int index) {
      this.tracker.addAction(ListAction.ofRemove(index, this.delegate.size()));
      return this.delegate.remove(index);
   }

   @Override
   public void clear() {
      this.tracker.addAction(ListAction.ofClear());
      this.delegate.clear();
   }

   public D write(BitBuffer buffer, SyncContext context) {
      buffer.writeIntSegmented(this.delegate.size(), 4);

      for (E value : this.delegate) {
         this.adapter.writeValue(buffer, context, this.adapter.validate(value, context));
      }

      return (D)this;
   }

   public D read(BitBuffer buffer, SyncContext context) {
      int size = buffer.readIntSegmented(4);
      this.delegate.clear();

      for (int i = 0; i < size; i++) {
         this.add(this.adapter.readValue(buffer, context, this.supplier.get()));
      }

      this.resetDiff();
      return (D)this;
   }

   @Override
   public boolean isDirty(SyncContext context) {
      return !this.tracker.getActions().isEmpty();
   }

   public D writeDiff(BitBuffer packet, SyncContext context) {
      packet.writeBoolean(this.tracker.getActions().isEmpty());
      if (this.tracker.getActions().isEmpty()) {
         return (D)this;
      } else {
         boolean doClear = this.tracker.getActions().get(0).type == ListAction.Type.CLEAR;
         packet.writeBoolean(doClear);
         packet.writeIntSegmented(this.tracker.getActions().size() - (doClear ? 1 : 0), 4);

         for (int i = doClear ? 1 : 0; i < this.tracker.getActions().size(); i++) {
            ListAction action = this.tracker.getActions().get(i);
            packet.writeIntBits(action.type.ordinal(), 2);
            if (action.type != ListAction.Type.APPEND) {
               packet.writeIntBounded(action.index, 0, action.size - 1);
            }

            if (action.type != ListAction.Type.REMOVE) {
               this.adapter.writeValue(packet, context, this.adapter.validate((E)action.value, context));
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
            DataList.Action action = new DataList.Action(-1, null, DataList.ActionType.values()[packet.readIntBits(2)], -1);
            if (action.type != DataList.ActionType.APPEND) {
               action.index = packet.readIntBounded(0, this.delegate.size() - 1);
            }

            if (action.type != DataList.ActionType.REMOVE) {
               action.value = this.adapter.readValue(packet, context, this.supplier.get());
            }

            action.apply(this);
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
         for (Object value : this.delegate) {
            if (value instanceof ICompound && ((ICompound)value).isDirty(context)) {
               return true;
            }
         }

         return false;
      }
   }

   public D writeDiffTree(BitBuffer packet, SyncContext context) {
      this.writeDiff(packet, context);

      for (E value : this.delegate) {
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

      for (E value : this.delegate) {
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

      for (E value : this.delegate) {
         if (value instanceof ICompound) {
            ((ICompound)value).resetDiffTree();
         }
      }

      return (D)this;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("[");
      Iterator<E> it = this.delegate.iterator();

      while (it.hasNext()) {
         sb.append(it.next());
         if (it.hasNext()) {
            sb.append(",");
         }
      }

      return sb.append("]").toString();
   }

   private static class Action {
      public int index;
      public Object value;
      public DataList.ActionType type;
      public int size;

      public Action(int index, Object value, DataList.ActionType type, int size) {
         this.index = index;
         this.value = value;
         this.type = type;
         this.size = size;
      }

      public static DataList.Action ofAppend(int size, Object value) {
         return new DataList.Action(size, value, DataList.ActionType.APPEND, -1);
      }

      public static DataList.Action ofAdd(int index, Object value, int size) {
         return new DataList.Action(index, value, DataList.ActionType.ADD, size);
      }

      public static DataList.Action ofSet(int index, Object value, int size) {
         return new DataList.Action(index, value, DataList.ActionType.SET, size);
      }

      public static DataList.Action ofRemove(int index, int size) {
         return new DataList.Action(index, null, DataList.ActionType.REMOVE, size);
      }

      public static DataList.Action ofClear() {
         return new DataList.Action(-1, null, DataList.ActionType.CLEAR, -1);
      }

      public void apply(List list) {
         switch (this.type) {
            case APPEND:
               list.add(this.value);
               break;
            case ADD:
               list.add(this.index, this.value);
               break;
            case SET:
               list.set(this.index, this.value);
               break;
            case REMOVE:
               list.remove(this.index);
               break;
            case CLEAR:
               list.clear();
         }
      }
   }

   private static enum ActionType {
      APPEND,
      ADD,
      SET,
      REMOVE,
      CLEAR;
   }
}
