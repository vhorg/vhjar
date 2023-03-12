package iskallia.vault.skill.ability.effect;

import com.google.common.collect.Sets;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.TauntConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractTauntAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TauntAbility extends AbstractTauntAbility<TauntConfig> {
   protected static final Predicate<LivingEntity> MONSTER_PREDICATE = entity -> entity.getType().getCategory() == MobCategory.MONSTER;

   protected AbilityActionResult doAction(TauntConfig config, ServerPlayer player, boolean active) {
      player.removeEffect(ModEffects.TAUNT);
      player.addEffect(new MobEffectInstance(ModEffects.TAUNT, config.getDurationTicks(), 0, false, false, true));
      float radius = config.getRadius(player);
      List<Mob> nearbyMobs = player.level
         .getNearbyEntities(
            Mob.class, TargetingConditions.forCombat().range(radius).selector(MONSTER_PREDICATE), player, player.getBoundingBox().inflate(radius)
         );
      List<BlockPos> candidateTeleportPositionList = this.getCandidateTeleportPositionList(player, 2, 8);

      for (Mob mob : nearbyMobs) {
         if (player.hasLineOfSight(mob)) {
            mob.setTarget(player);
            mob.removeEffect(ModEffects.TARGET_OVERRIDE);
            mob.addEffect(new MobEffectInstance(ModEffects.TARGET_OVERRIDE, config.getDurationTicks()));
            mob.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
            mob.getNavigation().stop();
            this.sortByShortestDistanceTo(candidateTeleportPositionList, player.blockPosition(), mob.getOnPos());
            BlockPos validTeleportPosition = this.getValidTeleportPosition(candidateTeleportPositionList, player.level, mob.getBoundingBox());
            if (validTeleportPosition != null) {
               mob.setPos(validTeleportPosition.getX(), validTeleportPosition.getY(), validTeleportPosition.getZ());
            }
         }
      }

      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected List<BlockPos> getCandidateTeleportPositionList(ServerPlayer player, int minRadius, int maxRadius) {
      int minRadiusSqr = minRadius * minRadius;
      int maxRadiusSqr = maxRadius * maxRadius;
      List<BlockPos> result = new ArrayList<>();

      for (int x = -maxRadius; x <= maxRadius; x++) {
         for (int y = -maxRadius; y <= maxRadius; y++) {
            for (int z = -maxRadius; z <= maxRadius; z++) {
               int distanceSqr = x * x + y * y + z * z;
               if (distanceSqr >= minRadiusSqr && distanceSqr <= maxRadiusSqr) {
                  result.add(new BlockPos(player.getX() + x, player.getY() + y, player.getZ() + z));
               }
            }
         }
      }

      return result;
   }

   protected void sortByShortestDistanceTo(List<BlockPos> candidateTeleportPositionList, BlockPos playerBlockPos, BlockPos mobBlockPos) {
      candidateTeleportPositionList.sort(Comparator.comparingLong(pos -> {
         long px = playerBlockPos.getX() - pos.getX();
         long py = playerBlockPos.getY() - pos.getY();
         long pz = playerBlockPos.getZ() - pos.getZ();
         long mx = mobBlockPos.getX() - pos.getX();
         long my = mobBlockPos.getY() - pos.getY();
         long mz = mobBlockPos.getZ() - pos.getZ();
         return px * px + py * py + pz * pz + mx * mx + my * my + mz * mz;
      }));
   }

   @Nullable
   protected BlockPos getValidTeleportPosition(List<BlockPos> candidateTeleportPositionList, Level level, AABB boundingBox) {
      for (BlockPos blockPos : candidateTeleportPositionList) {
         AABB adjustedAABB = AABB.ofSize(Vec3.ZERO, boundingBox.getXsize(), boundingBox.getYsize(), boundingBox.getZsize()).move(blockPos);
         if (!this.isCollidingWithBlock(level, adjustedAABB)) {
            return blockPos;
         }
      }

      return null;
   }

   protected boolean isCollidingWithBlock(Level level, AABB boundingBox) {
      Set<BlockState> validBlockStateSet = Sets.newHashSet(new BlockState[]{Blocks.AIR.defaultBlockState(), Blocks.WATER.defaultBlockState()});
      BlockPos blockPosMin = new BlockPos(boundingBox.minX + 0.001, boundingBox.minY + 0.001, boundingBox.minZ + 0.001);
      BlockPos blockPosMax = new BlockPos(boundingBox.maxX - 0.001, boundingBox.maxY - 0.001, boundingBox.maxZ - 0.001);
      if (level.hasChunksAt(blockPosMin, blockPosMax)) {
         MutableBlockPos pos = new MutableBlockPos();

         for (int x = blockPosMin.getX(); x <= blockPosMax.getX(); x++) {
            for (int y = blockPosMin.getY(); y <= blockPosMax.getY(); y++) {
               for (int z = blockPosMin.getZ(); z <= blockPosMax.getZ(); z++) {
                  if (!validBlockStateSet.contains(level.getBlockState(pos.set(x, y, z)))) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected void doParticles(TauntConfig config, ServerPlayer player) {
      float radius = config.getRadius(player);
      int particleCount = (int)Mth.clamp(Math.pow(radius, 2.0) * (float) Math.PI * 100.0, 50.0, 400.0);
      ((ServerLevel)player.level)
         .sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), particleCount / 2, radius * 0.5, 0.5, radius * 0.5, 0.0);
      ((ServerLevel)player.level)
         .sendParticles(ParticleTypes.ANGRY_VILLAGER, player.getX(), player.getY(), player.getZ(), particleCount / 8, radius * 0.5, 0.5, radius * 0.5, 0.0);
      AreaEffectCloud areaEffectCloud = new AreaEffectCloud(player.level, player.getX(), player.getY(), player.getZ());
      areaEffectCloud.setOwner(player);
      areaEffectCloud.setRadius(radius);
      areaEffectCloud.setRadiusOnUse(-0.5F);
      areaEffectCloud.setWaitTime(0);
      areaEffectCloud.setDuration(4);
      areaEffectCloud.setPotion(Potions.EMPTY);
      areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / areaEffectCloud.getDuration());
      areaEffectCloud.setFixedColor(Color.RED.getRGB());
      areaEffectCloud.setParticle(ParticleTypes.SMOKE);
      player.level.addFreshEntity(areaEffectCloud);
   }

   protected void doSound(TauntConfig config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.TAUNT, SoundSource.PLAYERS, 1.0F, 1.0F);
      player.playNotifySound(ModSounds.TAUNT, SoundSource.PLAYERS, 1.0F, 1.0F);
   }

   public static class TargetOverrideEffect extends MobEffect {
      private static final int RANGE = 128;

      public TargetOverrideEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.NEUTRAL, color);
         this.setRegistryName(resourceLocation);
      }

      public boolean isDurationEffectTick(int duration, int amplifier) {
         return duration % 10 == 0;
      }

      public void applyEffectTick(@Nonnull LivingEntity livingEntity, int amplifier) {
         if (livingEntity instanceof Mob mob) {
            Player nearestPlayer = livingEntity.level
               .getNearestPlayer(TargetingConditions.forNonCombat().range(128.0).selector(entity -> entity.hasEffect(ModEffects.TAUNT)), livingEntity);
            if (nearestPlayer != null) {
               mob.setTarget(nearestPlayer);
            }
         }
      }
   }

   public static class TauntEffect extends MobEffect {
      public TauntEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.NEUTRAL, color);
         this.setRegistryName(resourceLocation);
      }
   }
}
