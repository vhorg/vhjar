package iskallia.vault.bounty.task;

import iskallia.vault.VaultMod;
import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.bounty.task.properties.ItemDiscoveryProperties;
import iskallia.vault.core.event.common.ChestGenerationEvent;
import iskallia.vault.core.event.common.CoinStacksGenerationEvent;
import iskallia.vault.core.event.common.LootableBlockGenerationEvent;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.bounty.ClientboundBountyCompleteMessage;
import iskallia.vault.world.data.BountyData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemDiscoveryTask extends Task<ItemDiscoveryProperties> {
   private List<ItemStack> cachedItems;
   static long time = 0L;
   static int iterations = 100;
   static List<Long> times = new ArrayList<>();

   public ItemDiscoveryTask(UUID bountyId, ItemDiscoveryProperties properties, TaskReward taskReward) {
      super(TaskRegistry.ITEM_DISCOVERY, bountyId, properties, taskReward);
   }

   public ItemDiscoveryTask(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public void setCachedItems(List<ItemStack> cachedItems) {
      this.cachedItems = cachedItems;
   }

   @Override
   protected <E> boolean doValidate(ServerPlayer player, E event) {
      if (this.cachedItems != null && !this.cachedItems.isEmpty()) {
         for (ItemStack stack : this.cachedItems) {
            Item item = stack.getItem();
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            ResourceLocation requiredItem = this.getProperties().getItemId();
            if (itemId != null && itemId.equals(requiredItem)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
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

   public static <T> void onLootGeneration(T event) {
      time = System.currentTimeMillis();
      ServerPlayer player;
      List<ItemStack> items;
      if (event instanceof ChestGenerationEvent.Data e) {
         player = e.getPlayer();
         items = e.getLoot();
      } else if (event instanceof LivingDropsEvent e) {
         if (!(e.getSource().getEntity() instanceof ServerPlayer)) {
            return;
         }

         player = (ServerPlayer)e.getSource().getEntity();
         items = e.getDrops().stream().<ItemStack>map(ItemEntity::getItem).toList();
      } else if (event instanceof CoinStacksGenerationEvent.Data e) {
         player = e.getPlayer();
         items = e.getLoot();
      } else {
         if (!(event instanceof LootableBlockGenerationEvent.Data e)) {
            VaultMod.LOGGER.warn("Attempted to validate an unregistered event.");
            return;
         }

         player = e.getPlayer();
         items = e.getLoot();
      }

      BountyData data = BountyData.get();

      for (ItemDiscoveryTask task : data.getAllActiveTasksById(player, TaskRegistry.ITEM_DISCOVERY)) {
         if (!task.isComplete()) {
            task.setCachedItems(items);
            if (task.validate(player, event)) {
               for (ItemStack stack : items) {
                  Item item = stack.getItem();
                  ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
                  ResourceLocation requiredItem = task.getProperties().getItemId();
                  if (itemId != null && itemId.equals(requiredItem)) {
                     task.increment(stack.getCount());
                     if (task.isComplete()) {
                        ModNetwork.CHANNEL
                           .sendTo(new ClientboundBountyCompleteMessage(task.taskType), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                        break;
                     }
                  }
               }
            }
         }
      }

      if (times.size() < iterations) {
         times.add(System.currentTimeMillis() - time);
      } else {
         long average = (long)times.stream().mapToLong(value -> value).average().orElse(0.0);
         VaultMod.LOGGER.debug("ItemDiscovery Last {} Iteration Time: {}ms", iterations, average);
         times.clear();
      }
   }
}
