package iskallia.vault.core.data.compound;

import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.adapter.Adapter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDList extends DataList<UUIDList, UUID> {
   protected UUIDList(List<UUID> delegate, Adapter<UUID> adapter) {
      super(delegate, adapter);
   }

   public static UUIDList create() {
      return new UUIDList(new ArrayList<>(), Adapter.ofUUID());
   }
}
