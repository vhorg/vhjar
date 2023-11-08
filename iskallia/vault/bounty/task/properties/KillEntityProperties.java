package iskallia.vault.bounty.task.properties;

import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class KillEntityProperties extends TaskProperties {
   private EntityPredicate filter;

   public KillEntityProperties(EntityPredicate filter, List<ResourceLocation> validDimensions, boolean isVaultOnly, double amount) {
      super(TaskRegistry.KILL_ENTITY, validDimensions, isVaultOnly, amount);
      this.filter = filter;
   }

   public KillEntityProperties(CompoundTag tag) {
      super(tag);
      this.deserializeNBT(tag);
   }

   public EntityPredicate getFilter() {
      return this.filter;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      Adapters.ENTITY_PREDICATE.writeNbt(this.filter).ifPresent(predicateTag -> tag.put("filter", predicateTag));
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      if (tag.contains("entityId")) {
         this.filter = EntityPredicate.of(tag.getString("entityId"), true).orElse(EntityPredicate.TRUE);
      } else {
         this.filter = Adapters.ENTITY_PREDICATE.readNbt(tag.get("filter")).orElse(EntityPredicate.TRUE);
      }
   }
}
