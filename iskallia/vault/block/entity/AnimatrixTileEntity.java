package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class AnimatrixTileEntity extends BlockEntity {
   private Entity entityToRender;
   private boolean displayOnly;
   private final ItemStackHandler itemHandler = this.createHandler();
   private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);

   public AnimatrixTileEntity(BlockPos pPos, BlockState pState) {
      super(ModBlocks.ANIMATRIX_TILE_ENTITY, pPos, pState);
   }

   public boolean isDisplayOnly() {
      return this.displayOnly;
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(1) {
         protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (AnimatrixTileEntity.this.level != null && !AnimatrixTileEntity.this.level.isClientSide) {
               AnimatrixTileEntity.this.setEntityToRender(AnimatrixTileEntity.this.itemHandler.getStackInSlot(0));
               AnimatrixTileEntity.this.setChanged();
            }
         }

         @NotNull
         public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return super.insertItem(slot, stack, simulate);
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return AnimatrixTileEntity.this.itemHandler.getStackInSlot(0).isEmpty()
               ? true
               : stack.sameItem(AnimatrixTileEntity.this.itemHandler.getStackInSlot(0))
                  && AnimatrixTileEntity.this.itemHandler.getStackInSlot(0).getCount() + stack.getCount() <= 1;
         }
      };
   }

   public void load(CompoundTag pTag) {
      this.itemHandler.deserializeNBT(pTag.getCompound("inventory"));
      this.displayOnly = pTag.getBoolean("displayOnly");
      this.setEntityToRender(this.itemHandler.getStackInSlot(0));
   }

   protected void saveAdditional(CompoundTag pTag) {
      pTag.put("inventory", this.itemHandler.serializeNBT());
      pTag.putBoolean("displayOnly", this.displayOnly);
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public ItemStackHandler getItemHandler() {
      return this.itemHandler;
   }

   public Entity getEntityToRender() {
      return this.entityToRender;
   }

   public void setEntityToRender(ItemStack item) {
      if (this.level != null) {
         if (item.getItem() instanceof SpawnEggItem) {
            this.entityToRender = ((SpawnEggItem)item.getItem()).getType(null).create(this.level);
         } else if (item.isEmpty()) {
            this.entityToRender = null;
         }

         if (!this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
         }
      }
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.handler.cast() : super.getCapability(cap, side);
   }
}
