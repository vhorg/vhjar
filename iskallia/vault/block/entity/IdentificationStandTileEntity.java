package iskallia.vault.block.entity;

import iskallia.vault.block.entity.base.BookAnimatingTileEntity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.item.IdentifiableItem;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class IdentificationStandTileEntity extends BookAnimatingTileEntity {
   public IdentificationStandTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.IDENTIFICATION_STAND_TILE_ENTITY, pos, state);
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this;
   }

   @Override
   protected boolean canOpenBookModel(Player nearestPlayer, Level level, BlockPos blockPos, BlockState blockState, BookAnimatingTileEntity tileEntity) {
      return nearestPlayer.getInventory()
         .items
         .stream()
         .anyMatch(
            itemStack -> itemStack.getItem() instanceof IdentifiableItem identifiableItem
               && identifiableItem.getState(itemStack) == VaultGearState.UNIDENTIFIED
         );
   }
}
