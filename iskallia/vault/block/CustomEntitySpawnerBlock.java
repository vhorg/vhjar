package iskallia.vault.block;

import iskallia.vault.block.entity.CustomEntitySpawnerTileEntity;
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

public class CustomEntitySpawnerBlock extends BaseSpawnerBlock {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new CustomEntitySpawnerTileEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return BlockHelper.getTicker(blockEntityType, ModBlocks.CUSTOM_ENTITY_SPAWNER_TILE_ENTITY, (l, p, s, te) -> CustomEntitySpawnerTileEntity.tick(l, p, te));
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CustomEntitySpawnerTileEntity spawnerBe) {
         spawnerBe.setRandomSpawn();
      }
   }
}
