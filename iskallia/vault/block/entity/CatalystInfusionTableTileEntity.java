package iskallia.vault.block.entity;

import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.inventory.CatalystInfusionTableContainer;
import iskallia.vault.init.ModBlocks;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class CatalystInfusionTableTileEntity extends ForgeRecipeTileEntity implements MenuProvider {
   public static final Set<String> LEGACY_INVENTORY_KEYS = new HashSet<>(Arrays.asList("catalyst", "infuser", "output"));

   public CatalystInfusionTableTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.CATALYST_INFUSION_TABLE_TILE_ENTITY, pWorldPosition, pBlockState, 6, ForgeRecipeType.CATALYST);
   }

   @Override
   protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory) {
      return new CatalystInfusionTableContainer(windowId, this.getLevel(), this.getBlockPos(), playerInventory);
   }

   @Override
   public void load(@Nonnull CompoundTag nbt) {
      super.load(nbt);

      for (String key : LEGACY_INVENTORY_KEYS) {
         ItemStackHandler handler = new ItemStackHandler();
         handler.deserializeNBT(nbt.getCompound(key));

         for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack extra = handler.getStackInSlot(slot);
            int left = extra.getCount();

            for (int i = 0; i < this.getInventory().getContainerSize(); i++) {
               ItemStack stack = this.getInventory().getItem(i);
               if (left <= 0) {
                  break;
               }

               if (stack.isEmpty() || ItemStack.isSameItemSameTags(stack, extra)) {
                  int difference = Math.min(extra.getMaxStackSize() - stack.getCount(), left);
                  ItemStack copy = extra.copy();
                  copy.setCount(stack.getCount() + difference);
                  this.getInventory().setItem(i, copy);
                  left -= difference;
               }
            }
         }
      }
   }
}
