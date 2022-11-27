package iskallia.vault.skill.ability.effect.spi;

import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.config.HunterConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.world.data.ServerVaults;
import java.awt.Color;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHunterAbility<C extends HunterConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Hunter";
   }

   protected boolean canDoAction(C config, ServerPlayer player, boolean active) {
      return super.canDoAction(config, player, active) && ServerVaults.isInVault(player);
   }

   protected AbilityActionResult doAction(C config, ServerPlayer player, boolean active) {
      if (!(player.getCommandSenderWorld() instanceof ServerLevel serverWorld)) {
         return AbilityActionResult.FAIL;
      } else {
         for (int delay = 0; delay < config.getDurationTicks() / 5; delay++) {
            ServerScheduler.INSTANCE
               .schedule(
                  delay * 5,
                  () -> this.selectPositions(config, serverWorld, player)
                     .forEach(
                        highlightPosition -> {
                           Color color = highlightPosition.color;

                           for (int i = 0; i < 8; i++) {
                              Vec3 v = MiscUtils.getRandomOffset(highlightPosition.blockPos, RANDOM);
                              serverWorld.sendParticles(
                                 player,
                                 (SimpleParticleType)ModParticles.DEPTH_FIREWORK.get(),
                                 true,
                                 v.x,
                                 v.y,
                                 v.z,
                                 0,
                                 color.getRed() / 255.0F,
                                 color.getGreen() / 255.0F,
                                 color.getBlue() / 255.0F,
                                 1.0
                              );
                           }
                        }
                     )
               );
         }

         return AbilityActionResult.SUCCESS_COOLDOWN;
      }
   }

   protected void doParticles(C config, ServerPlayer player) {
   }

   protected void doSound(C config, ServerPlayer player) {
   }

   protected abstract List<AbstractHunterAbility.HighlightPosition> selectPositions(C var1, ServerLevel var2, ServerPlayer var3);

   protected void forEachTileEntity(C config, Level world, Player player, BiConsumer<BlockPos, BlockEntity> consumer) {
      BlockPos playerOffset = player.blockPosition();
      double radius = config.getSearchRadius();
      double radiusSq = radius * radius;
      int iRadius = Mth.ceil(radius);
      Vec3i radVec = new Vec3i(iRadius, iRadius, iRadius);
      ChunkPos posMin = new ChunkPos(player.blockPosition().subtract(radVec));
      ChunkPos posMax = new ChunkPos(player.blockPosition().offset(radVec));

      for (int xx = posMin.x; xx <= posMax.x; xx++) {
         for (int zz = posMin.z; zz <= posMax.z; zz++) {
            LevelChunk ch = world.getChunkSource().getChunkNow(xx, zz);
            if (ch != null) {
               ch.getBlockEntities().forEach((pos, tile) -> {
                  if (tile != null && pos.distSqr(playerOffset) <= radiusSq) {
                     consumer.accept(pos, tile);
                  }
               });
            }
         }
      }
   }

   public record HighlightPosition(BlockPos blockPos, Color color) {
   }
}
