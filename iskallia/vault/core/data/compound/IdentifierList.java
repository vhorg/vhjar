package iskallia.vault.core.data.compound;

import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.adapter.Adapter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class IdentifierList extends DataList<IdentifierList, ResourceLocation> {
   protected IdentifierList(List<ResourceLocation> delegate, Adapter<ResourceLocation> adapter) {
      super(delegate, adapter);
   }

   public static IdentifierList create() {
      return new IdentifierList(new ArrayList<>(), Adapter.ofIdentifier());
   }
}
