package iskallia.vault.bounty.task.properties;

import iskallia.vault.bounty.TaskRegistry;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ItemSubmissionProperties extends TaskProperties {
   private ResourceLocation itemId;

   public ItemSubmissionProperties(ResourceLocation itemId, List<ResourceLocation> validDimensions, boolean vaultOnly, double amount) {
      super(TaskRegistry.ITEM_SUBMISSION, validDimensions, vaultOnly, amount);
      this.itemId = itemId;
   }

   public ItemSubmissionProperties(CompoundTag tag) {
      super(tag);
      this.deserializeNBT(tag);
   }

   public ResourceLocation getItemId() {
      return this.itemId;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putString("itemId", this.itemId.toString());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.itemId = new ResourceLocation(tag.getString("itemId"));
   }
}
