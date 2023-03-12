package iskallia.vault.core.data.adapter.vault;

import iskallia.vault.core.data.ICompound;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class CompoundAdapter<T extends ICompound<?>> implements IBitAdapter<T, SyncContext> {
   private final Supplier<T> supplier;
   private final boolean nullable;

   public CompoundAdapter(Supplier<T> supplier, boolean nullable) {
      this.supplier = supplier;
      this.nullable = nullable;
   }

   public static <T extends ICompound<?>> CompoundAdapter<T> of(Supplier<T> supplier) {
      return new CompoundAdapter<>(supplier, false);
   }

   public CompoundAdapter<T> asNullable() {
      return new CompoundAdapter<>(this.supplier, true);
   }

   public Supplier<T> getSupplier() {
      return this.supplier;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public void writeBits(@Nullable T value, BitBuffer buffer, SyncContext context) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         value.write(buffer, context);
      }
   }

   public Optional<T> readBits(BitBuffer buffer, SyncContext context) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         T value = null;
         if (this.supplier != null) {
            value = this.supplier.get();
         }

         if (value != null) {
            value.read(buffer, context);
         }

         return Optional.ofNullable(value);
      }
   }
}
