package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.FarmerCactusConfig;
import iskallia.vault.skill.ability.effect.FarmerAbility;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.common.ForgeHooks;

public class FarmerCactusAbility extends FarmerAbility<FarmerCactusConfig> {
   @Override
   protected boolean canGrowBlock(ServerLevel world, BlockPos pos, Block block, BlockState blockState) {
      if (!(block instanceof SugarCaneBlock) && !(block instanceof CactusBlock)) {
         if (block instanceof BambooBlock bambooBlock) {
            return world.getBlockState(pos.below()).is(block) ? false : bambooBlock.isValidBonemealTarget(world, pos, blockState, false);
         } else if (block instanceof BambooSaplingBlock bambooSaplingBlock) {
            return !world.isEmptyBlock(pos.above()) ? false : bambooSaplingBlock.isValidBonemealTarget(world, pos, blockState, false);
         } else {
            return block instanceof NetherWartBlock ? (Integer)blockState.getValue(NetherWartBlock.AGE) < 3 : super.canGrowBlock(world, pos, block, blockState);
         }
      } else if (!world.isEmptyBlock(pos.above())) {
         return false;
      } else {
         int height = 1;

         while (world.getBlockState(pos.below(height)).is(block)) {
            height++;
         }

         return height < 3;
      }
   }

   protected void doGrowBlock(FarmerCactusConfig config, ServerPlayer player, ServerLevel world, BlockPos pos, Block block, BlockState state) {
      super.doGrowBlock(config, player, world, pos, block, state);
      if (!(block instanceof SugarCaneBlock) && !(block instanceof CactusBlock)) {
         if (block instanceof BambooBlock || block instanceof BambooSaplingBlock) {
            BlockState blockState = world.getBlockState(pos);
            if (blockState.getBlock() instanceof BonemealableBlock bonemealableBlock) {
               bonemealableBlock.performBonemeal(world, world.random, pos, blockState);
            }

            BlockPos bambooBlockPos = pos.above();

            List<BlockPos> particlePosList;
            for (particlePosList = new ArrayList<>(); world.getBlockState(bambooBlockPos).is(block); bambooBlockPos = bambooBlockPos.above()) {
               particlePosList.add(bambooBlockPos);
            }

            for (BlockPos blockPos : particlePosList) {
               world.sendParticles(
                  ParticleTypes.HAPPY_VILLAGER,
                  blockPos.getX() + 0.5,
                  blockPos.getY() + 0.5,
                  blockPos.getZ() + 0.5,
                  Math.max(1, 20 / particlePosList.size()),
                  0.5,
                  0.5,
                  0.5,
                  0.0
               );
            }
         } else if (block instanceof NetherWartBlock) {
            BlockState blockStatex = world.getBlockState(pos);
            if (ForgeHooks.onCropsGrowPre(world, pos, blockStatex, true)) {
               blockStatex = (BlockState)blockStatex.setValue(NetherWartBlock.AGE, (Integer)blockStatex.getValue(NetherWartBlock.AGE) + 1);
               world.setBlock(pos, blockStatex, 2);
               ForgeHooks.onCropsGrowPost(world, pos, blockStatex);
            }

            world.sendParticles(ParticleTypes.SOUL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.0);
         }
      } else {
         BlockPos above = pos.above();
         if (ForgeHooks.onCropsGrowPre(world, pos, state, true)) {
            world.setBlockAndUpdate(above, block.defaultBlockState());
            BlockState newState = (BlockState)state.setValue(BlockStateProperties.AGE_15, 0);
            world.setBlock(pos, newState, 4);
            newState.neighborChanged(world, above, block, pos, false);
            world.scheduleTick(above, block, 1, TickPriority.EXTREMELY_HIGH);
            ForgeHooks.onCropsGrowPost(world, above, state);
            world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.0);
         }
      }
   }
}
