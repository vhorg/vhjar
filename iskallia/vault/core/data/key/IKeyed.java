package iskallia.vault.core.data.key;

public interface IKeyed<T> {
   VersionedKey<?, T> getKey();

   void setKey(VersionedKey<?, T> var1);
}
