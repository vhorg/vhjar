package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class DoubleAdapter extends Adapter<Double> {
   public Double validate(Double value, SyncContext context) {
      return value;
   }

   public void writeValue(BitBuffer buffer, SyncContext context, Double value) {
      buffer.writeDouble(value);
   }

   public Double readValue(BitBuffer buffer, SyncContext context, Double value) {
      return buffer.readDouble();
   }
}
