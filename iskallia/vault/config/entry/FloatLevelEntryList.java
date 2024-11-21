package iskallia.vault.config.entry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FloatLevelEntryList<T extends FloatLevelEntryList.FloatLevelEntry> extends ArrayList<T> {
   public Optional<T> getForValue(float value) {
      if (this.isEmpty()) {
         return Optional.empty();
      } else {
         List<T> copy = new ArrayList<>(this);
         copy.sort(Comparator.comparing(FloatLevelEntryList.FloatLevelEntry::getMinValue));

         for (int i = 0; i < copy.size(); i++) {
            if (value < copy.get(i).getMinValue()) {
               if (i == 0) {
                  return Optional.empty();
               }

               return Optional.of(copy.get(i - 1));
            }
         }

         return Optional.of(copy.get(copy.size() - 1));
      }
   }

   public interface FloatLevelEntry {
      float getMinValue();
   }
}
