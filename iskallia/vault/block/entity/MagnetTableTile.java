package iskallia.vault.block.entity;

import iskallia.vault.container.inventory.MagnetTableContainerMenu;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.LegacyMagnetItem;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class MagnetTableTile extends RandomizableContainerBlockEntity implements WorldlyContainer {
   private NonNullList<ItemStack> stacks;
   private final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());

   public MagnetTableTile(BlockPos pos, BlockState state) {
      super(ModBlocks.MAGNET_TABLE_TILE_ENTITY, pos, state);
      this.stacks = NonNullList.withSize(5, ItemStack.EMPTY);
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.vault.magnet_table");
   }

   public void setChanged() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
         super.setChanged();
      }
   }

   public void load(CompoundTag compound) {
      super.load(compound);
      if (!this.tryLoadLootTable(compound)) {
         this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      }

      ContainerHelper.loadAllItems(compound, this.stacks);
   }

   public void saveAdditional(CompoundTag compound) {
      super.saveAdditional(compound);
      if (!this.trySaveLootTable(compound)) {
         ContainerHelper.saveAllItems(compound, this.stacks);
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public int getContainerSize() {
      return this.stacks.size();
   }

   public AbstractContainerMenu createMenu(int id, Inventory player) {
      return new MagnetTableContainerMenu(id, player, this);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   public ItemStack getMagnet() {
      return (ItemStack)this.stacks.get(0);
   }

   public void setItems(NonNullList<ItemStack> stacks) {
      this.stacks = stacks;
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return true;
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
      if (direction.getAxis() == Axis.Y) {
         return stack.getItem() instanceof LegacyMagnetItem;
      } else {
         Item i = stack.getItem();
         return ModConfigs.MAGNET_CONFIG.getMaterialItem(index - 1) == i;
      }
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return true;
   }

   public int[] getSlotsForFace(Direction side) {
      return side.getAxis() == Axis.Y ? new int[]{0} : new int[]{1, 2, 3, 4};
   }

   public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
      return !this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
         ? this.handlers[facing.ordinal()].cast()
         : super.getCapability(capability, facing);
   }

   public void setRemoved() {
      super.setRemoved();

      for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
         handler.invalidate();
      }
   }
}
