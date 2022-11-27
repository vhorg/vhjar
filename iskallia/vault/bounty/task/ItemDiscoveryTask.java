package iskallia.vault.bounty.task;

import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.bounty.task.properties.ItemDiscoveryProperties;
import iskallia.vault.core.event.common.LootGenerationEvent;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.bounty.ClientboundBountyCompleteMessage;
import iskallia.vault.world.data.BountyData;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemDiscoveryTask extends Task<ItemDiscoveryProperties> {
   public ItemDiscoveryTask(UUID bountyId, ItemDiscoveryProperties properties, TaskReward taskReward) {
      super(TaskRegistry.ITEM_DISCOVERY, bountyId, properties, taskReward);
   }

   public ItemDiscoveryTask(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   @Override
   protected <E> boolean doValidate(ServerPlayer player, E event) {
      AtomicBoolean valid = new AtomicBoolean(false);
      if (event instanceof LootGenerationEvent.Data data) {
         LootTableGenerator lootTableGenerator = (LootTableGenerator)data.getGenerator();
         Iterator<ItemStack> items = lootTableGenerator.getItems();
         items.forEachRemaining(stack -> {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
            ResourceLocation requiredItem = this.getProperties().getItemId();
            if (itemId != null && itemId.equals(requiredItem)) {
               valid.set(true);
            }
         });
      }

      return valid.get();
   }

   @Override
   public boolean isComplete() {
      return this.amountObtained >= this.getProperties().getAmount();
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
      this.properties = new ItemDiscoveryProperties(tag.getCompound("properties"));
   }

   public static void onLootGeneration(LootGenerationEvent.Data event) {
      LootTableGenerator generator = (LootTableGenerator)event.getGenerator();
      if (generator.source instanceof ServerPlayer player && event.getPhase() == LootGenerationEvent.Phase.POST) {
         BountyData data = BountyData.get();
         data.getAllActiveTasksById(player, TaskRegistry.ITEM_DISCOVERY)
            .stream()
            .filter(task -> !task.isComplete())
            .filter(task -> task.validate(player, event))
            .peek(task -> {
               AtomicInteger count = new AtomicInteger();
               Iterator<ItemStack> items = event.getGenerator().getItems();
               items.forEachRemaining(stack -> {
                  ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
                  ResourceLocation requiredItem = ((ItemDiscoveryTask)task).getProperties().getItemId();
                  if (itemId != null && itemId.equals(requiredItem)) {
                     count.addAndGet(stack.getCount());
                  }
               });
               task.increment(count.get());
            })
            .filter(Task::isComplete)
            .forEach(
               task -> ModNetwork.CHANNEL
                  .sendTo(new ClientboundBountyCompleteMessage(task.taskType), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
            );
      }
   }
}
