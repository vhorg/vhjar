package iskallia.vault.bounty.task.properties;

import iskallia.vault.util.nbt.NBTHelper;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class TaskProperties implements INBTSerializable<CompoundTag> {
   protected ResourceLocation taskType;
   protected List<ResourceLocation> validDimensions;
   private boolean vaultOnly;
   private double amount;

   protected TaskProperties(ResourceLocation taskType, List<ResourceLocation> validDimensions, boolean vaultOnly, double amount) {
      this.taskType = taskType;
      this.validDimensions = validDimensions;
      this.vaultOnly = vaultOnly;
      this.amount = amount;
   }

   public TaskProperties(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public ResourceLocation getTaskType() {
      return this.taskType;
   }

   public boolean isVaultOnly() {
      return this.vaultOnly;
   }

   public double getAmount() {
      return this.amount;
   }

   public List<ResourceLocation> getValidDimensions() {
      return this.validDimensions;
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putString("taskType", this.taskType.toString());
      NBTHelper.writeCollection(tag, "validDimensions", this.validDimensions, StringTag.class, id -> StringTag.valueOf(id.toString()));
      tag.putBoolean("vaultOnly", this.vaultOnly);
      tag.putDouble("amount", this.amount);
      return tag;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.taskType = new ResourceLocation(nbt.getString("taskType"));
      this.validDimensions = NBTHelper.readList(nbt, "validDimensions", StringTag.class, stringTag -> new ResourceLocation(stringTag.getAsString()));
      this.vaultOnly = nbt.getBoolean("vaultOnly");
      this.amount = nbt.getDouble("amount");
   }
}
