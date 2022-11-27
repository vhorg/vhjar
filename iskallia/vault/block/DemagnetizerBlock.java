package iskallia.vault.block;

import iskallia.vault.block.entity.DemagnetizerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.Nullable;

public class DemagnetizerBlock extends Block implements EntityBlock {
   public DemagnetizerBlock() {
      super(Properties.of(Material.STONE, MaterialColor.STONE).noOcclusion().strength(2.0F, 10.0F));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new DemagnetizerTileEntity(pPos, pState);
   }
}
