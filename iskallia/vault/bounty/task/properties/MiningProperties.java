package iskallia.vault.bounty.task.properties;

import iskallia.vault.bounty.TaskRegistry;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class MiningProperties extends TaskProperties {
   private ResourceLocation blockId;

   public MiningProperties(ResourceLocation blockId, List<ResourceLocation> validDimensions, boolean vaultOnly, double amount) {
      super(TaskRegistry.MINING, validDimensions, vaultOnly, amount);
      this.blockId = blockId;
   }

   public MiningProperties(CompoundTag tag) {
      super(tag);
      this.deserializeNBT(tag);
   }

   public ResourceLocation getBlockId() {
      return this.blockId;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putString("blockId", this.blockId.toString());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.blockId = new ResourceLocation(tag.getString("blockId"));
   }
}
