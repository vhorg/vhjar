package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.VaultBoss;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractTauntAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import java.awt.Color;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
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
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class TauntRepelAbility extends AbstractTauntAbility {
   protected static final Predicate<LivingEntity> MONSTER_PREDICATE = entity -> entity.getType().getCategory() == MobCategory.MONSTER
      && !(entity instanceof VaultBoss)
      && !entity.hasEffect(ModEffects.TAUNT_CHARM);
   private float repelForce;

   public TauntRepelAbility(
      int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float radius, int durationTicks, float repelForce
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, radius, durationTicks);
      this.repelForce = repelForce;
   }

   public TauntRepelAbility() {
   }

   public float getRepelForce() {
      return this.repelForce;
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               player.removeEffect(ModEffects.TAUNT_REPEL_PLAYER);
               player.addEffect(new MobEffectInstance(ModEffects.TAUNT_REPEL_PLAYER, this.durationTicks, 0, false, false, true));
               float radius = this.getRadius(player);

               for (Mob mob : player.level
                  .getNearbyEntities(
                     Mob.class, TargetingConditions.forCombat().range(radius).selector(MONSTER_PREDICATE), player, AABBHelper.create(player.position(), radius)
                  )) {
                  if (player.hasLineOfSight(mob)) {
                     mob.setTarget(null);
                     mob.removeEffect(ModEffects.TAUNT_REPEL_MOB);
                     mob.addEffect(new MobEffectInstance(ModEffects.TAUNT_REPEL_MOB, this.durationTicks, 0, false, true, false));
                     Vec3 force = mob.position().subtract(player.position()).normalize().scale(this.repelForce);
                     mob.push(force.x, force.y, force.z);
                     mob.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
                     mob.getNavigation().stop();
                  }
               }

               return Ability.ActionResult.successCooldownImmediate();
            }
         )
         .orElse(Ability.ActionResult.fail());
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
                  .sendParticles(ParticleTypes.SOUL, player.getX(), player.getY(), player.getZ(), particleCount / 2, radius * 0.5, 0.5, radius * 0.5, 0.0);
               AreaEffectCloud areaEffectCloud = new AreaEffectCloud(player.level, player.getX(), player.getY(), player.getZ());
               areaEffectCloud.setOwner(player);
               areaEffectCloud.setRadius(radius);
               areaEffectCloud.setRadiusOnUse(-0.5F);
               areaEffectCloud.setWaitTime(0);
               areaEffectCloud.setDuration(4);
               areaEffectCloud.setPotion(Potions.EMPTY);
               areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / areaEffectCloud.getDuration());
               areaEffectCloud.setFixedColor(Color.BLACK.getRGB());
               areaEffectCloud.setParticle(ParticleTypes.SMOKE);
               player.level.addFreshEntity(areaEffectCloud);
            }
         );
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.TAUNT_REPEL, SoundSource.PLAYERS, 1.0F, 1.0F);
         player.playNotifySound(ModSounds.TAUNT_REPEL, SoundSource.PLAYERS, 1.0F, 1.0F);
      });
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.repelForce), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.repelForce = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.repelForce)).ifPresent(tag -> nbt.put("repelForce", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.repelForce = Adapters.FLOAT.readNbt(nbt.get("repelForce")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.repelForce)).ifPresent(element -> json.add("repelForce", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.repelForce = Adapters.FLOAT.readJson(json.get("repelForce")).orElse(0.0F);
   }

   public static class FearGoal extends Goal {
      private static final int RANGE = 1024;
      private static final int RADIUS = 10;
      private static final int VERTICAL_DISTANCE = 4;
      protected final PathfinderMob mob;
      protected final double speedModifier;
      protected double posX;
      protected double posY;
      protected double posZ;

      public FearGoal(PathfinderMob mob, double speedModifier) {
         this.mob = mob;
         this.speedModifier = speedModifier;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (this.mob.getDeltaMovement().lengthSqr() > 2.0) {
            return false;
         } else {
            Player nearestPlayer = this.mob
               .level
               .getNearestPlayer(TargetingConditions.forNonCombat().range(1024.0).selector(entity -> entity.hasEffect(ModEffects.TAUNT_REPEL_PLAYER)), this.mob);
            Vec3 randomPos = null;

            for (int i = 0; i < 10; i++) {
               if (nearestPlayer == null) {
                  randomPos = DefaultRandomPos.getPos(this.mob, 10, 4);
               } else {
                  randomPos = DefaultRandomPos.getPosAway(this.mob, 10, 4, nearestPlayer.position());
               }

               if (randomPos != null) {
                  break;
               }
            }

            if (randomPos == null) {
               return false;
            } else {
               this.posX = randomPos.x;
               this.posY = randomPos.y;
               this.posZ = randomPos.z;
               return true;
            }
         }
      }

      public void start() {
         this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
      }

      public boolean canContinueToUse() {
         if (this.mob.getNavigation().isDone() && this.canUse()) {
            this.start();
         }

         return true;
      }

      public boolean isInterruptable() {
         return false;
      }
   }

   public static class TauntRepelMobEffect extends MobEffect {
      private static final double SPEED_MODIFIER = 1.25;

      public TauntRepelMobEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.NEUTRAL, color);
         this.setRegistryName(resourceLocation);
      }

      public boolean isDurationEffectTick(int duration, int amplifier) {
         return true;
      }

      public void applyEffectTick(@Nonnull LivingEntity livingEntity, int amplifier) {
         if (livingEntity instanceof Mob mob) {
            mob.goalSelector
               .getRunningGoals()
               .filter(wrappedGoalx -> !(wrappedGoalx.getGoal() instanceof TauntRepelAbility.FearGoal))
               .forEach(WrappedGoal::stop);
            mob.setTarget(null);

            for (WrappedGoal wrappedGoal : mob.goalSelector.getAvailableGoals()) {
               if (wrappedGoal.getGoal() instanceof TauntRepelAbility.FearGoal fearGoal && !wrappedGoal.isRunning() && fearGoal.canUse()) {
                  wrappedGoal.start();
                  break;
               }
            }
         }
      }

      @ParametersAreNonnullByDefault
      public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         super.addAttributeModifiers(livingEntity, attributeMap, amplifier);
         if (livingEntity instanceof PathfinderMob mob) {
            mob.goalSelector.addGoal(0, new TauntRepelAbility.FearGoal(mob, 1.25));
         }
      }

      @ParametersAreNonnullByDefault
      public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
         if (livingEntity instanceof PathfinderMob mob) {
            mob.goalSelector
               .getAvailableGoals()
               .stream()
               .filter(wrappedGoal -> wrappedGoal.getGoal() instanceof TauntRepelAbility.FearGoal)
               .forEach(wrappedGoal -> {
                  wrappedGoal.stop();
                  mob.goalSelector.removeGoal(wrappedGoal.getGoal());
               });
         }
      }
   }

   public static class TauntRepelPlayerEffect extends MobEffect {
      public TauntRepelPlayerEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.NEUTRAL, color);
         this.setRegistryName(resourceLocation);
      }
   }
}
