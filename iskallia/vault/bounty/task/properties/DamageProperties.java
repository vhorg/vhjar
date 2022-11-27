package iskallia.vault.bounty.task.properties;

import iskallia.vault.bounty.TaskRegistry;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class DamageProperties extends TaskProperties {
   private ResourceLocation entityId;

   public DamageProperties(ResourceLocation entityId, List<ResourceLocation> validDimensions, boolean vaultOnly, double amount) {
      super(TaskRegistry.DAMAGE_ENTITY, validDimensions, vaultOnly, amount);
      this.entityId = entityId;
   }

   public DamageProperties(CompoundTag tag) {
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
