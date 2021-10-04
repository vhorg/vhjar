package iskallia.vault.util.data;

import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

public interface RandomListAccess<T> {
   @Nullable
   T getRandom(Random var1);

   default Optional<T> getOptionalRandom(Random random) {
      return Optional.ofNullable(this.getRandom(random));
   }

   void forEach(BiConsumer<T, Number> var1);

   boolean removeEntry(T var1);

   @Nullable
   default T removeRandom(Random random) {
      T element = this.getRandom(random);
      if (element != null) {
         this.removeEntry(element);
         return element;
      } else {
         return null;
      }
   }
}
