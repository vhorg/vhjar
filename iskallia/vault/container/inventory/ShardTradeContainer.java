package iskallia.vault.container.inventory;

import iskallia.vault.client.ClientShardTradeData;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.container.base.AbstractPlayerSensitiveContainer;
import iskallia.vault.container.slot.InfiniteSellSlot;
import iskallia.vault.container.slot.PlayerSensitiveSlot;
import iskallia.vault.container.slot.SellSlot;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModContainers;
import iskallia.vault.item.ItemShardPouch;
import iskallia.vault.world.data.SoulShardTraderData;
import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.LogicalSide;

public class ShardTradeContainer extends AbstractPlayerSensitiveContainer {
   public ShardTradeContainer(int windowId, PlayerInventory inventory) {
      this(windowId, inventory, new Inventory(4));
   }

   public ShardTradeContainer(int windowId, PlayerInventory inventory, IInventory tradeView) {
      super(ModContainers.SHARD_TRADE_CONTAINER, windowId);
      this.initSlots(inventory, tradeView);
   }

   private void initSlots(PlayerInventory playerInventory, IInventory tradeView) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.func_75146_a(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 102 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.func_75146_a(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 160));
      }

      this.func_75146_a(new ShardTradeContainer.RandomSellSlot(tradeView, 0, 34, 36));
      this.func_75146_a(new ShardTradeContainer.ShardSellSlot(tradeView, 1, 146, 10));
      this.func_75146_a(new ShardTradeContainer.ShardSellSlot(tradeView, 2, 146, 38));
      this.func_75146_a(new ShardTradeContainer.ShardSellSlot(tradeView, 3, 146, 66));
   }

   public ItemStack func_82846_b(PlayerEntity player, int index) {
      ItemStack itemstack = ItemStack.field_190927_a;
      Slot slot = (Slot)this.field_75151_b.get(index);
      if (slot != null && slot.func_75216_d()) {
         ItemStack slotStack = slot.func_75211_c();
         if (slot instanceof PlayerSensitiveSlot) {
            slotStack = ((PlayerSensitiveSlot)slot).modifyTakenStack(player, slotStack, true);
         }

         itemstack = slotStack.func_77946_l();
         if (index >= 0 && index < 36 && !this.mergeItemStack(slot, player, slotStack, 36, 40)) {
            return ItemStack.field_190927_a;
         }

         if (index >= 0 && index < 27) {
            if (!this.mergeItemStack(slot, player, slotStack, 27, 36)) {
               return ItemStack.field_190927_a;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.mergeItemStack(slot, player, slotStack, 0, 27)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.mergeItemStack(slot, player, slotStack, 0, 36)) {
            return ItemStack.field_190927_a;
         }

         if (slotStack.func_190916_E() > 0) {
            slot.func_75218_e();
         }

         if (slotStack.func_190916_E() == itemstack.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         if (slot instanceof PlayerSensitiveSlot) {
            ((PlayerSensitiveSlot)slot).modifyTakenStack(player, slotStack, false);
         }

         slot.func_190901_a(player, slotStack);
      }

      return itemstack;
   }

   protected boolean mergeItemStack(Slot fromSlot, PlayerEntity player, ItemStack stack, int startIndex, int endIndex) {
      boolean didMerge = false;

      for (int i = startIndex; i < endIndex && !stack.func_190926_b(); i++) {
         Slot targetSlot = (Slot)this.field_75151_b.get(i);
         ItemStack slotStack = targetSlot.func_75211_c();
         if (targetSlot.func_75214_a(stack)
            && !slotStack.func_190926_b()
            && slotStack.func_77973_b() == stack.func_77973_b()
            && ItemStack.func_77970_a(stack, slotStack)) {
            int targetSize = slotStack.func_190916_E() + stack.func_190916_E();
            int targetMaxSize = targetSlot.func_178170_b(slotStack);
            if (targetSize <= targetMaxSize) {
               stack.func_190918_g(stack.func_190916_E());
               fromSlot.func_75209_a(stack.func_190916_E());
               slotStack.func_190920_e(targetSize);
               targetSlot.func_75218_e();
               didMerge = true;
            } else if (slotStack.func_190916_E() < targetMaxSize) {
               int takenAmount = targetMaxSize - slotStack.func_190916_E();
               stack.func_190918_g(takenAmount);
               fromSlot.func_75209_a(takenAmount);
               slotStack.func_190920_e(targetMaxSize);
               targetSlot.func_75218_e();
               didMerge = true;
            }
         }
      }

      if (stack.func_190926_b()) {
         return didMerge;
      } else {
         for (int ix = startIndex; ix < endIndex; ix++) {
            Slot targetSlot = (Slot)this.field_75151_b.get(ix);
            ItemStack slotStack = targetSlot.func_75211_c();
            if (slotStack.func_190926_b() && targetSlot.func_75214_a(stack)) {
               if (stack.func_190916_E() > targetSlot.func_178170_b(stack)) {
                  targetSlot.func_75215_d(stack.func_77979_a(targetSlot.func_178170_b(stack)));
               } else {
                  targetSlot.func_75215_d(stack.func_77979_a(stack.func_190916_E()));
               }

               targetSlot.func_75218_e();
               didMerge = true;
               break;
            }
         }

         return didMerge;
      }
   }

   public boolean func_75145_c(PlayerEntity player) {
      return true;
   }

   public static class RandomSellSlot extends InfiniteSellSlot implements PlayerSensitiveSlot {
      public RandomSellSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
         super(inventoryIn, index, xPosition, yPosition);
      }

      public boolean func_82869_a(PlayerEntity player) {
         int count = ItemShardPouch.getShardCount(player.field_71071_by);
         int shardCost;
         if (player.func_130014_f_().func_201670_d()) {
            shardCost = ClientShardTradeData.getRandomTradeCost();
         } else {
            shardCost = ModConfigs.SOUL_SHARD.getShardTradePrice();
         }

         return count >= shardCost;
      }

      @Override
      public ItemStack modifyTakenStack(PlayerEntity player, ItemStack taken, LogicalSide side, boolean simulate) {
         long tradeSeed;
         if (player instanceof ServerPlayerEntity) {
            SoulShardTraderData tradeData = SoulShardTraderData.get(((ServerPlayerEntity)player).func_71121_q());
            tradeSeed = tradeData.getSeed();
            if (!simulate) {
               tradeData.nextSeed();
            }
         } else {
            tradeSeed = ClientShardTradeData.getTradeSeed();
            if (!simulate) {
               ClientShardTradeData.nextSeed();
            }
         }

         Random rand = new Random(tradeSeed);
         SoulShardConfig.ShardTrade trade;
         if (player.func_130014_f_().func_201670_d()) {
            trade = ClientShardTradeData.getShardTrades().getRandom(rand);
         } else {
            trade = ModConfigs.SOUL_SHARD.getRandomTrade(rand);
         }

         if (trade != null) {
            int shardCost;
            if (player.func_130014_f_().func_201670_d()) {
               shardCost = ClientShardTradeData.getRandomTradeCost();
            } else {
               shardCost = ModConfigs.SOUL_SHARD.getShardTradePrice();
            }

            if (ItemShardPouch.reduceShardAmount(player.field_71071_by, shardCost, simulate)) {
               if (side.isServer() && !simulate && player.field_71070_bA != null) {
                  player.field_71070_bA.func_75142_b();
               }

               return trade.getItem().func_77946_l();
            }
         }

         return ItemStack.field_190927_a;
      }
   }

   public static class ShardSellSlot extends SellSlot implements PlayerSensitiveSlot {
      public ShardSellSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
         super(inventoryIn, index, xPosition, yPosition);
      }

      public boolean func_82869_a(PlayerEntity player) {
         int shardCost;
         if (player instanceof ServerPlayerEntity) {
            SoulShardTraderData tradeData = SoulShardTraderData.get(((ServerPlayerEntity)player).func_71121_q());
            SoulShardTraderData.SelectedTrade trade = tradeData.getTrades().get(this.getSlotIndex() - 1);
            if (trade == null) {
               return false;
            }

            shardCost = trade.getShardCost();
         } else {
            Tuple<ItemStack, Integer> trade = ClientShardTradeData.getTradeInfo(this.getSlotIndex() - 1);
            if (trade == null) {
               return false;
            }

            shardCost = (Integer)trade.func_76340_b();
         }

         int count = ItemShardPouch.getShardCount(player.field_71071_by);
         return count >= shardCost;
      }

      @Override
      public ItemStack modifyTakenStack(PlayerEntity player, ItemStack taken, LogicalSide side, boolean simulate) {
         int shardCost;
         if (player instanceof ServerPlayerEntity) {
            SoulShardTraderData tradeData = SoulShardTraderData.get(((ServerPlayerEntity)player).func_71121_q());
            SoulShardTraderData.SelectedTrade trade = tradeData.getTrades().get(this.getSlotIndex() - 1);
            if (trade == null) {
               return ItemStack.field_190927_a;
            }

            shardCost = trade.getShardCost();
         } else {
            Tuple<ItemStack, Integer> trade = ClientShardTradeData.getTradeInfo(this.getSlotIndex() - 1);
            if (trade == null) {
               return ItemStack.field_190927_a;
            }

            shardCost = (Integer)trade.func_76340_b();
         }

         if (ItemShardPouch.reduceShardAmount(player.field_71071_by, shardCost, simulate)) {
            if (side.isServer() && !simulate) {
               if (player instanceof ServerPlayerEntity) {
                  SoulShardTraderData tradeData = SoulShardTraderData.get(((ServerPlayerEntity)player).func_71121_q());
                  tradeData.useTrade(this.getSlotIndex() - 1);
                  SoulShardTraderData.SelectedTrade trade = tradeData.getTrades().get(this.getSlotIndex() - 1);
                  if (trade != null && trade.isInfinite()) {
                     player.field_71070_bA.field_75153_a.set(this.field_75222_d, ItemStack.field_190927_a);
                     this.func_75211_c().func_190917_f(1);
                  }
               }

               if (player.field_71070_bA != null) {
                  player.field_71070_bA.func_75142_b();
               }
            }

            return taken;
         } else {
            return ItemStack.field_190927_a;
         }
      }
   }
}
