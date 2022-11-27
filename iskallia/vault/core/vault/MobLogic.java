package iskallia.vault.core.vault;

import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.world.storage.VirtualWorld;

public abstract class MobLogic extends DataObject<MobLogic> implements ISupplierKey<MobLogic> {
   public static final FieldRegistry FIELDS = new FieldRegistry();

   public void initServer(VirtualWorld world, Vault vault) {
   }

   public void releaseServer() {
      CommonEvents.release(this);
   }
}
