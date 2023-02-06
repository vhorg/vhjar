package iskallia.vault.util.data;

import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

public class LazySet<T> extends HashSet<T> {
   protected boolean initialized;
   protected Supplier<List<T>> initializer;

   public LazySet(Supplier<List<T>> initializer) {
      this.initializer = initializer;
   }

   @Override
   public boolean contains(Object o) {
      if (!this.initialized) {
         this.addAll(this.initializer.get());
         this.initialized = true;
      }

      return super.contains(o);
   }
}
