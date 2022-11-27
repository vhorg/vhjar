package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class FloatAdapter extends Adapter<Float> {
   public Float validate(Float value, SyncContext context) {
      return value;
   }

   public void writeValue(BitBuffer buffer, SyncContext context, Float value) {
      buffer.writeFloat(value);
   }

   public Float readValue(BitBuffer buffer, SyncContext context, Float value) {
      return buffer.readFloat();
   }
}
