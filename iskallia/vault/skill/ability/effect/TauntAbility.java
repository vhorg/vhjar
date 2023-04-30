package iskallia.vault.skill.ability.effect;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.TauntParticleMessage;
import iskallia.vault.skill.ability.effect.spi.AbstractTauntAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class TauntAbility extends AbstractTauntAbility {
   protected static final Predicate<LivingEntity> MONSTER_PREDICATE = entity -> entity.getType().getCategory() == MobCategory.MONSTER;
   private float damageModifier;

   public TauntAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float radius, int durationTicks, float damageModifier
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, radius, durationTicks);
      this.damageModifier = damageModifier;
   }

   public TauntAbility() {
   }

   public float getDamageModifier() {
      return this.damageModifier;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               player.removeEffect(ModEffects.TAUNT);
               player.addEffect(new MobEffectInstance(ModEffects.TAUNT, this.getDurationTicks(), 0, false, false, true));
               float radius = this.getRadius(player);
               List<Mob> nearbyMobs = player.level
                  .getNearbyEntities(
                     Mob.class, TargetingConditions.forCombat().range(radius).selector(MONSTER_PREDICATE), player, AABBHelper.create(player.position(), radius)
                  );
               List<BlockPos> candidateTeleportPositionList = this.getCandidateTeleportPositionList(player, 2, 8);

               for (Mob mob : nearbyMobs) {
                  if (player.hasLineOfSight(mob)) {
                     mob.setTarget(player);
                     mob.removeEffect(ModEffects.TARGET_OVERRIDE);
                     MobEffect effect = ModEffects.TARGET_OVERRIDE;
                     if (effect instanceof TauntAbility.TargetOverrideEffect overrideEffect) {
                        overrideEffect.setTauntingPlayer(player);
                     }

                     mob.addEffect(
                        new MobEffectInstance(effect, this.getDurationTicks(), 0, false, false) {
                           public boolean tick(LivingEntity livingEntity, Runnable p_19554_) {
                              if (!livingEntity.isDeadOrDying() && livingEntity.tickCount % 4 == 0) {
                                 ModNetwork.CHANNEL
                                    .send(
                                       PacketDistributor.ALL.noArg(),
                                       new TauntParticleMessage(
                                          new Vec3(
                                             livingEntity.getX(),
                                             livingEntity.getY() + livingEntity.getBbHeight() - livingEntity.getBbHeight() / 8.0F,
                                             livingEntity.getZ()
                                          ),
                                          livingEntity.getBbWidth()
                                       )
                                    );
                              }

                              return super.tick(livingEntity, p_19554_);
                           }
                        }
                     );
                     mob.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
                     mob.getNavigation().stop();
                     this.sortByShortestDistanceTo(candidateTeleportPositionList, player.blockPosition(), mob.getOnPos());
                     BlockPos validTeleportPosition = this.getValidTeleportPosition(candidateTeleportPositionList, player.level, mob.getBoundingBox());
                     if (validTeleportPosition != null) {
                        mob.setPos(validTeleportPosition.getX(), validTeleportPosition.getY(), validTeleportPosition.getZ());
                     }
                  }
               }

               return Ability.ActionResult.successCooldownImmediate();
            }
         )
         .orElse(Ability.ActionResult.fail());
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

   @Override
   protected void doParticles(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(
            player -> {
               float radius = this.getRadius(player);
               int particleCount = (int)Mth.clamp(Math.pow(radius, 2.0) * (float) Math.PI * 100.0, 50.0, 400.0);
               ((ServerLevel)player.level)
                  .sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), particleCount / 2, radius * 0.5, 0.5, radius * 0.5, 0.0);
               ((ServerLevel)player.level)
                  .sendParticles(
                     ParticleTypes.ANGRY_VILLAGER, player.getX(), player.getY(), player.getZ(), particleCount / 8, radius * 0.5, 0.5, radius * 0.5, 0.0
                  );
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
         );
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.TAUNT, SoundSource.PLAYERS, 0.4F, 1.0F);
         player.playNotifySound(ModSounds.TAUNT, SoundSource.PLAYERS, 0.4F, 1.0F);
      });
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void on(LivingHurtEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         MobEffectInstance effectInstance = mob.getEffect(ModEffects.TARGET_OVERRIDE);
         if (effectInstance != null) {
            if (effectInstance.getEffect() instanceof TauntAbility.TargetOverrideEffect overrideEffect
               && overrideEffect.tauntingPlayer instanceof ServerPlayer player) {
               AbilityTree abilities = PlayerAbilitiesData.get((ServerLevel)player.level).getAbilities(player);

               for (TauntAbility ability : abilities.getAll(TauntAbility.class, Skill::isUnlocked)) {
                  float damage = event.getAmount();
                  event.setAmount(damage * ability.getDamageModifier());
               }
            }
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageModifier), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.damageModifier = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageModifier)).ifPresent(tag -> nbt.put("damageModifier", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.damageModifier = Adapters.FLOAT.readNbt(nbt.get("damageModifier")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageModifier)).ifPresent(element -> json.add("damageModifier", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.damageModifier = Adapters.FLOAT.readJson(json.get("damageModifier")).orElse(0.0F);
   }

   public static class TargetOverrideEffect extends MobEffect {
      private static final int RANGE = 128;
      private Player tauntingPlayer = null;

      public TargetOverrideEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.NEUTRAL, color);
         this.setRegistryName(resourceLocation);
      }

      public void setTauntingPlayer(Player tauntingPlayer) {
         this.tauntingPlayer = tauntingPlayer;
      }

      public Player getTauntingPlayer() {
         return this.tauntingPlayer;
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
