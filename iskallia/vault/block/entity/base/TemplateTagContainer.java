package iskallia.vault.block.entity.base;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public interface TemplateTagContainer {
   String TEMPLATE_TAG = "template_tags";

   List<String> getTemplateTags();

   default List<String> loadTemplateTags(CompoundTag nbt) {
      List<String> templateTags = new ArrayList<>();
      if (nbt.contains("template_tags", 9)) {
         for (Tag tag : nbt.getList("template_tags", 8)) {
            templateTags.add(tag.getAsString());
         }
      }

      return templateTags;
   }

   default void saveTemplateTags(CompoundTag nbt) {
      ListTag stack = new ListTag();

      for (String path : this.getTemplateTags()) {
         stack.add(StringTag.valueOf(path));
      }

      nbt.put("template_tags", stack);
   }
}
