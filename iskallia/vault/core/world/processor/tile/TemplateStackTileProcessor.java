package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

public class TemplateStackTileProcessor extends TargetTileProcessor<TemplateStackTileProcessor> {
   private String path;
   private List<String> stack = new ArrayList<>();

   public TemplateStackTileProcessor path(String path) {
      this.path = path;
      return this;
   }

   public TemplateStackTileProcessor stack(String stack) {
      this.stack.add(stack);
      return this;
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      if (this.predicate.test(tile)) {
         String[] paths = this.path.split(Pattern.quote("/"));
         CompoundTag nbt = tile.getEntity().asWhole().orElseGet(CompoundTag::new);

         for (int i = 0; i < paths.length - 1; i++) {
            CompoundTag child = nbt.getCompound(paths[i]);
            nbt.put(paths[i], child);
            nbt = child;
         }

         ListTag stack = nbt.getList(paths[paths.length - 1], 8);

         for (String path : this.stack) {
            stack.add(StringTag.valueOf(path));
         }

         nbt.put(paths[paths.length - 1], stack);
         tile.setEntity(PartialCompoundNbt.of(nbt));
      }

      return tile;
   }
}
