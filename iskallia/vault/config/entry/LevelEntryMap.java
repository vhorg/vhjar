package iskallia.vault.config.entry;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public class LevelEntryMap<T> extends HashMap<Integer, T> {
   public Optional<T> getForLevel(int level) {
      return this.keySet().stream().filter(this.greaterOrEqual(level)).max(Comparator.naturalOrder()).map(this::get);
   }

   @NotNull
   private Predicate<Integer> greaterOrEqual(int level) {
      return i -> level >= i;
   }
}
