package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;

public class TemplateStackTileProcessor extends TagListProcessor<TemplateStackTileProcessor> {
   private final List<String> stack = new ArrayList<>();

   public TemplateStackTileProcessor stack(String stack) {
      this.stack.add(stack);
      return this;
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      if (this.predicate.test(tile)) {
         CompoundTag nbt = tile.getEntity().asWhole().orElseGet(CompoundTag::new);
         this.addTags(nbt, this.stack);
         tile.setEntity(PartialCompoundNbt.of(nbt));
      }

      return tile;
   }
}
