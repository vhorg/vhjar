package iskallia.vault.util.data;

import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.net.IBitSerializer;
import javax.annotation.Nonnull;
import net.minecraftforge.common.util.NonNullSupplier;

public class LazyHolder<T> {
   private T value;
   private final NonNullSupplier<T> initializer;
   private final IBitSerializer<T> serializer;

   public LazyHolder(T value, NonNullSupplier<T> initializer, IBitSerializer<T> serializer) {
      this.value = value;
      this.initializer = initializer;
      this.serializer = serializer;
   }

   public LazyHolder(NonNullSupplier<T> initializer, IBitSerializer<T> serializer) {
      this(null, initializer, serializer);
   }

   @Nonnull
   public T get() {
      if (this.value == null) {
         this.value = (T)this.initializer.get();
      }

      return this.value;
   }

   public void set(@Nonnull T value) {
      this.value = value;
   }

   public void refresh() {
      this.value = (T)this.initializer.get();
   }

   public void write(BitBuffer buf) {
      this.serializer.write(buf, this.get());
   }

   public void read(BitBuffer buf) {
      this.value = this.serializer.read(buf);
   }
}
