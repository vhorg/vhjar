package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.PartialNBT;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityGroupsConfig extends Config {
   @Expose
   private Map<ResourceLocation, Set<String>> groups;

   @Override
   public String getName() {
      return "entity_groups";
   }

   public boolean isInGroup(ResourceLocation groupId, Vec3 pos, BlockPos blockPos, PartialNBT nbt) {
      for (String predicate : this.groups.get(groupId)) {
         if (EntityPredicate.of(predicate).test(pos, blockPos, nbt)) {
            return true;
         }
      }

      return false;
   }

   public boolean isInGroup(ResourceLocation groupId, Entity entity) {
      return this.isInGroup(groupId, entity.position(), entity.blockPosition(), PartialNBT.of(entity.serializeNBT()));
   }

   public boolean isInGroup(ResourceLocation groupId, PartialEntity entity) {
      return this.isInGroup(groupId, entity.getPos(), entity.getBlockPos(), entity.getNBT());
   }

   @Override
   protected void reset() {
      this.groups = new LinkedHashMap<>();
   }
}
