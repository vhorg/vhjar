package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.task.Task;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class TaskBuilderBlock extends Block {
   public TaskBuilderBlock() {
      super(Properties.copy(Blocks.STONE));
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      if (!level.isClientSide) {
         Direction dir = Direction.NORTH;
         if (placer != null) {
            dir = placer.getDirection();
         }

         int width = ModConfigs.TEAM_TASKS.width;
         int startOffset = width * 3 / 2;
         BlockPos startPos = pos.below().relative(dir).relative(dir.getCounterClockWise(), startOffset);
         int i = 0;

         for (Task task : ModConfigs.TEAM_TASKS.streamNonReclaimTasks().toList()) {
            BlockPos firstCorner = startPos.relative(dir.getClockWise(), i % width * 3).relative(dir, i / width * 3);
            BlockPos secondCorner = firstCorner.relative(dir, 2).relative(dir.getClockWise(), 2);
            BlockPos.betweenClosed(firstCorner, secondCorner).forEach(blockPos -> placeBlock(level, blockPos, Blocks.STONE.defaultBlockState()));
            BlockPos pillarPos = firstCorner.relative(dir, 1).relative(dir.getClockWise(), 1).above();
            if (placeBlock(level, pillarPos, ModBlocks.TASK_PILLAR.defaultBlockState())) {
               level.getBlockEntity(pillarPos, ModBlocks.TASK_PILLAR_TILE_ENTITY).ifPresent(tileEntity -> tileEntity.setTaskId(task.getId()));
            }

            i++;
         }
      }
   }

   private static boolean placeBlock(Level level, BlockPos blockPos, BlockState stateToPlace) {
      if (level.getBlockState(blockPos).getMaterial().isReplaceable()) {
         level.setBlock(blockPos, stateToPlace, 11);
         return true;
      } else {
         return false;
      }
   }
}
