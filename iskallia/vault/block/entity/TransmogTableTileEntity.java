package iskallia.vault.block.entity;

import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.container.inventory.TransmogTableInventory;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class TransmogTableTileEntity extends BaseContainerBlockEntity implements WorldlyContainer {
   protected TransmogTableInventory internalInventory = new TransmogTableInventory() {
      @Override
      public void setChanged() {
         super.setChanged();
         TransmogTableTileEntity.this.setChanged();
      }
   };

   public TransmogTableTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.TRANSMOG_TABLE_TILE_ENTITY, pos, state);
   }

   public TransmogTableInventory getInternalInventory() {
      return this.internalInventory;
   }

   @Nonnull
   protected Component getDefaultName() {
      return new TranslatableComponent("container.the_vault.transmog_table");
   }

   @Nonnull
   protected AbstractContainerMenu createMenu(int containerId, Inventory pInventory) {
      return new TransmogTableContainer(containerId, pInventory.player, this.internalInventory);
   }

   public int getContainerSize() {
      return this.internalInventory.getContainerSize();
   }

   public boolean isEmpty() {
      return this.internalInventory.isEmpty();
   }

   @Nonnull
   public ItemStack getItem(int index) {
      return this.internalInventory.getItem(index);
   }

   @Nonnull
   public ItemStack removeItem(int index, int count) {
      return this.internalInventory.removeItem(index, count);
   }

   @Nonnull
   public ItemStack removeItemNoUpdate(int index) {
      return this.internalInventory.removeItemNoUpdate(index);
   }

   public void setItem(int index, @Nonnull ItemStack itemStack) {
      this.internalInventory.setItem(index, itemStack);
   }

   public boolean stillValid(@Nonnull Player player) {
      return this.internalInventory.stillValid(player);
   }

   public void clearContent() {
      this.internalInventory.clearContent();
   }

   public void setChanged() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
         super.setChanged();
      }
   }

   public void load(@Nonnull CompoundTag tag) {
      super.load(tag);
      ContainerHelper.loadAllItems(tag, this.internalInventory.getSlots());
   }

   protected void saveAdditional(@Nonnull CompoundTag tag) {
      super.saveAdditional(tag);
      ContainerHelper.saveAllItems(tag, this.internalInventory.getSlots());
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Nonnull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nonnull
   public int[] getSlotsForFace(Direction side) {
      return side.getAxis() == Axis.Y ? new int[]{0} : new int[]{1, 2, 3, 4};
   }

   public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
      return false;
   }

   public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
      return true;
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return super.getCapability(cap, side);
   }
}
