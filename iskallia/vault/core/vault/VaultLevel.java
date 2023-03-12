package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;

public class VaultLevel extends DataObject<VaultLevel> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Integer> VALUE = FieldKey.of("value", Integer.class).with(Version.v1_0, Adapters.INT_SEGMENTED_7, DISK.all()).register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public int get() {
      return Math.max(this.get(VALUE), 0);
   }
}
