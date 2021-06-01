package iskallia.vault.container;

import iskallia.vault.block.GlobalTraderBlock;
import iskallia.vault.block.entity.GlobalTraderTileEntity;
import iskallia.vault.container.inventory.TraderInventory;
import iskallia.vault.container.slot.VendingSellSlot;
import iskallia.vault.init.ModContainers;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.nbt.NBTSerializer;
import iskallia.vault.vending.Trade;
import iskallia.vault.world.data.GlobalTraderData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class GlobalTraderContainer extends Container {
   protected GlobalTraderTileEntity tileEntity;
   protected TraderInventory traderInventory;
   protected PlayerInventory playerInventory;
   protected List<Trade> playerTrades = new ArrayList<>();

   public GlobalTraderContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, ListNBT playerTrades) {
      super(ModContainers.TRADER_CONTAINER, windowId);
      BlockState blockState = world.func_180495_p(pos);
      this.tileEntity = (GlobalTraderTileEntity)GlobalTraderBlock.getBlockTileEntity(world, pos, blockState);
      this.playerInventory = playerInventory;
      this.traderInventory = new TraderInventory();
      this.func_75146_a(new Slot(this.traderInventory, 0, 210, 43) {
         public void func_75218_e() {
            super.func_75218_e();
            GlobalTraderContainer.this.traderInventory.updateRecipe();
            if (GlobalTraderContainer.this.hasTraded()) {
               GlobalTraderContainer.this.lockAllTrades();
            }
         }

         public void func_75220_a(ItemStack oldStackIn, ItemStack newStackIn) {
            super.func_75220_a(oldStackIn, newStackIn);
            GlobalTraderContainer.this.traderInventory.updateRecipe();
            if (GlobalTraderContainer.this.hasTraded()) {
               GlobalTraderContainer.this.lockAllTrades();
            }
         }
      });
      this.func_75146_a(new VendingSellSlot(this.traderInventory, 2, 268, 43) {
         public void func_75218_e() {
            super.func_75218_e();
            if (GlobalTraderContainer.this.hasTraded()) {
               GlobalTraderContainer.this.lockAllTrades();
            }
         }
      });

      for (int i1 = 0; i1 < 3; i1++) {
         for (int k1 = 0; k1 < 9; k1++) {
            this.func_75146_a(new Slot(playerInventory, k1 + i1 * 9 + 9, 167 + k1 * 18, 86 + i1 * 18));
         }
      }

      for (int j1 = 0; j1 < 9; j1++) {
         this.func_75146_a(new Slot(playerInventory, j1, 167 + j1 * 18, 144));
      }

      if (playerTrades != null) {
         playerTrades.forEach(data -> {
            try {
               CompoundNBT tradeData = (CompoundNBT)data;
               this.playerTrades.add(NBTSerializer.deserialize(Trade.class, tradeData));
            } catch (Exception var3x) {
               var3x.printStackTrace();
            }
         });
      }
   }

   public GlobalTraderTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public Trade getSelectedTrade() {
      return this.traderInventory.getSelectedTrade();
   }

   public void selectTrade(int index) {
      if (index >= 0 && index < this.playerTrades.size()) {
         Trade trade = this.playerTrades.get(index);
         this.traderInventory.updateTrade(trade);
         this.traderInventory.updateRecipe();
         if (this.traderInventory.func_70301_a(0) != ItemStack.field_190927_a) {
            ItemStack buyStack = this.traderInventory.func_70304_b(0);
            this.playerInventory.func_70441_a(buyStack);
         }

         if (trade.getTradesLeft() > 0) {
            int slot = this.slotForItem(trade.getBuy().getItem());
            if (slot != -1) {
               ItemStack buyStack = this.playerInventory.func_70304_b(slot);
               this.traderInventory.func_70299_a(0, buyStack);
            }

            World world = this.tileEntity.func_145831_w();
            if (world != null && !world.field_72995_K) {
               GlobalTraderData.get((ServerWorld)world).updatePlayerTrades(this.playerInventory.field_70458_d, this.playerTrades);
            }
         }
      }
   }

   private boolean hasTraded() {
      for (Trade t : this.playerTrades) {
         if (t.getTimesTraded() >= t.getMaxTrades()) {
            return true;
         }
      }

      return false;
   }

   private void lockAllTrades() {
      for (Trade t : this.playerTrades) {
         t.setTimesTraded(t.getMaxTrades());
      }
   }

   private int slotForItem(Item item) {
      for (int i = 0; i < this.playerInventory.func_70302_i_(); i++) {
         if (this.playerInventory.func_70301_a(i).func_77973_b() == item) {
            return i;
         }
      }

      return -1;
   }

   public boolean func_75145_c(PlayerEntity player) {
      return true;
   }

   public ItemStack func_82846_b(PlayerEntity playerIn, int index) {
      return ItemStack.field_190927_a;
   }

   public void func_75134_a(PlayerEntity player) {
      super.func_75134_a(player);
      ItemStack buy = this.traderInventory.func_70301_a(0);
      if (!buy.func_190926_b()) {
         EntityHelper.giveItem(player, buy);
      }

      if (!player.field_70170_p.field_72995_K) {
         if (this.hasTraded()) {
            this.lockAllTrades();
         }

         GlobalTraderData.get((ServerWorld)player.field_70170_p).updatePlayerTrades(player, this.getPlayerTrades());
      }
   }

   public List<Trade> getPlayerTrades() {
      return this.playerTrades;
   }
}
