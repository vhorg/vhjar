package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.ChestGenerationEvent;
import iskallia.vault.core.event.common.LootableBlockGenerationEvent;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.core.BlockPos;

public abstract class LootLogic extends DataObject<LootLogic> implements ISupplierKey<LootLogic> {
   public static final FieldRegistry FIELDS = new FieldRegistry();

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.CHEST_LOOT_GENERATION.pre().register(vault, data -> {
         if (data.getTileEntity().getLevel() == world) {
            data.setVersion(vault.get(Vault.VERSION));
            data.setRandom(this.createPositionRandom(vault, data.getPos()));
            this.onChestPreGenerate(world, vault, data);
         }
      });
      CommonEvents.CHEST_LOOT_GENERATION.post().register(vault, data -> {
         if (data.getTileEntity().getLevel() == world) {
            this.onChestPostGenerate(world, vault, data);
         }
      });
      CommonEvents.LOOTABLE_BLOCK_GENERATION_EVENT.pre().register(vault, data -> {
         if (data.getTileEntity().getLevel() == world) {
            data.setVersion(vault.get(Vault.VERSION));
            data.setRandom(this.createPositionRandom(vault, data.getPos()));
            this.onBlockPreGenerate(world, vault, data);
         }
      });
      CommonEvents.LOOTABLE_BLOCK_GENERATION_EVENT.post().register(vault, data -> {
         if (data.getTileEntity().getLevel() == world) {
            this.onBlockPostGenerate(world, vault, data);
         }
      });
   }

   private JavaRandom createPositionRandom(Vault vault, BlockPos at) {
      JavaRandom random = JavaRandom.ofInternal(vault.get(Vault.SEED));
      long a = random.nextLong() | 1L;
      long b = random.nextLong() | 1L;
      long c = random.nextLong() | 1L;
      int x = at.getX();
      int y = at.getY();
      int z = at.getZ();
      if (vault.get(Vault.VERSION).isOlderThan(Version.v1_5)) {
         random.setSeed(a * x + b * y + c + z ^ vault.get(Vault.SEED));
      } else {
         random.setSeed(a * x + b * y + c * z ^ vault.get(Vault.SEED));
      }

      return random;
   }

   protected abstract void onChestPreGenerate(VirtualWorld var1, Vault var2, ChestGenerationEvent.Data var3);

   protected abstract void onChestPostGenerate(VirtualWorld var1, Vault var2, ChestGenerationEvent.Data var3);

   protected abstract void onBlockPreGenerate(VirtualWorld var1, Vault var2, LootableBlockGenerationEvent.Data var3);

   protected abstract void onBlockPostGenerate(VirtualWorld var1, Vault var2, LootableBlockGenerationEvent.Data var3);
}
