package iskallia.vault.core.data.compound;

import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDList extends DataList<UUIDList, UUID> {
   protected UUIDList(List<UUID> delegate, IBitAdapter<UUID, ?> adapter) {
      super(delegate, adapter);
   }

   public static UUIDList create() {
      return new UUIDList(new ArrayList<>(), Adapters.UUID);
   }
}
