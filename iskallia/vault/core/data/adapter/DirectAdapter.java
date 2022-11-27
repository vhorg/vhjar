package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class DirectAdapter<T> extends Adapter<T> {
   private final Adapter.Writer<T> writer;
   private final Adapter.Reader<T> reader;

   public DirectAdapter(Adapter.Writer<T> writer, Adapter.Reader<T> reader) {
      this.writer = writer;
      this.reader = reader;
   }

   @Override
   public T validate(T value, SyncContext context) {
      return value;
   }

   @Override
   public void writeValue(BitBuffer buffer, SyncContext context, T value) {
      this.writer.writeValue(buffer, context, value);
   }

   @Override
   public T readValue(BitBuffer buffer, SyncContext context, T value) {
      return this.reader.readValue(buffer, context);
   }
}
