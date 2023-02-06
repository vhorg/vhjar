package iskallia.vault.core.net;

public interface IBitSerializer<T> {
   void write(BitBuffer var1, T var2);

   T read(BitBuffer var1);

   static <T> IBitSerializer<T> of(final IBitSerializer.Writer<T> writer, final IBitSerializer.Reader<T> reader) {
      return new IBitSerializer<T>() {
         @Override
         public void write(BitBuffer buffer, T value) {
            writer.write(buffer, value);
         }

         @Override
         public T read(BitBuffer buffer) {
            return reader.read(buffer);
         }
      };
   }

   @FunctionalInterface
   public interface Reader<T> {
      T read(BitBuffer var1);
   }

   @FunctionalInterface
   public interface Writer<T> {
      void write(BitBuffer var1, T var2);
   }
}
