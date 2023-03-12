package iskallia.vault.config.entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class LevelEntryList<T extends LevelEntryList.ILevelEntry> extends ArrayList<T> {
   public static final LevelEntryList<?> EMPTY = new LevelEntryList();

   public static <T extends LevelEntryList.ILevelEntry> LevelEntryList<T> empty() {
      return (LevelEntryList<T>)EMPTY;
   }

   @SafeVarargs
   public static <T extends LevelEntryList.ILevelEntry> LevelEntryList<T> of(T... elements) {
      LevelEntryList<T> list = new LevelEntryList<>(elements.length);
      Collections.addAll(list, elements);
      return list;
   }

   public LevelEntryList() {
   }

   public LevelEntryList(int initialCapacity) {
      super(initialCapacity);
   }

   public Optional<T> getForLevel(int level) {
      if (this.isEmpty()) {
         return Optional.empty();
      } else {
         List<T> copy = new ArrayList<>(this);
         copy.sort(Comparator.comparing(LevelEntryList.ILevelEntry::getLevel));

         for (int i = 0; i < copy.size(); i++) {
            if (level < copy.get(i).getLevel()) {
               if (i == 0) {
                  return Optional.empty();
               }

               return Optional.of(copy.get(i - 1));
            }
         }

         return Optional.of(copy.get(copy.size() - 1));
      }
   }

   public LevelEntryList<T> put(T element) {
      this.add(element);
      return this;
   }

   public interface ILevelEntry {
      int getLevel();
   }

   private static class Test {
      public static void main(String[] args) {
         LevelEntryList<LevelEntryList.Test.TestLevelEntry> list = new LevelEntryList<>();
         list.add(new LevelEntryList.Test.TestLevelEntry(10));
         list.add(new LevelEntryList.Test.TestLevelEntry(5));
         list.add(new LevelEntryList.Test.TestLevelEntry(1));
         list.add(new LevelEntryList.Test.TestLevelEntry(20));

         for (int i = 0; i < 25; i++) {
            System.out.println("For level " + i + ": " + list.getForLevel(i).orElse(null));
         }

         list = new LevelEntryList<>();
         Optional<LevelEntryList.Test.TestLevelEntry> result = list.getForLevel(10);
         System.out.println(result);
      }

      private record TestLevelEntry(int level) implements LevelEntryList.ILevelEntry {
         @Override
         public int getLevel() {
            return this.level;
         }
      }
   }
}
