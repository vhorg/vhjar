package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class TemplateStackSpawnerProcessor extends TagListProcessor<TemplateStackSpawnerProcessor> {
   private final List<String> stack = new ArrayList<>();

   public TemplateStackSpawnerProcessor stack(String stack) {
      this.stack.add(stack);
      return this;
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      if (this.predicate.test(tile)) {
         tile.getEntity().asWhole().ifPresent(tag -> {
            if (tag.contains("Inventory", 10)) {
               tag = tag.getCompound("Inventory");
               if (tag.contains("Stacks", 9)) {
                  ListTag stacks = tag.getList("Stacks", 10);

                  for (int i = 0; i < stacks.size(); i++) {
                     CompoundTag stack = stacks.getCompound(i);
                     CompoundTag stackTag = stack.getCompound("tag");
                     stack.put("tag", stackTag);
                     CompoundTag entityTag = stackTag.getCompound("EntityTag");
                     stackTag.put("EntityTag", entityTag);
                     CompoundTag forgeDataTag = entityTag.getCompound("ForgeData");
                     entityTag.put("ForgeData", forgeDataTag);
                     this.addTags(forgeDataTag, this.stack);
                  }
               }
            }
         });
      }

      return tile;
   }
}
