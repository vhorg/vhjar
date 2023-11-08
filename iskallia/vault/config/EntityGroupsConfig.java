package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.entity.PartialEntity;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityGroupsConfig extends Config {
   @Expose
   private Map<ResourceLocation, Set<EntityPredicate>> groups;

   @Override
   public String getName() {
      return "entity_groups";
   }

   public boolean isInGroup(ResourceLocation groupId, Vec3 pos, BlockPos blockPos, PartialCompoundNbt nbt) {
      for (EntityPredicate predicate : this.groups.getOrDefault(groupId, new HashSet<>())) {
         if (predicate.test(pos, blockPos, nbt)) {
            return true;
         }
      }

      return false;
   }

   public boolean isInGroup(ResourceLocation groupId, Entity entity) {
      return this.isInGroup(groupId, entity.position(), entity.blockPosition(), PartialCompoundNbt.of(entity.serializeNBT()));
   }

   public boolean isInGroup(ResourceLocation groupId, PartialEntity entity) {
      return this.isInGroup(groupId, entity.getPos(), entity.getBlockPos(), entity.getNbt());
   }

   @Override
   protected void reset() {
      this.groups = new LinkedHashMap<>();
   }

   public Map<ResourceLocation, Set<EntityPredicate>> getGroups() {
      return this.groups;
   }
}
