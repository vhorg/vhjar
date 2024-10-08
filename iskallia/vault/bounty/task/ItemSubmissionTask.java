package iskallia.vault.bounty.task;

import iskallia.vault.block.BountyBlock;
import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.bounty.task.properties.ItemSubmissionProperties;
import iskallia.vault.world.data.BountyData;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class ItemSubmissionTask extends Task<ItemSubmissionProperties> {
   public ItemSubmissionTask(UUID bountyId, ItemSubmissionProperties properties, TaskReward taskReward) {
      super(TaskRegistry.ITEM_SUBMISSION, bountyId, properties, taskReward);
   }

   public ItemSubmissionTask(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   @Override
   protected <E> boolean doValidate(ServerPlayer player, E event) {
      if (!(event instanceof RightClickBlock e)) {
         return false;
      } else {
         Block block = e.getWorld().getBlockState(e.getPos()).getBlock();
         if (e.getHand() != InteractionHand.MAIN_HAND) {
            return false;
         } else if (!(block instanceof BountyBlock bountyTable)) {
            return false;
         } else {
            ItemStack stack = e.getItemStack();
            Item item = stack.getItem();
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            return id != null && id.equals(this.getProperties().getItemId());
         }
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
      this.properties = new ItemSubmissionProperties(tag.getCompound("properties"));
   }

   @SubscribeEvent
   public static void onInteractWithBountyTable(RightClickBlock event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         BountyData data = BountyData.get();

         for (Task<?> task : data.getAllLegendaryById(player, TaskRegistry.ITEM_SUBMISSION).stream().filter(taskx -> !taskx.isComplete()).toList()) {
            if (task.validate(player, event)) {
               event.setCanceled(true);
               ItemStack stack = event.getItemStack();
               ItemStack match = event.getItemStack().copy();
               int amount = stack.getCount();
               double remainder = Math.max(task.getAmountObtained() + amount - task.getProperties().getAmount(), 0.0);
               task.increment(amount - remainder);
               stack.setCount((int)remainder);
               event.getPlayer()
                  .getLevel()
                  .playSound(
                     null,
                     event.getPos(),
                     SoundEvents.ITEM_PICKUP,
                     SoundSource.PLAYERS,
                     0.7F,
                     ((event.getPlayer().getRandom().nextFloat() - event.getPlayer().getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                  );
               if (remainder == 0.0) {
                  Inventory inventory = event.getPlayer().getInventory();
                  int slot = inventory.findSlotMatchingItem(match);
                  if (slot < 0) {
                     return;
                  }

                  ItemStack newStack = inventory.getItem(slot).copy();
                  if (newStack.isEmpty()) {
                     return;
                  }

                  inventory.setItem(slot, ItemStack.EMPTY);
                  inventory.setItem(event.getPlayer().getInventory().selected, newStack);
               }

               if (task.isComplete()) {
                  task.complete(player);
               }

               return;
            }
         }

         for (Task<?> taskx : data.getAllActiveById(player, TaskRegistry.ITEM_SUBMISSION).stream().filter(taskxx -> !taskxx.isComplete()).toList()) {
            if (taskx.validate(player, event)) {
               event.setCanceled(true);
               ItemStack stackx = event.getItemStack();
               ItemStack matchx = event.getItemStack().copy();
               int amountx = stackx.getCount();
               double remainderx = Math.max(taskx.getAmountObtained() + amountx - taskx.getProperties().getAmount(), 0.0);
               taskx.increment(amountx - remainderx);
               stackx.setCount((int)remainderx);
               event.getPlayer()
                  .getLevel()
                  .playSound(
                     null,
                     event.getPos(),
                     SoundEvents.ITEM_PICKUP,
                     SoundSource.PLAYERS,
                     0.7F,
                     ((event.getPlayer().getRandom().nextFloat() - event.getPlayer().getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                  );
               if (remainderx == 0.0) {
                  Inventory inventoryx = event.getPlayer().getInventory();
                  int slotx = inventoryx.findSlotMatchingItem(matchx);
                  if (slotx < 0) {
                     return;
                  }

                  ItemStack newStack = inventoryx.getItem(slotx).copy();
                  if (newStack.isEmpty()) {
                     return;
                  }

                  inventoryx.setItem(slotx, ItemStack.EMPTY);
                  inventoryx.setItem(event.getPlayer().getInventory().selected, newStack);
               }

               if (taskx.isComplete()) {
                  taskx.complete(player);
               }

               return;
            }
         }
      }
   }
}
