package iskallia.vault.core.util.iterator;

import java.util.Iterator;
import java.util.function.Function;

public class MappingIterator<V, T> implements Iterator<T> {
   private final Iterator<V> parent;
   private final Function<V, T> mapper;
   private boolean isDirty = true;
   private boolean hasNext;
   private T next;

   public MappingIterator(Iterator<V> parent, Function<V, T> mapper) {
      this.parent = parent;
      this.mapper = mapper;
   }

   @Override
   public boolean hasNext() {
      this.compute();
      return this.hasNext;
   }

   @Override
   public T next() {
      this.compute();
      this.isDirty = true;
      return this.next;
   }

   protected void compute() {
      if (this.isDirty) {
         while (this.parent.hasNext()) {
            T value = this.mapper.apply(this.parent.next());
            if (value != null) {
               this.hasNext = true;
               this.next = value;
               this.isDirty = false;
               return;
            }
         }

         this.hasNext = false;
         this.next = null;
         this.isDirty = false;
      }
   }
}
