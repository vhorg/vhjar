package iskallia.vault.block.entity;

import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VaultArtisanStationTileEntity extends BlockEntity implements MenuProvider {
   private final OverSizedInventory inventory = new OverSizedInventory(12, this);
   private final SimpleContainer gearInput = new SimpleContainer(1) {
      public void setChanged() {
         super.setChanged();
         VaultArtisanStationTileEntity.this.setChanged();
      }
   };

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this ? this.inventory.stillValid(player) : false;
   }

   public VaultArtisanStationTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_ARTISAN_STATION_ENTITY, pos, state);
   }

   public OverSizedInventory getInventory() {
      return this.inventory;
   }

   public SimpleContainer getGearInput() {
      return this.gearInput;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.inventory.load(tag);
      this.gearInput.setItem(0, ItemStack.of(tag.getCompound("gearInput")));
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      this.inventory.save(tag);
      tag.put("gearInput", this.gearInput.getItem(0).copy().serializeNBT());
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultArtisanStationContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
   }
}
