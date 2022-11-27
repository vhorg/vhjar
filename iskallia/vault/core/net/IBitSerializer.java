package iskallia.vault.core.net;

public interface IBitSerializer<T> {
   void write(BitBuffer var1, T var2);

   T read(BitBuffer var1);

   @FunctionalInterface
   public interface Reader<T> {
      T read(BitBuffer var1);
   }

   @FunctionalInterface
   public interface Writer<T> {
      void write(BitBuffer var1, T var2);
   }
}
