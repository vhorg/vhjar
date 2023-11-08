package iskallia.vault.core.data.key;

public abstract class Keyed<T> implements IKeyed<T> {
   private VersionedKey<?, T> key;

   @Override
   public VersionedKey<?, T> getKey() {
      return this.key;
   }

   @Override
   public void setKey(VersionedKey<?, T> key) {
      this.key = key;
   }
}
