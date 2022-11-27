package iskallia.vault.block;

import iskallia.vault.block.entity.EliteSpawnerTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EliteSpawnerBlock extends BaseSpawnerBlock {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new EliteSpawnerTileEntity(pos, state);
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      if (!level.isClientSide() && level.getBlockEntity(pos) instanceof EliteSpawnerTileEntity spawnerBe) {
         spawnerBe.setRandomSpawn();
      }
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return BlockHelper.getTicker(blockEntityType, ModBlocks.ELITE_SPAWNER_TILE_ENTITY, (l, p, s, te) -> EliteSpawnerTileEntity.tick(l, p, te));
   }
}
