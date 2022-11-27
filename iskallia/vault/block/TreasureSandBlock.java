package iskallia.vault.block;

import iskallia.vault.block.base.LootableBlock;
import iskallia.vault.block.entity.TreasureSandTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TreasureSandBlock extends LootableBlock {
   public TreasureSandBlock() {
      super(Properties.copy(Blocks.SAND));
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new TreasureSandTileEntity(pos, state);
   }
}
