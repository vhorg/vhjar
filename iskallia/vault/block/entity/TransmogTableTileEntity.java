package iskallia.vault.block.entity;

import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.container.inventory.TransmogTableInventory;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransmogTableTileEntity extends BlockEntity implements MenuProvider {
   protected TransmogTableInventory internalInventory = new TransmogTableInventory(this) {
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

   public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inv, @NotNull Player player) {
      return this.getLevel() == null ? null : new TransmogTableContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
   }

   public boolean stillValid(@NotNull Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this ? this.internalInventory.stillValid(player) : false;
   }

   public void setChanged() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
         super.setChanged();
      }
   }

   public void load(@Nonnull CompoundTag compound) {
      super.load(compound);
      super.load(compound);
      if (compound.contains("Items")) {
         loadAllItems(compound, this.internalInventory);
      } else {
         this.internalInventory.load(compound);
      }
   }

   protected void saveAdditional(@Nonnull CompoundTag tag) {
      super.saveAdditional(tag);
      this.internalInventory.save(tag);
   }

   private static void loadAllItems(CompoundTag pTag, Container inventory) {
      ListTag listtag = pTag.getList("Items", 10);

      for (int i = 0; i < listtag.size(); i++) {
         CompoundTag compoundtag = listtag.getCompound(i);
         int j = compoundtag.getByte("Slot") & 255;
         if (j >= 0 && j < inventory.getContainerSize()) {
            inventory.setItem(j, ItemStack.of(compoundtag));
         }
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Nonnull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return super.getCapability(cap, side);
   }

   @NotNull
   public Component getDisplayName() {
      return new TranslatableComponent("container.the_vault.transmog_table");
   }
}
