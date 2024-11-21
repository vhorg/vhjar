package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class IdentifiedArtifactItem extends BlockItem {
   public IdentifiedArtifactItem(Block block, Properties properties) {
      super(block, properties);
   }

   public void onDestroyed(ItemEntity itemEntity, DamageSource source) {
      super.onDestroyed(itemEntity, source);
      if ((source == DamageSource.IN_FIRE || source == DamageSource.LAVA) && itemEntity.level instanceof ServerLevel world) {
         ItemEntity newItemEntity = new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), new ItemStack(ModItems.ARTIFACT_FRAGMENT));
         this.spawnParticles(world, itemEntity.blockPosition());
         world.addFreshEntity(newItemEntity);
         BlockState block = world.getBlockState(itemEntity.blockPosition());
         if (block.getBlock() instanceof BaseFireBlock || block.getBlock() == Blocks.LAVA) {
            world.setBlock(itemEntity.blockPosition(), Blocks.AIR.defaultBlockState(), 3);
         }

         itemEntity.remove(RemovalReason.KILLED);
      }
   }

   private void spawnParticles(Level world, BlockPos pos) {
      for (int i = 0; i < 20; i++) {
         double d0 = world.random.nextGaussian() * 0.02;
         double d1 = world.random.nextGaussian() * 0.02;
         double d2 = world.random.nextGaussian() * 0.02;
         ((ServerLevel)world)
            .sendParticles(
               ParticleTypes.FLAME,
               pos.getX() + world.random.nextDouble() - d0,
               pos.getY() + world.random.nextDouble() - d1,
               pos.getZ() + world.random.nextDouble() - d2,
               10,
               d0,
               d1,
               d2,
               0.5
            );
      }

      world.playSound(null, pos, SoundEvents.GENERIC_BURN, SoundSource.BLOCKS, 1.0F, 1.0F);
   }
}
