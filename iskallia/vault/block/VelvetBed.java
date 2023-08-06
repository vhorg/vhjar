package iskallia.vault.block;

import iskallia.vault.block.entity.VelvetBedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class VelvetBed extends BedBlock {
   public VelvetBed() {
      super(DyeColor.RED, Properties.copy(Blocks.RED_BED));
   }

   public void fallOn(Level p_153362_, BlockState p_153363_, BlockPos p_153364_, Entity p_153365_, float p_153366_) {
      p_153365_.causeFallDamage(p_153366_, 0.0F, DamageSource.FALL);
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new VelvetBedTileEntity(pPos, pState, this.getColor());
   }
}
