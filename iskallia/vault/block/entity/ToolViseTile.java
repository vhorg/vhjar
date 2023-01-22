package iskallia.vault.block.entity;

import iskallia.vault.container.ToolViseContainerMenu;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ToolViseTile extends BlockEntity implements MenuProvider {
   private final OverSizedInventory inventory = new OverSizedInventory(6, this::setChanged, player -> true);
   private final SimpleContainer pickaxeInput = new SimpleContainer(1) {
      public void setChanged() {
         super.setChanged();
         ToolViseTile.this.setChanged();
      }
   };

   public ToolViseTile(BlockPos pos, BlockState state) {
      super(ModBlocks.TOOL_VISE_TILE_ENTITY, pos, state);
   }

   public OverSizedInventory getInventory() {
      return this.inventory;
   }

   public SimpleContainer getPickaxeInput() {
      return this.pickaxeInput;
   }

   public Component getDisplayName() {
      return new TranslatableComponent("container.the_vault.tool_vise");
   }

   public void setChanged() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
         super.setChanged();
      }
   }

   private int getSize() {
      return this.pickaxeInput.getContainerSize() + this.inventory.getContainerSize();
   }

   public void load(@NotNull CompoundTag compound) {
      super.load(compound);
      NonNullList<ItemStack> stacks = NonNullList.withSize(this.getSize(), ItemStack.EMPTY);
      if (compound.contains("Items")) {
         ContainerHelper.loadAllItems(compound, stacks);
         this.pickaxeInput.setItem(0, (ItemStack)stacks.get(0));

         for (int index = 1; index < stacks.size(); index++) {
            this.inventory.setItem(index - 1, (ItemStack)stacks.get(index));
         }
      } else {
         this.inventory.load(compound);
         this.pickaxeInput.setItem(0, ItemStack.of(compound.getCompound("pickaxeInput")));
      }
   }

   public void saveAdditional(@NotNull CompoundTag compound) {
      super.saveAdditional(compound);
      this.inventory.save(compound);
      compound.put("pickaxeInput", this.pickaxeInput.getItem(0).copy().serializeNBT());
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, BlockEntity::saveWithoutMetadata);
   }

   @NotNull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new ToolViseContainerMenu(containerId, this.getLevel(), this.getBlockPos(), inv);
   }

   public ItemStack getPickaxe() {
      return this.pickaxeInput.getItem(0);
   }
}
