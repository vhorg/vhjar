package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AncientCopperConduitTileEntity extends BlockEntity {
   public int tickCount;
   private float activeRotation;

   public AncientCopperConduitTileEntity(BlockPos pPos, BlockState pState) {
      super(ModBlocks.ANCIENT_COPPER_CONDUIT_BLOCK_TILE_ENTITY, pPos, pState);
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
   }

   public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AncientCopperConduitTileEntity pBlockEntity) {
      pBlockEntity.tickCount++;
      pBlockEntity.activeRotation++;
   }

   public float getActiveRotation(float p_59198_) {
      return (this.activeRotation + p_59198_) * -0.0375F;
   }
}
