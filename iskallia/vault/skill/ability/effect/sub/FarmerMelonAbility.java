package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.FarmerMelonConfig;
import iskallia.vault.skill.ability.effect.FarmerAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FarmerMelonAbility extends FarmerAbility<FarmerMelonConfig> {
   @Override
   protected boolean canGrowBlock(ServerLevel world, BlockPos pos, Block block, BlockState blockState) {
      return block instanceof SweetBerryBushBlock sweetBerryBushBlock
         ? sweetBerryBushBlock.isValidBonemealTarget(world, pos, blockState, false)
         : super.canGrowBlock(world, pos, block, blockState) || block instanceof StemBlock;
   }

   protected void doGrowBlock(FarmerMelonConfig config, ServerPlayer player, ServerLevel world, BlockPos pos, Block block, BlockState blockState) {
      super.doGrowBlock(config, player, world, pos, block, blockState);
      if (block instanceof StemBlock stemBlock) {
         if (stemBlock.isValidBonemealTarget(world, pos, blockState, false)) {
            BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, pos, player);
         } else {
            for (int i = 0; i < 40; i++) {
               blockState.randomTick(world, pos, world.random);
            }
         }

         world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.0);
      } else if (block instanceof SweetBerryBushBlock) {
         BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, pos, player);
         world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.0);
      }
   }
}
