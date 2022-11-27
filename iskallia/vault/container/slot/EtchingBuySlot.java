package iskallia.vault.container.slot;

import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.container.inventory.EtchingTradeContainer;
import iskallia.vault.entity.entity.EtchingVendorEntity;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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

   public boolean mayPlace(@Nonnull ItemStack stack) {
      return false;
   }

   public boolean mayPickup(Player player) {
      EtchingVendorControllerTileEntity.EtchingTrade trade = this.getAssociatedTrade();
      if (trade == null) {
         return false;
      } else {
         int count = this.getInputSlot().getItem().getCount();
         return trade.getRequiredPlatinum() <= count && !trade.isSold();
      }
   }

   public Slot getInputSlot() {
      return this.etchingTradeContainer.getSlot(36 + this.tradeId * 2);
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

   public void onTake(Player player, ItemStack stack) {
      EtchingVendorEntity vendor = this.etchingTradeContainer.getVendor();
      if (vendor != null) {
         EtchingVendorControllerTileEntity controllerTile = vendor.getControllerTile();
         if (controllerTile != null) {
            EtchingVendorControllerTileEntity.EtchingTrade trade = this.getAssociatedTrade();
            if (trade != null) {
               this.getInputSlot().remove(trade.getRequiredPlatinum());
               this.set(ItemStack.EMPTY);
               trade.setSold(true);
               controllerTile.sendUpdates();
               vendor.playSound(SoundEvents.VILLAGER_CELEBRATE, 1.0F, (vendor.level.random.nextFloat() - vendor.level.random.nextFloat()) * 0.2F + 1.0F);
            }
         }
      }
   }
}
