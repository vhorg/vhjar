package iskallia.vault.core.util;

import iskallia.vault.core.Version;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

public class VersionMap<T> extends AbstractMap<Version, T> {
   private Map<Version, T> delegate = new LinkedHashMap<>();
   private Version oldest;

   @NotNull
   @Override
   public Set<Entry<Version, T>> entrySet() {
      return this.delegate.entrySet();
   }

   public T put(Version key, T value) {
      if (this.oldest == null || key.isOlderThan(this.oldest)) {
         this.oldest = key;
      }

      return this.delegate.put(key, value);
   }

   public Optional<Version> getOldest() {
      return Optional.ofNullable(this.oldest);
   }

   public Optional<T> getFor(Version version) {
      T previous = null;

      for (Entry<Version, T> entry : this.entrySet()) {
         if (version.isOlderThan(entry.getKey())) {
            break;
         }

         previous = entry.getValue();
      }

      return Optional.ofNullable(previous);
   }
}
