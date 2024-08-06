package iskallia.vault.antique.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class DropConditionContext {
   private final int level;
   private final DropConditionType type;
   private final ResourceLocation ownerKey;
   private final List<String> tags = new ArrayList<>();

   public DropConditionContext(int level, DropConditionType type, ResourceLocation ownerKey) {
      this.level = level;
      this.type = type;
      this.ownerKey = ownerKey;
   }

   public void addTag(String tag) {
      this.tags.add(tag);
   }

   public void addTags(List<String> tags) {
      this.tags.addAll(tags);
   }

   public ResourceLocation getOwnerKey() {
      return this.ownerKey;
   }

   public List<String> getTags() {
      return Collections.unmodifiableList(this.tags);
   }

   public int getLevel() {
      return this.level;
   }

   public DropConditionType getType() {
      return this.type;
   }
}
