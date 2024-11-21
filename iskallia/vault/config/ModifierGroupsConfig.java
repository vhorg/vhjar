package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.modifier.spi.predicate.ModifierPredicate;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class ModifierGroupsConfig extends Config {
   @Expose
   private Map<ResourceLocation, ModifierPredicate> groups;

   @Override
   public String getName() {
      return "modifier_groups";
   }

   public boolean isInGroup(ResourceLocation groupId, VaultModifier<?> modifier) {
      ModifierPredicate predicate = this.groups.get(groupId);
      return predicate != null ? predicate.test(modifier) : false;
   }

   @Override
   protected void reset() {
      this.groups = new LinkedHashMap<>();
   }
}
