package iskallia.vault.bounty.task;

import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.bounty.task.properties.TaskProperties;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.bounty.ClientboundBountyCompleteMessage;
import iskallia.vault.world.data.BountyData;
import iskallia.vault.world.data.ServerVaults;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;

public abstract class Task<P extends TaskProperties> implements INBTSerializable<CompoundTag> {
   protected ResourceLocation taskType;
   protected UUID bountyId;
   protected P properties;
   protected TaskReward taskReward;
   protected double amountObtained;

   protected Task(ResourceLocation taskType, UUID bountyId, P properties, TaskReward taskReward) {
      this.taskType = taskType;
      this.bountyId = bountyId;
      this.properties = properties;
      this.taskReward = taskReward;
   }

   protected Task() {
   }

   protected abstract <E> boolean doValidate(ServerPlayer var1, E var2);

   public <E> boolean validate(ServerPlayer player, E event) {
      return this.inValidDimension(player) && this.doValidate(player, event);
   }

   public void increment(double amount) {
      this.doIncrement(amount);
      BountyData.get().setDirty();
   }

   protected void doIncrement(double amount) {
      this.amountObtained = (int)Math.min(this.properties.getAmount(), this.amountObtained + amount);
   }

   public abstract boolean isComplete();

   protected void complete(ServerPlayer player) {
      ModNetwork.CHANNEL.sendTo(new ClientboundBountyCompleteMessage(this.taskType), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public UUID getBountyId() {
      return this.bountyId;
   }

   public ResourceLocation getTaskType() {
      return this.taskType;
   }

   public P getProperties() {
      return this.properties;
   }

   public TaskReward getTaskReward() {
      return this.taskReward;
   }

   public double getAmountObtained() {
      return this.amountObtained;
   }

   public boolean inValidDimension(ServerPlayer serverPlayer) {
      return this.getProperties().isVaultOnly()
         ? this.getTaskType().equals(TaskRegistry.COMPLETION) || ServerVaults.get(serverPlayer.level).isPresent()
         : this.getProperties().getValidDimensions().isEmpty()
            || this.getProperties().getValidDimensions().contains(serverPlayer.getLevel().dimension().location());
   }

   public static <T extends Task<P>, P extends TaskProperties> T fromTag(CompoundTag tag) {
      ResourceLocation id = new ResourceLocation(tag.getString("taskType"));
      if (id.equals(TaskRegistry.KILL_ENTITY)) {
         return (T)(new KillEntityTask(tag));
      } else if (id.equals(TaskRegistry.DAMAGE_ENTITY)) {
         return (T)(new DamageTask(tag));
      } else if (id.equals(TaskRegistry.COMPLETION)) {
         return (T)(new CompletionTask(tag));
      } else if (id.equals(TaskRegistry.ITEM_SUBMISSION)) {
         return (T)(new ItemSubmissionTask(tag));
      } else if (id.equals(TaskRegistry.ITEM_DISCOVERY)) {
         return (T)(new ItemDiscoveryTask(tag));
      } else if (id.equals(TaskRegistry.MINING)) {
         return (T)(new MiningTask(tag));
      } else {
         throw new IllegalArgumentException("No task found based on the taskType: " + id);
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putString("taskType", this.taskType.toString());
      tag.putString("bountyId", this.bountyId.toString());
      tag.put("reward", this.taskReward.serializeNBT());
      tag.putDouble("amountObtained", this.amountObtained);
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.taskType = new ResourceLocation(tag.getString("taskType"));
      this.bountyId = UUID.fromString(tag.getString("bountyId"));
      this.taskReward = new TaskReward(tag.getCompound("reward"));
      this.amountObtained = tag.getDouble("amountObtained");
   }
}
