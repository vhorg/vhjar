package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.Version;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.resources.ResourceLocation;

public class ReferenceTileProcessor extends TileProcessor {
   private final ResourceLocation id;

   public ReferenceTileProcessor(ResourceLocation id) {
      this.id = id;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public PartialTile process(PartialTile value, ProcessorContext context) {
      Version version = context.vault == null ? Version.latest() : context.vault.get(Vault.VERSION);
      Palette palette = VaultRegistry.PALETTE.getKey(this.id).get(version);

      for (TileProcessor child : palette.getTileProcessors()) {
         value = child.process(value, context);
         if (value == null) {
            break;
         }
      }

      return value;
   }
}
