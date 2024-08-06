package iskallia.vault.core.world.processor.tile;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

public abstract class TagListProcessor<T extends TagListProcessor<T>> extends TargetTileProcessor<T> {
   protected void addTags(CompoundTag baseTag, List<String> tags) {
      ListTag stack = baseTag.getList("template_tags", 8);

      for (String path : tags) {
         stack.add(StringTag.valueOf(path));
      }

      baseTag.put("template_tags", stack);
   }
}
