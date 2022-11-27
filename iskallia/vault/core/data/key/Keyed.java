package iskallia.vault.core.data.key;

public abstract class Keyed<T> {
   private VersionedKey<?, T> key;

   public VersionedKey<?, T> getKey() {
      return this.key;
   }

   public void setKey(VersionedKey<?, T> key) {
      this.key = key;
   }
}
