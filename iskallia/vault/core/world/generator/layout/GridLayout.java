package iskallia.vault.core.world.generator.layout;

import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;

public abstract class GridLayout extends DataObject<GridLayout> implements ISupplierKey<GridLayout>, VaultLayout {
   public static final FieldRegistry FIELDS = new FieldRegistry();

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public abstract void initServer(VirtualWorld var1, Vault var2, GridGenerator var3);

   public abstract void releaseServer();

   public abstract Template getAt(Vault var1, RegionPos var2, RandomSource var3, PlacementSettings var4);
}
