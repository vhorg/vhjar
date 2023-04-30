package iskallia.vault.block.entity;

import iskallia.vault.container.AlchemyTableContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.nbt.NBTHelper;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AlchemyTableTileEntity extends BlockEntity implements MenuProvider {
   private final SimpleContainer inventory = new SimpleContainer(1) {
      public void setChanged() {
         super.setChanged();
         AlchemyTableTileEntity.this.setChanged();
      }
   };

   public AlchemyTableTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.ALCHEMY_TABLE_TILE_ENTITY, pos, state);
   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this;
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      NBTHelper.deserializeSimpleContainer(this.inventory, tag.getList("inventory", 10));
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.put("inventory", NBTHelper.serializeSimpleContainer(this.inventory));
   }

   @Nullable
   public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
      return this.getLevel() == null ? null : new AlchemyTableContainer(id, this.getLevel(), this.getBlockPos(), inventory);
   }
}
