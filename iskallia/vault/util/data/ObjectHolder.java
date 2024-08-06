package iskallia.vault.util.data;

public class ObjectHolder<T> {
   private T object;

   public ObjectHolder(T object) {
      this.object = object;
   }

   public T get() {
      return this.object;
   }

   public void set(T object) {
      this.object = object;
   }
}
