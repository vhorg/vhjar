package iskallia.vault.bounty.task.properties;

import iskallia.vault.bounty.TaskRegistry;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class KillEntityProperties extends TaskProperties {
   private ResourceLocation entityId;

   public KillEntityProperties(ResourceLocation entityId, List<ResourceLocation> validDimensions, boolean isVaultOnly, double amount) {
      super(TaskRegistry.KILL_ENTITY, validDimensions, isVaultOnly, amount);
      this.entityId = entityId;
   }

   public KillEntityProperties(CompoundTag tag) {
      super(tag);
      this.deserializeNBT(tag);
   }

   public ResourceLocation getEntityId() {
      return this.entityId;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putString("entityId", this.entityId.toString());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.entityId = new ResourceLocation(tag.getString("entityId"));
   }
}
