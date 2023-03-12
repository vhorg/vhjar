package iskallia.vault.core.data.adapter.vault;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class DirectAdapter<T> implements IBitAdapter<T, SyncContext> {
   private final DirectAdapter.Writer<T> writer;
   private final DirectAdapter.Reader<T> reader;

   public DirectAdapter(DirectAdapter.Writer<T> writer, DirectAdapter.Reader<T> reader) {
      this.writer = writer;
      this.reader = reader;
   }

   public void writeBits(@Nullable T value, BitBuffer buffer, SyncContext context) {
      this.writer.writeBits(value, buffer, context);
   }

   public Optional<T> readBits(BitBuffer buffer, SyncContext context) {
      return this.reader.readBits(buffer, context);
   }

   @FunctionalInterface
   public interface Reader<T> {
      Optional<T> readBits(BitBuffer var1, SyncContext var2);
   }

   @FunctionalInterface
   public interface Writer<T> {
      void writeBits(@Nullable T var1, BitBuffer var2, SyncContext var3);
   }
}
