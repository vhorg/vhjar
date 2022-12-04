package iskallia.vault.bounty.task;

import iskallia.vault.VaultMod;
import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.bounty.task.properties.CompletionProperties;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.stat.StatsCollector;
import iskallia.vault.event.event.VaultJoinForgeEvent;
import iskallia.vault.event.event.VaultLeaveForgeEvent;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.bounty.ClientboundBountyCompleteMessage;
import iskallia.vault.world.data.BountyData;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;

@EventBusSubscriber
public class CompletionTask extends Task<CompletionProperties> {
   public CompletionTask(UUID bountyId, CompletionProperties properties, TaskReward taskReward) {
      super(TaskRegistry.COMPLETION, bountyId, properties, taskReward);
   }

   public CompletionTask(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   @Override
   public boolean inValidDimension(ServerPlayer serverPlayer) {
      return true;
   }

   @Override
   protected <E> boolean doValidate(ServerPlayer player, E event) {
      if (event instanceof VaultLeaveForgeEvent e) {
         Objectives objectives = e.getVault().get(Vault.OBJECTIVES);
         if (!this.getProperties().getId().equals(VaultMod.id("vault"))
            && objectives.get(Objectives.LIST).stream().noneMatch(objective -> isValidObjective(this.getProperties().getId(), objective))) {
            return false;
         } else if (e.getVault().has(Vault.STATS)) {
            StatsCollector statCollector = e.getVault().get(Vault.STATS);
            StatCollector stats = statCollector.get(player.getUUID());
            return stats.getCompletion() == Completion.COMPLETED;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean isComplete() {
      return this.amountObtained >= this.properties.getAmount();
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.put("properties", this.properties.serializeNBT());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.properties = new CompletionProperties(tag.getCompound("properties"));
   }

   @SubscribeEvent
   public static void onVaultLeave(VaultLeaveForgeEvent event) {
      BountyData.get()
         .getAllActiveTasksById(event.getPlayer(), TaskRegistry.COMPLETION)
         .stream()
         .filter(task -> !task.isComplete())
         .filter(task -> task.validate(event.getPlayer(), event))
         .peek(task -> task.increment(1.0))
         .filter(Task::isComplete)
         .forEach(
            task -> ModNetwork.CHANNEL
               .sendTo(new ClientboundBountyCompleteMessage(task.taskType), event.getPlayer().connection.connection, NetworkDirection.PLAY_TO_CLIENT)
         );
   }

   @SubscribeEvent
   public static void onVaultEnter(VaultJoinForgeEvent event) {
   }

   private static boolean isValidObjective(ResourceLocation id, Objective objective) {
      SupplierKey<Objective> key = objective.getKey();
      ResourceLocation objectiveId = key.getId();
      return objectiveId.equals(id);
   }
}
