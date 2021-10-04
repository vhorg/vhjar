package iskallia.vault.container;

import iskallia.vault.block.VendingMachineBlock;
import iskallia.vault.block.entity.VendingMachineTileEntity;
import iskallia.vault.container.inventory.VendingInventory;
import iskallia.vault.container.slot.SellSlot;
import iskallia.vault.init.ModContainers;
import iskallia.vault.item.ItemTraderCore;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.vending.TraderCore;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VendingMachineContainer extends Container {
   protected VendingMachineTileEntity tileEntity;
   protected VendingInventory vendingInventory;
   protected PlayerInventory playerInventory;

   public VendingMachineContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
      super(ModContainers.VENDING_MACHINE_CONTAINER, windowId);
      BlockState blockState = world.func_180495_p(pos);
      this.tileEntity = (VendingMachineTileEntity)VendingMachineBlock.getBlockTileEntity(world, pos, blockState);
      this.playerInventory = playerInventory;
      this.vendingInventory = new VendingInventory();
      this.func_75146_a(new Slot(this.vendingInventory, 0, 210, 43) {
         public void func_75218_e() {
            super.func_75218_e();
            VendingMachineContainer.this.vendingInventory.updateRecipe();
         }

         public void func_75220_a(ItemStack oldStackIn, ItemStack newStackIn) {
            super.func_75220_a(oldStackIn, newStackIn);
            VendingMachineContainer.this.vendingInventory.updateRecipe();
         }
      });
      this.func_75146_a(new SellSlot(this.vendingInventory, 2, 268, 43));

      for (int i1 = 0; i1 < 3; i1++) {
         for (int k1 = 0; k1 < 9; k1++) {
            this.func_75146_a(new Slot(playerInventory, k1 + i1 * 9 + 9, 167 + k1 * 18, 86 + i1 * 18));
         }
      }

      for (int j1 = 0; j1 < 9; j1++) {
         this.func_75146_a(new Slot(playerInventory, j1, 167 + j1 * 18, 144));
      }
   }

   public VendingMachineTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public TraderCore getSelectedTrade() {
      return this.vendingInventory.getSelectedCore();
   }

   public void selectTrade(int index) {
      List<TraderCore> cores = this.tileEntity.getCores();
      if (index >= 0 && index < cores.size()) {
         TraderCore traderCore = cores.get(index);
         this.vendingInventory.updateSelectedCore(this.tileEntity, traderCore);
         this.vendingInventory.updateRecipe();
         if (this.vendingInventory.func_70301_a(0) != ItemStack.field_190927_a) {
            ItemStack buyStack = this.vendingInventory.func_70304_b(0);
            this.playerInventory.func_70441_a(buyStack);
         }

         if (traderCore.getTrade().getTradesLeft() > 0) {
            int slot = this.slotForItem(traderCore.getTrade().getBuy().getItem());
            if (slot != -1) {
               ItemStack buyStack = this.playerInventory.func_70304_b(slot);
               this.vendingInventory.func_70299_a(0, buyStack);
            }
         }
      }
   }

   public void deselectTrades() {
      if (this.vendingInventory.func_70301_a(0) != ItemStack.field_190927_a) {
         ItemStack buyStack = this.vendingInventory.func_70304_b(0);
         this.playerInventory.func_70441_a(buyStack);
      }

      this.vendingInventory.updateSelectedCore(this.tileEntity, null);
   }

   public void ejectCore(int index) {
      List<TraderCore> cores = this.tileEntity.getCores();
      if (index >= 0 && index < cores.size()) {
         this.deselectTrades();
         TraderCore ejectedCore = this.tileEntity.getCores().remove(index);
         ItemStack itemStack = ItemTraderCore.getStackFromCore(ejectedCore);
         this.playerInventory.field_70458_d.func_146097_a(itemStack, false, true);
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
      ItemStack buy = this.vendingInventory.func_70301_a(0);
      if (!buy.func_190926_b()) {
         EntityHelper.giveItem(player, buy);
      }
   }
}
