package iskallia.vault.block;

import iskallia.vault.block.entity.VaultDoorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class UnknownVaultDoorBlock extends DoorBlock {
   public UnknownVaultDoorBlock() {
      super(
         Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151648_G)
            .func_200948_a(-1.0F, 3600000.0F)
            .func_200947_a(SoundType.field_185852_e)
            .func_226896_b_()
      );
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.func_176194_O().func_177621_b())
                        .func_206870_a(field_176520_a, Direction.NORTH))
                     .func_206870_a(field_176519_b, Boolean.FALSE))
                  .func_206870_a(field_176521_M, DoorHingeSide.LEFT))
               .func_206870_a(field_176522_N, Boolean.FALSE))
            .func_206870_a(field_176523_O, DoubleBlockHalf.LOWER)
      );
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return new VaultDoorTileEntity();
   }

   public void func_220082_b(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
      super.func_220082_b(state, world, pos, oldState, isMoving);
   }
}
