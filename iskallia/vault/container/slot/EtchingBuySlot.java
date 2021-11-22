package iskallia.vault.container.slot;

import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.container.inventory.EtchingTradeContainer;
import iskallia.vault.entity.EtchingVendorEntity;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class EtchingBuySlot extends SlotItemHandler {
   private final EtchingTradeContainer etchingTradeContainer;
   private final int tradeId;

   public EtchingBuySlot(EtchingTradeContainer etchingTradeContainer, IItemHandler itemHandler, int tradeId, int index, int xPosition, int yPosition) {
      super(itemHandler, index, xPosition, yPosition);
      this.etchingTradeContainer = etchingTradeContainer;
      this.tradeId = tradeId;
   }

   public boolean func_75214_a(@Nonnull ItemStack stack) {
      return false;
   }

   public boolean func_82869_a(PlayerEntity player) {
      EtchingVendorControllerTileEntity.EtchingTrade trade = this.getAssociatedTrade();
      if (trade == null) {
         return false;
      } else {
         int count = this.getInputSlot().func_75211_c().func_190916_E();
         return trade.getRequiredPlatinum() <= count && !trade.isSold();
      }
   }

   public Slot getInputSlot() {
      return this.etchingTradeContainer.func_75139_a(36 + this.tradeId * 2);
   }

   @Nullable
   public EtchingVendorControllerTileEntity.EtchingTrade getAssociatedTrade() {
      EtchingVendorEntity vendor = this.etchingTradeContainer.getVendor();
      if (vendor == null) {
         return null;
      } else {
         EtchingVendorControllerTileEntity controllerTile = vendor.getControllerTile();
         return controllerTile == null ? null : controllerTile.getTrade(this.tradeId);
      }
   }

   public ItemStack func_190901_a(PlayerEntity player, ItemStack stack) {
      EtchingVendorEntity vendor = this.etchingTradeContainer.getVendor();
      if (vendor == null) {
         return ItemStack.field_190927_a;
      } else {
         EtchingVendorControllerTileEntity controllerTile = vendor.getControllerTile();
         if (controllerTile == null) {
            return ItemStack.field_190927_a;
         } else {
            EtchingVendorControllerTileEntity.EtchingTrade trade = this.getAssociatedTrade();
            if (trade == null) {
               return ItemStack.field_190927_a;
            } else {
               this.getInputSlot().func_75209_a(trade.getRequiredPlatinum());
               this.func_75215_d(ItemStack.field_190927_a);
               trade.setSold(true);
               controllerTile.sendUpdates();
               vendor.func_184185_a(
                  SoundEvents.field_219721_mv,
                  1.0F,
                  (vendor.field_70170_p.field_73012_v.nextFloat() - vendor.field_70170_p.field_73012_v.nextFloat()) * 0.2F + 1.0F
               );
               return stack;
            }
         }
      }
   }
}
