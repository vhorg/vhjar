package iskallia.vault.core.vault;

import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.ChestGenerationEvent;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.world.storage.VirtualWorld;

public abstract class LootLogic extends DataObject<LootLogic> implements ISupplierKey<LootLogic> {
   public static final FieldRegistry FIELDS = new FieldRegistry();

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.CHEST_LOOT_GENERATION.pre().register(vault, data -> {
         if (data.getTileEntity().getLevel() == world) {
            JavaRandom random = JavaRandom.ofInternal(vault.get(Vault.SEED));
            long a = random.nextLong() | 1L;
            long b = random.nextLong() | 1L;
            long c = random.nextLong() | 1L;
            int x = data.getPos().getX();
            int y = data.getPos().getY();
            int z = data.getPos().getZ();
            random.setSeed(a * x + b * y + c + z ^ vault.get(Vault.SEED));
            data.setRandom(random);
            this.onPreGenerate(world, vault, data);
         }
      });
      CommonEvents.CHEST_LOOT_GENERATION.post().register(vault, data -> {
         if (data.getTileEntity().getLevel() == world) {
            this.onPostGenerate(world, vault, data);
         }
      });
   }

   protected abstract void onPreGenerate(VirtualWorld var1, Vault var2, ChestGenerationEvent.Data var3);

   protected abstract void onPostGenerate(VirtualWorld var1, Vault var2, ChestGenerationEvent.Data var3);
}
