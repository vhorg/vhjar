package iskallia.vault.core.vault.abyss;

import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.FieldRegistry;

public class LegacyAbyssManager extends DataObject<LegacyAbyssManager> {
   public static final FieldRegistry FIELDS = new FieldRegistry();

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }
}
