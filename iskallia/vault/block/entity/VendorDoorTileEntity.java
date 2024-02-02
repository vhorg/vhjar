package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class VendorDoorTileEntity extends TreasureDoorTileEntity {
   public VendorDoorTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VENDOR_DOOR_TILE_ENTITY, pos, state);
   }

   @Override
   public void load(CompoundTag nbt) {
      super.load(nbt);
   }

   @Override
   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, VendorDoorTileEntity tile) {
      TreasureDoorTileEntity.tick(level, pos, state, tile);
   }
}
