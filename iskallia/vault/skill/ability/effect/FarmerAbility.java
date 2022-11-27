package iskallia.vault.skill.ability.effect;

import iskallia.vault.skill.ability.config.FarmerConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractFarmerAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FarmerAbility<C extends FarmerConfig> extends AbstractFarmerAbility<C> {
   @Override
   protected boolean canGrowBlock(ServerLevel world, BlockPos pos, Block block, BlockState blockState) {
      if (block instanceof CropBlock cropBlock) {
         return cropBlock.isValidBonemealTarget(world, pos, blockState, false);
      } else {
         return block instanceof SaplingBlock saplingBlock ? saplingBlock.isValidBonemealTarget(world, pos, blockState, false) : false;
      }
   }

   @Override
   protected void doGrowBlock(C config, ServerPlayer player, ServerLevel world, BlockPos pos, Block block, BlockState blockState) {
      if (block instanceof CropBlock || block instanceof SaplingBlock) {
         BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, pos, player);
         world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.0);
      }
   }
}
