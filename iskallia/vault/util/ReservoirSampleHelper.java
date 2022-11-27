package iskallia.vault.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class ReservoirSampleHelper {
   private static final Random RANDOM = new Random();

   public static <T> List<T> sample(Iterable<T> iterable) {
      return sample(iterable, 1, RANDOM, new ArrayList<>(1));
   }

   public static <T> List<T> sample(Iterable<T> iterable, int count) {
      return sample(iterable, count, RANDOM, new ArrayList<>(count));
   }

   public static <T> List<T> sample(Iterable<T> iterable, int count, Random random) {
      return sample(iterable, count, random, new ArrayList<>(count));
   }

   public static <T> List<T> sample(Iterable<T> iterable, int count, Random random, List<T> result) {
      return sample(iterable.iterator(), count, random, result);
   }

   public static <T> List<T> sample(Iterator<T> iterator) {
      return sample(iterator, 1, RANDOM, new ArrayList<>(1));
   }

   public static <T> List<T> sample(Iterator<T> iterator, int count) {
      return sample(iterator, count, RANDOM, new ArrayList<>(count));
   }

   public static <T> List<T> sample(Iterator<T> iterator, int count, Random random) {
      return sample(iterator, count, random, new ArrayList<>(count));
   }

   public static <T> List<T> sample(Iterator<T> iterator, int count, Random random, List<T> result) {
      if (count <= 0) {
         return result;
      } else {
         for (int index = 0; iterator.hasNext(); index++) {
            T element = iterator.next();
            if (index < count) {
               result.add(element);
            } else {
               int randomIndex = random.nextInt(index + 1);
               if (randomIndex < count) {
                  result.set(randomIndex, element);
               }
            }
         }

         return result;
      }
   }

   public static void main(String[] args) {
      Random random = new Random();
      List<Integer> list = new ArrayList<>();
      Map<Integer, Integer> countMap = new HashMap<>();

      for (int i = 0; i < 100; i++) {
         list.add(i);
      }

      for (int i = 0; i < 1000000; i++) {
         for (Integer result : sample(list, 1, random, new ArrayList<>(1))) {
            countMap.merge(result, 1, Integer::sum);
         }
      }

      System.out.println(countMap);
   }

   private ReservoirSampleHelper() {
   }
}
