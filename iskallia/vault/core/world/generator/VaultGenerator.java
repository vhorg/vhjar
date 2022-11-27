package iskallia.vault.core.world.generator;

import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.ISupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class VaultGenerator extends DataObject<VaultGenerator> implements ISupplierKey<GridGenerator> {
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.SURFACE_GENERATION.in(world).register(vault, data -> this.generate(vault, data.getGenRegion(), data.getChunk().getPos()));
   }

   public void tickServer(VirtualWorld world, Vault vault) {
   }

   public abstract void generate(Vault var1, ServerLevelAccessor var2, ChunkPos var3);
}
