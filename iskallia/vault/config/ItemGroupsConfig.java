package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.data.item.PartialItem;
import iskallia.vault.core.world.data.item.PartialStack;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class ItemGroupsConfig extends Config {
   @Expose
   private Map<ResourceLocation, Set<ItemPredicate>> groups;

   @Override
   public String getName() {
      return "item_groups";
   }

   public boolean isInGroup(ResourceLocation groupId, PartialItem item, PartialCompoundNbt nbt) {
      for (ItemPredicate predicate : this.groups.getOrDefault(groupId, new HashSet<>())) {
         if (predicate.test(item, nbt)) {
            return true;
         }
      }

      return false;
   }

   public boolean isInGroup(ResourceLocation groupId, PartialStack stack) {
      return this.isInGroup(groupId, stack.getItem(), stack.getNbt());
   }

   @Override
   protected void reset() {
      this.groups = new LinkedHashMap<>();
   }
}
