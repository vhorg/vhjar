package iskallia.vault.container.inventory;

import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.container.slot.EtchingBuySlot;
import iskallia.vault.container.slot.FilteredSlot;
import iskallia.vault.entity.EtchingVendorEntity;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModItems;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.InvWrapper;

public class EtchingTradeContainer extends Container {
   private final IInventory tradeInventory = new Inventory(6);
   private final World world;
   private final int vendorEntityId;

   public EtchingTradeContainer(int containerId, PlayerInventory playerInventory, int vendorEntityId) {
      super(ModContainers.ETCHING_TRADE_CONTAINER, containerId);
      this.world = playerInventory.field_70458_d.field_70170_p;
      this.vendorEntityId = vendorEntityId;
      this.initPlayerSlots(playerInventory);
      this.initTradeSlots();
   }

   private void initPlayerSlots(PlayerInventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.func_75146_a(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 102 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.func_75146_a(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 160));
      }
   }

   private void initTradeSlots() {
      for (int i = 0; i < 3; i++) {
         this.func_75146_a(
            new FilteredSlot(new InvWrapper(this.tradeInventory), i * 2, 53, 10 + i * 28, stack -> stack.func_77973_b() == ModItems.VAULT_PLATINUM)
         );
         this.func_75146_a(new EtchingBuySlot(this, new InvWrapper(this.tradeInventory), i, i * 2 + 1, 107, 10 + i * 28));
      }

      EtchingVendorEntity vendor = this.getVendor();
      if (vendor != null) {
         EtchingVendorControllerTileEntity controllerTile = vendor.getControllerTile();
         if (controllerTile != null) {
            for (int i = 0; i < 3; i++) {
               EtchingVendorControllerTileEntity.EtchingTrade trade = controllerTile.getTrade(i);
               if (trade != null && !trade.isSold()) {
                  Slot outSlot = this.func_75139_a(37 + i * 2);
                  outSlot.func_75215_d(trade.getSoldEtching().func_77946_l());
               }
            }
         }
      }
   }

   @Nullable
   public EtchingVendorEntity getVendor() {
      return (EtchingVendorEntity)this.world.func_73045_a(this.vendorEntityId);
   }

   public void func_75134_a(PlayerEntity player) {
      super.func_75134_a(player);
      this.tradeInventory.func_70299_a(1, ItemStack.field_190927_a);
      this.tradeInventory.func_70299_a(3, ItemStack.field_190927_a);
      this.tradeInventory.func_70299_a(5, ItemStack.field_190927_a);
      this.func_193327_a(player, player.field_70170_p, this.tradeInventory);
   }

   public ItemStack func_82846_b(PlayerEntity player, int index) {
      ItemStack itemstack = ItemStack.field_190927_a;
      Slot slot = (Slot)this.field_75151_b.get(index);
      if (slot != null && slot.func_75216_d()) {
         ItemStack slotStack = slot.func_75211_c();
         itemstack = slotStack.func_77946_l();
         if (index >= 0 && index < 36 && this.func_75135_a(slotStack, 36, 42, false)) {
            return itemstack;
         }

         if (index >= 0 && index < 27) {
            if (!this.func_75135_a(slotStack, 27, 36, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.func_75135_a(slotStack, 0, 27, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.func_75135_a(slotStack, 0, 36, false)) {
            return ItemStack.field_190927_a;
         }

         if (slotStack.func_190916_E() == 0) {
            slot.func_75215_d(ItemStack.field_190927_a);
         } else {
            slot.func_75218_e();
         }

         if (slotStack.func_190916_E() == itemstack.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         slot.func_190901_a(player, slotStack);
      }

      return itemstack;
   }

   public boolean func_75145_c(PlayerEntity player) {
      EtchingVendorEntity vendor = this.getVendor();
      return vendor != null && vendor.isValid();
   }
}
