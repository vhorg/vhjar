package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public abstract class Adapter<T> {
   public abstract T validate(T var1, SyncContext var2);

   public abstract void writeValue(BitBuffer var1, SyncContext var2, T var3);

   public abstract T readValue(BitBuffer var1, SyncContext var2, T var3);

   @FunctionalInterface
   public interface Reader<T> {
      T readValue(BitBuffer var1, SyncContext var2);
   }

   @FunctionalInterface
   public interface Writer<T> {
      void writeValue(BitBuffer var1, SyncContext var2, T var3);
   }
}
