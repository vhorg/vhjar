package iskallia.vault.block.entity;

import iskallia.vault.container.VaultJewelCuttingStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VaultJewelCuttingStationTileEntity extends BlockEntity implements MenuProvider {
   private final OverSizedInventory inventory = new OverSizedInventory(12, this) {
      public boolean canPlaceItem(int pIndex, ItemStack pStack) {
         return pIndex != 0 && pIndex != 1 ? false : super.canPlaceItem(pIndex, pStack);
      }
   };

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this ? this.inventory.stillValid(player) : false;
   }

   public VaultJewelCuttingStationTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_JEWEL_CUTTING_STATION_ENTITY, pos, state);
   }

   public OverSizedInventory getInventory() {
      return this.inventory;
   }

   public ItemStack getJewelInput() {
      return this.inventory.getItem(5);
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.inventory.load(tag);
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      this.inventory.save(tag);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultJewelCuttingStationContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
   }
}
