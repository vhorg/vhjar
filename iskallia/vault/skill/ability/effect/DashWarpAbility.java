package iskallia.vault.skill.ability.effect;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModEntities;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ITeleporter;

public class DashWarpAbility extends InstantManaAbility {
   private float projectileLaunchForce;

   public DashWarpAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float projectileLaunchForce) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.projectileLaunchForce = projectileLaunchForce;
   }

   public DashWarpAbility() {
   }

   public float getProjectileLaunchForce() {
      return this.projectileLaunchForce;
   }

   @Override
   public String getAbilityGroupName() {
      return "Dash";
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(this::doAction).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(this::doParticles);
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(this::doSound);
   }

   private Ability.ActionResult doAction(ServerPlayer player) {
      DashWarpAbility.WarpArrow warpArrow = new DashWarpAbility.WarpArrow(player.level, player);
      warpArrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, this.getProjectileLaunchForce(), 1.0F);
      warpArrow.pickup = Pickup.DISALLOWED;
      player.level.addFreshEntity(warpArrow);
      return Ability.ActionResult.successCooldownImmediate();
   }

   private void doParticles(ServerPlayer serverPlayer) {
   }

   private void doSound(ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.5F, 2.0F);
      player.playNotifySound(SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.5F, 2.0F);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.projectileLaunchForce), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.projectileLaunchForce = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.FLOAT.writeNbt(Float.valueOf(this.projectileLaunchForce)).ifPresent(tag -> nbt.put("projectileLaunchForce", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.projectileLaunchForce = Adapters.FLOAT.readNbt(nbt.get("projectileLaunchForce")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.FLOAT.writeJson(Float.valueOf(this.projectileLaunchForce)).ifPresent(element -> json.add("projectileLaunchForce", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.projectileLaunchForce = Adapters.FLOAT.readJson(json.get("projectileLaunchForce")).orElse(0.0F);
   }

   public static class WarpArrow extends AbstractArrow {
      private static final EntityDataAccessor<Optional<UUID>> PLAYER_UUID = SynchedEntityData.defineId(
         DashWarpAbility.WarpArrow.class, EntityDataSerializers.OPTIONAL_UUID
      );
      private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(DashWarpAbility.WarpArrow.class, EntityDataSerializers.INT);
      private Player cachedPlayer;

      public WarpArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
         super(entityType, level);
      }

      public WarpArrow(Level level, ServerPlayer serverPlayer) {
         super(ModEntities.WARP_ARROW, serverPlayer, level);
         this.entityData.set(PLAYER_UUID, Optional.of(serverPlayer.getUUID()));
         this.entityData.set(AGE, 0);
         this.cachedPlayer = serverPlayer;
      }

      protected void defineSynchedData() {
         super.defineSynchedData();
         this.entityData.define(PLAYER_UUID, Optional.empty());
         this.entityData.define(AGE, 0);
      }

      @Nullable
      private Player getPlayer() {
         if (this.cachedPlayer != null && !this.cachedPlayer.isRemoved()) {
            return this.cachedPlayer;
         } else {
            this.cachedPlayer = ((Optional)this.entityData.get(PLAYER_UUID)).<Player>map(value -> this.level.getPlayerByUUID(value)).orElse(null);
            return this.cachedPlayer;
         }
      }

      protected boolean tryPickup(@Nonnull Player player) {
         return false;
      }

      @Nonnull
      protected ItemStack getPickupItem() {
         return ItemStack.EMPTY;
      }

      public void tick() {
         Player player = this.getPlayer();
         if (player == null || !player.isAlive()) {
            this.discard();
         }

         if (player != null && !player.level.isClientSide) {
            this.getEntityData().set(AGE, (Integer)this.getEntityData().get(AGE) + 1);
         }

         super.tick();
      }

      @ParametersAreNonnullByDefault
      @Nullable
      public Entity changeDimension(ServerLevel serverLevel, ITeleporter teleporter) {
         Player player = this.getPlayer();
         if (player != null && player.level.dimension() != serverLevel.dimension()) {
            this.discard();
         }

         return null;
      }

      protected void onHit(HitResult hitResult) {
         Type hitResultType = hitResult.getType();
         if (hitResultType == Type.BLOCK) {
            this.onHitBlock((BlockHitResult)hitResult);
         } else if (hitResultType == Type.ENTITY) {
            this.onHitEntity((EntityHitResult)hitResult);
         } else if (hitResultType == Type.MISS) {
            this.discard();
         }
      }

      protected void onHitBlock(@Nonnull BlockHitResult blockHitResult) {
         if (this.level instanceof ServerLevel serverLevel && !this.isRemoved()) {
            if (this.getPlayer() instanceof ServerPlayer serverPlayer
               && serverPlayer.connection.getConnection().isConnected()
               && serverPlayer.level == this.level
               && !serverPlayer.isSleeping()) {
               Vec3 origin = serverPlayer.position();
               this.getTeleportPosition(serverLevel, origin, this.position()).ifPresent(target -> {
                  this.teleport(serverPlayer, target);
                  this.playTeleportSound(serverLevel, serverPlayer, target);
                  this.sendTeleportParticles(serverLevel, origin);
               });
               this.discard();
            } else {
               this.discard();
            }
         }
      }

      protected void onHitEntity(@Nonnull EntityHitResult entityHitResult) {
         if (this.level instanceof ServerLevel serverLevel && !this.isRemoved()) {
            if (this.getPlayer() instanceof ServerPlayer serverPlayer
               && serverPlayer.connection.getConnection().isConnected()
               && serverPlayer.level == this.level
               && !serverPlayer.isSleeping()) {
               Entity entity = entityHitResult.getEntity();
               Vec3 origin = serverPlayer.position();
               Vec3 target = entity.position();
               Optional<Vec3> playerTarget = this.getTeleportPosition(serverLevel, origin, target);
               Optional<Vec3> entityTarget = this.getTeleportPosition(serverLevel, target, origin);
               if (playerTarget.isPresent() && entityTarget.isPresent()) {
                  this.teleport(serverPlayer, playerTarget.get());
                  this.playTeleportSound(serverLevel, serverPlayer, playerTarget.get());
                  this.sendTeleportParticles(serverLevel, origin);
                  if (this.canEntityBeTeleported(entity)) {
                     this.teleport(entity, entityTarget.get());
                     this.playTeleportSound(serverLevel, serverPlayer, entityTarget.get());
                  }

                  this.sendTeleportParticles(serverLevel, target);
               }

               this.discard();
            } else {
               this.discard();
            }
         }
      }

      private boolean canEntityBeTeleported(Entity target) {
         return target.isAlive() && target instanceof LivingEntity;
      }

      private void teleport(Entity entity, Vec3 target) {
         if (entity.isPassenger()) {
            entity.dismountTo(target.x, target.y, target.z);
         } else {
            entity.teleportTo(target.x, target.y, target.z);
         }

         entity.resetFallDistance();
      }

      private void sendTeleportParticles(ServerLevel serverLevel, Vec3 origin) {
         serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, origin.x(), origin.y() + 1.0, origin.z(), 100, 0.25, 0.5, 0.25, 0.0);
      }

      private void playTeleportSound(ServerLevel serverLevel, ServerPlayer player, Vec3 target) {
         serverLevel.playSound(player, target.x(), target.y(), target.z(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5F, 2.0F);
         player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5F, 2.0F);
      }

      private Optional<Vec3> getTeleportPosition(ServerLevel serverLevel, Vec3 origin, Vec3 target) {
         BlockPos targetBlockPos = new BlockPos(target);
         BlockPos originBlockPos = new BlockPos(origin);
         if (targetBlockPos.equals(originBlockPos)) {
            return Optional.empty();
         } else {
            List<BlockPos> candidatePositions = new ArrayList<>();

            for (int x = -1; x <= 1; x++) {
               for (int y = -1; y <= 1; y++) {
                  for (int z = -1; z <= 1; z++) {
                     candidatePositions.add(targetBlockPos.offset(x, y, z));
                  }
               }
            }

            Predicate<BlockPos> filter = bp -> this.isPassable(serverLevel.getBlockState(bp)) && this.isPassable(serverLevel.getBlockState(bp.above()));
            Comparator<BlockPos> comparator = Comparator.comparingDouble(value -> value.distSqr(targetBlockPos));
            comparator = comparator.thenComparingDouble(value -> value.distToCenterSqr(origin));
            candidatePositions = candidatePositions.stream().filter(filter).sorted(comparator).toList();
            if (candidatePositions.isEmpty()) {
               return Optional.empty();
            } else {
               BlockPos result = candidatePositions.get(0);
               return result.equals(originBlockPos) ? Optional.empty() : Optional.of(new Vec3(result.getX() + 0.5, result.getY(), result.getZ() + 0.5));
            }
         }
      }

      private boolean isPassable(BlockState blockState) {
         return blockState.isAir() ? true : !blockState.getFluidState().isEmpty();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WarpArrowRenderer extends ArrowRenderer<DashWarpAbility.WarpArrow> {
      public static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/warp_arrow.png");

      public WarpArrowRenderer(Context context) {
         super(context);
      }

      @Nonnull
      public ResourceLocation getTextureLocation(@Nonnull DashWarpAbility.WarpArrow entity) {
         return TEXTURE_LOCATION;
      }

      @ParametersAreNonnullByDefault
      public void render(
         DashWarpAbility.WarpArrow entity, float pEntityYaw, float partialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight
      ) {
         super.render(entity, pEntityYaw, partialTicks, pMatrixStack, pBuffer, pPackedLight);
         int age = (Integer)entity.getEntityData().get(DashWarpAbility.WarpArrow.AGE);
         if (age >= 2) {
            double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
            double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
            double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
            entity.level.addParticle(ParticleTypes.REVERSE_PORTAL, x, y, z, 0.0, 0.0, 0.0);
         }
      }
   }
}
