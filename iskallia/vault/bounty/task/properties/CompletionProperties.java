package iskallia.vault.bounty.task.properties;

import iskallia.vault.bounty.TaskRegistry;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class CompletionProperties extends TaskProperties {
   private ResourceLocation id;

   public CompletionProperties(ResourceLocation id, List<ResourceLocation> validDimensions, boolean isVaultOnly, double amount) {
      super(TaskRegistry.COMPLETION, validDimensions, isVaultOnly, amount);
      this.id = id;
   }

   public CompletionProperties(CompoundTag tag) {
      super(tag);
      this.deserializeNBT(tag);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putString("id", this.id.toString());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.id = new ResourceLocation(tag.getString("id"));
   }
}
