package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.Version;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.resources.ResourceLocation;

public class ReferenceTileProcessor extends TileProcessor {
   private final WeightedList<ResourceLocation> pool;

   public ReferenceTileProcessor(WeightedList<ResourceLocation> pool) {
      this.pool = pool;
   }

   public WeightedList<ResourceLocation> getPool() {
      return this.pool;
   }

   public PartialTile process(PartialTile value, ProcessorContext context) {
      Version version = context.getVault() == null ? Version.latest() : context.getVault().get(Vault.VERSION);
      ResourceLocation reference = this.pool.getRandom(context.getRandom(value.getPos())).orElse(null);
      Palette palette = VaultRegistry.PALETTE.getKey(reference).get(version);

      for (TileProcessor child : palette.getTileProcessors()) {
         value = child.process(value, context);
         if (value == null) {
            break;
         }
      }

      return value;
   }
}
