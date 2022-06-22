package iskallia.vault.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ListHelper {
   public static <T> void traverseOccurrences(Iterable<T> iterable, ListHelper.OccurrenceConsumer<T> consumer) {
      Map<T, Long> occurrenceMap = StreamSupport.stream(iterable.spliterator(), false)
         .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      int index = 0;

      for (Entry<T, Long> entry : occurrenceMap.entrySet()) {
         T item = entry.getKey();
         Long occurrence = entry.getValue();
         consumer.consume(index, item, occurrence);
         index++;
      }
   }

   @FunctionalInterface
   public interface OccurrenceConsumer<T> {
      void consume(int var1, T var2, long var3);
   }
}
