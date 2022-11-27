package iskallia.vault.core.util.iterator;

import java.util.Iterator;

public class EmptyIterator<T> implements Iterator<T> {
   @Override
   public boolean hasNext() {
      return false;
   }

   @Override
   public T next() {
      throw new UnsupportedOperationException();
   }
}
