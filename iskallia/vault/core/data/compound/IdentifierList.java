package iskallia.vault.core.data.compound;

import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class IdentifierList extends DataList<IdentifierList, ResourceLocation> {
   protected IdentifierList(List<ResourceLocation> delegate, IBitAdapter<ResourceLocation, ?> adapter) {
      super(delegate, adapter);
   }

   public static IdentifierList create() {
      return new IdentifierList(new ArrayList<>(), Adapters.IDENTIFIER);
   }
}
