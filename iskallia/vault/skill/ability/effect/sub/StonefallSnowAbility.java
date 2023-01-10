package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.StonefallSnowConfig;
import iskallia.vault.skill.ability.effect.AbstractStonefallAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class StonefallSnowAbility extends AbstractStonefallAbility<StonefallSnowConfig> {
   protected AbilityActionResult doAction(StonefallSnowConfig config, ServerPlayer player, boolean active) {
      float radius = config.getRadius();
      BlockHelper.withVerticalCylinderPositions(player.blockPosition(), radius, Math.max(2.0F, radius / 2.0F), radius, blockPos -> {
         if (this.checkAndReplace(player.level, blockPos, Blocks.POWDER_SNOW, Blocks.AIR.defaultBlockState())) {
            this.doReplaceParticles(player.level, blockPos);
            this.doReplaceSound(player, blockPos);
         }
      });
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   private boolean checkAndReplace(Level level, BlockPos blockPos, Block toReplace, BlockState replaceWith) {
      if (level.getBlockState(blockPos).is(toReplace)) {
         level.setBlockAndUpdate(blockPos, replaceWith);
         return true;
      } else {
         return false;
      }
   }

   private void doReplaceParticles(Level level, BlockPos blockPos) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleTypes.POOF, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 25, 0.5, 0.5, 0.5, 0.0);
      }
   }

   private void doReplaceSound(ServerPlayer player, BlockPos blockPos) {
      player.level.playSound(player, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.2F, 1.0F);
      player.playNotifySound(SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.2F, 1.0F);
   }

   protected void doParticles(StonefallSnowConfig config, ServerPlayer player) {
      float radius = config.getRadius();
      int particleCount = (int)Mth.clamp(Math.pow(radius, 2.0) * (float) Math.PI * 100.0, 50.0, 400.0);
      ((ServerLevel)player.level)
         .sendParticles(ParticleTypes.FLAME, player.getX(), player.getY(), player.getZ(), particleCount, radius * 0.5, 0.5, radius * 0.5, 0.0);
   }

   protected void doSound(StonefallSnowConfig config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.5F, 1.0F);
      player.playNotifySound(SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.5F, 1.0F);
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 0.5F, 1.0F);
      player.playNotifySound(SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 0.5F, 1.0F);
   }
}
