package iskallia.vault.entity.entity;

import iskallia.vault.client.particles.FireballParticle;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.network.message.ClientboundFireballExplosionMessage;
import iskallia.vault.skill.ability.effect.spi.AbstractFireballAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class VaultFireball extends AbstractArrow {
   private static final EntityDataAccessor<Float> ID_DAMAGE = SynchedEntityData.defineId(VaultFireball.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Optional<UUID>> THROWER_UUID = SynchedEntityData.defineId(VaultFireball.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<Integer> ID_TYPE = SynchedEntityData.defineId(VaultFireball.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(VaultFireball.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> ID_BOUNCES = SynchedEntityData.defineId(VaultFireball.class, EntityDataSerializers.INT);
   public static int MAX_AGE = 4;
   private boolean grounded;
   private int life;
   public Vec3 prevDeltaMovement = new Vec3(0.0, 0.0, 0.0);
   private int bounceCount = 0;
   private int duration = 0;
   private boolean leftOwner;
   private boolean hasBeenShot;
   private boolean explodeDelayed;
   private int explodeDelayedTicks;

   public VaultFireball(EntityType<? extends AbstractArrow> entityType, Level level) {
      super(entityType, level);
   }

   public VaultFireball(Level level, LivingEntity thrower) {
      super(ModEntities.FIREBALL, thrower, level);
      this.setOwner(thrower);
      this.entityData.set(THROWER_UUID, Optional.of(thrower.getUUID()));
      this.entityData.set(ID_DAMAGE, this.getDamage());
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public void setType(int id) {
      this.setType(VaultFireball.FireballType.byId(id));
   }

   public void setType(VaultFireball.FireballType type) {
      this.entityData.set(ID_TYPE, type.ordinal());
   }

   public VaultFireball.FireballType getFireballType() {
      return VaultFireball.FireballType.byId((Integer)this.entityData.get(ID_TYPE));
   }

   public float getDamage() {
      if (this.getOwner() instanceof ServerPlayer serverPlayer) {
         AbilityTree abilities = PlayerAbilitiesData.get(serverPlayer.getLevel()).getAbilities(serverPlayer);
         Iterator var3 = abilities.getAll(AbstractFireballAbility.class, Skill::isUnlocked).iterator();
         if (var3.hasNext()) {
            AbstractFireballAbility ability = (AbstractFireballAbility)var3.next();
            return ability.getAbilityPower(serverPlayer);
         }
      }

      return 0.0F;
   }

   public VaultFireball createBouncingFireball(Level level, LivingEntity thrower, int bounceCount) {
      VaultFireball javelin = new VaultFireball(level, thrower);
      javelin.bounceCount = bounceCount;
      javelin.entityData.set(ID_BOUNCES, bounceCount);
      return javelin;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_DAMAGE, 0.0F);
      this.entityData.define(ID_TYPE, 0);
      this.entityData.define(ID_BOUNCES, 0);
      this.entityData.define(AGE, 0);
      this.entityData.define(THROWER_UUID, Optional.empty());
   }

   public byte getPierceLevel() {
      return 0;
   }

   public UUID getThrowerUUID() {
      return ((Optional)this.entityData.get(THROWER_UUID)).isPresent() ? (UUID)((Optional)this.entityData.get(THROWER_UUID)).get() : null;
   }

   public Player getThrower() {
      return ((Optional)this.entityData.get(THROWER_UUID)).isPresent()
         ? this.level.getPlayerByUUID((UUID)((Optional)this.entityData.get(THROWER_UUID)).get())
         : null;
   }

   public int getAge() {
      return (Integer)this.entityData.get(AGE);
   }

   public static boolean hasLineOfSight(Entity p_147185_, Player thrower) {
      if (p_147185_.level != thrower.level) {
         return false;
      } else {
         Vec3 vec3 = new Vec3(thrower.getX(), thrower.getEyeY(), thrower.getZ());
         Vec3 vec31 = new Vec3(p_147185_.getX(), p_147185_.getEyeY(), p_147185_.getZ());
         double theta = Math.atan2(p_147185_.getZ() - thrower.getZ(), p_147185_.getX() - thrower.getX());
         double angle1 = Math.toDegrees(theta - Math.toRadians(90.0));
         double angle2 = thrower.getYRot();
         double diff = (angle2 - angle1 + 180.0) % 360.0 - 180.0;
         diff = Math.abs(diff < -180.0 ? diff + 360.0 : diff);
         if (diff > 75.0) {
            return false;
         } else {
            return vec31.distanceTo(vec3) > 128.0
               ? false
               : thrower.level.clip(new ClipContext(vec3, vec31, Block.COLLIDER, Fluid.NONE, thrower)).getType() == Type.MISS;
         }
      }
   }

   private boolean shouldFall() {
      return this.inGround && this.level.noCollision(new AABB(this.position(), this.position()).inflate(0.06));
   }

   private void startFalling() {
      this.inGround = false;
      Vec3 vec3 = this.getDeltaMovement();
      this.life = 0;
   }

   private boolean checkLeftOwner() {
      Entity entity = this.getOwner();
      if (entity != null) {
         for (Entity entity1 : this.level
            .getEntities(
               this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), p_37272_ -> !p_37272_.isSpectator() && p_37272_.isPickable()
            )) {
            if (entity1.getRootVehicle() == entity.getRootVehicle()) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean isNoGravity() {
      return super.isNoGravity();
   }

   public void tick() {
      if (this.inGroundTime > 4) {
         this.grounded = true;
      }

      if (!this.level.isClientSide()) {
         if (this.explodeDelayed) {
            this.explodeDelayedTicks--;
            if (this.explodeDelayedTicks <= 0) {
               this.explode(this.position());
            }
         }

         if (this.getFireballType() == VaultFireball.FireballType.BOUNCING && this.duration != 0 && this.tickCount >= this.duration * 20) {
            this.explode(this.position());
         }
      }

      if (this.level.isClientSide() && !this.grounded && (this.tickCount > 4 || (Integer)this.entityData.get(ID_BOUNCES) > 0)) {
         this.particleTrail();
      }

      this.prevDeltaMovement = this.getDeltaMovement();
      this.baseTick();
      if (!this.hasBeenShot) {
         this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner(), this.blockPosition());
         this.hasBeenShot = true;
      }

      if (!this.leftOwner) {
         this.leftOwner = this.checkLeftOwner();
      }

      boolean flag = this.isNoPhysics();
      Vec3 vec3 = this.getDeltaMovement();
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         double d0 = vec3.horizontalDistance();
         this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
         this.setXRot((float)(Mth.atan2(vec3.y, d0) * 180.0F / (float)Math.PI));
         this.yRotO = this.getYRot();
         this.xRotO = this.getXRot();
      }

      BlockPos blockpos = this.blockPosition();
      BlockState blockstate = this.level.getBlockState(blockpos);
      if (!blockstate.isAir() && !flag) {
         VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
         if (!voxelshape.isEmpty()) {
            Vec3 vec31 = this.position();

            for (AABB aabb : voxelshape.toAabbs()) {
               if (aabb.move(blockpos).contains(vec31)) {
                  this.inGround = true;
                  break;
               }
            }
         }
      }

      if (this.shakeTime > 0) {
         this.shakeTime--;
      }

      if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW)) {
         this.clearFire();
      }

      if (this.inGround && !flag) {
         if (this.shouldFall()) {
            this.startFalling();
         } else if (!this.level.isClientSide) {
            this.tickDespawn();
         }

         this.inGroundTime++;
      } else {
         this.inGroundTime = 0;
         Vec3 vec32 = this.position();
         Vec3 vec33 = vec32.add(vec3);
         HitResult hitresult = this.level.clip(new ClipContext(vec32, vec33, Block.COLLIDER, Fluid.NONE, this));
         if (hitresult.getType() != Type.MISS) {
            vec33 = hitresult.getLocation();
         }

         while (!this.isRemoved()) {
            EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
            if (entityhitresult != null) {
               hitresult = entityhitresult;
            }

            if (hitresult != null && hitresult.getType() == Type.ENTITY) {
               Entity entity = ((EntityHitResult)hitresult).getEntity();
               Entity entity1 = this.getOwner();
               if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                  hitresult = null;
                  entityhitresult = null;
               }
            }

            if (hitresult != null && hitresult.getType() != Type.MISS && !flag && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
               this.onHit(hitresult);
               this.hasImpulse = true;
            }

            if (entityhitresult == null || this.getPierceLevel() <= 0) {
               break;
            }

            hitresult = null;
         }

         vec3 = this.getDeltaMovement();
         double d5 = vec3.x;
         double d6 = vec3.y;
         double d1 = vec3.z;
         if (this.isCritArrow()) {
            for (int i = 0; i < 4; i++) {
               this.level
                  .addParticle(ParticleTypes.CRIT, this.getX() + d5 * i / 4.0, this.getY() + d6 * i / 4.0, this.getZ() + d1 * i / 4.0, -d5, -d6 + 0.2, -d1);
            }
         }

         double d7 = this.getX() + d5;
         double d2 = this.getY() + d6;
         double d3 = this.getZ() + d1;
         double d4 = vec3.horizontalDistance();
         if (flag) {
            this.setYRot((float)(Mth.atan2(-d5, -d1) * 180.0F / (float)Math.PI));
         } else {
            this.setYRot((float)(Mth.atan2(d5, d1) * 180.0F / (float)Math.PI));
         }

         this.setXRot((float)(Mth.atan2(d6, d4) * 180.0F / (float)Math.PI));
         this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
         this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
         float f = 0.99F;
         float f1 = 0.05F;
         if (this.isInWater()) {
            for (int j = 0; j < 4; j++) {
               float f2 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25, d2 - d6 * 0.25, d3 - d1 * 0.25, d5, d6, d1);
            }

            f = this.getWaterInertia();
         }

         this.setDeltaMovement(vec3.scale(f));
         if (!this.isNoGravity() && !flag) {
            Vec3 vec34 = this.getDeltaMovement();
            boolean bouncing = this.getFireballType() == VaultFireball.FireballType.BOUNCING;
            this.setDeltaMovement(vec34.x, vec34.y - 0.025F * (bouncing ? 4 : 1), vec34.z);
         }

         this.setPos(d7, d2, d3);
         this.checkInsideBlocks();
      }
   }

   @NotNull
   protected List<LivingEntity> getTargetEntities(Level world, LivingEntity attacker, Vec3 pos, float radius) {
      AABB aabb = new AABB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius);
      return world.getEntitiesOfClass(LivingEntity.class, aabb);
   }

   protected void onHit(@NotNull HitResult result) {
      if (!this.level.isClientSide() && this.getOwner() instanceof LivingEntity livingEntity) {
         if (this.getFireballType() == VaultFireball.FireballType.BOUNCING && this.getOwner() != null) {
            if (result.getType() == Type.BLOCK) {
               Vec3 motion = this.prevDeltaMovement;
               Direction face = ((BlockHitResult)result).getDirection();
               Vec3 normal = new Vec3(face.getNormal().getX(), face.getNormal().getY(), face.getNormal().getZ());
               double dot = motion.dot(normal) * 1.5;
               Vec3 reflect = motion.subtract(normal.multiply(new Vec3(dot, dot, dot))).add(0.0, 0.1F, 0.0).multiply(new Vec3(0.9F, 0.9F, 0.9F));
               VaultFireball fireball = this.createBouncingFireball(this.level, livingEntity, this.bounceCount + 1);
               fireball.setPos(
                  result.getLocation().x() + reflect.normalize().x / 50.0,
                  result.getLocation().y() + reflect.normalize().y / 50.0,
                  result.getLocation().z() + reflect.normalize().z / 50.0
               );
               fireball.setDeltaMovement(reflect);
               double d0 = reflect.horizontalDistance();
               fireball.xRotO = (float)(Mth.atan2(reflect.y, d0) * 180.0F / (float)Math.PI);
               fireball.yRotO = (float)(Mth.atan2(reflect.x, reflect.z) * 180.0F / (float)Math.PI);
               fireball.updateRotation();
               fireball.pickup = Pickup.DISALLOWED;
               fireball.setType(this.getFireballType().ordinal());
               fireball.setDuration(this.getDuration());
               fireball.tickCount = this.tickCount;
               this.level.addFreshEntity(fireball);
               this.remove(RemovalReason.DISCARDED);
            } else if (result.getType() == Type.ENTITY && ((EntityHitResult)result).getEntity() instanceof LivingEntity living) {
               Entity entity = ((EntityHitResult)result).getEntity();
               if (this.getOwner() != null
                  && this.getOwner() instanceof ServerPlayer player
                  && entity instanceof LivingEntity livingHit
                  && !(livingHit instanceof Player)) {
                  float attackDamage = this.getDamage();
                  DamageSource damageSource = DamageSource.playerAttack(player);
                  ActiveFlags.IS_AP_ATTACKING
                     .runIfNotSet(() -> ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> livingHit.hurt(damageSource, attackDamage * 0.25F)));
                  player.level
                     .playSound(
                        null,
                        this.getPosition(0.0F).x,
                        this.getPosition(0.0F).y,
                        this.getPosition(0.0F).z,
                        SoundEvents.FIRECHARGE_USE,
                        SoundSource.PLAYERS,
                        0.2F,
                        1.0F
                     );
               }
            }
         }

         if (this.getFireballType() == VaultFireball.FireballType.FIRESHOT) {
            if (result.getType() == Type.BLOCK) {
               this.remove(RemovalReason.DISCARDED);
               ModNetwork.CHANNEL
                  .send(PacketDistributor.ALL.noArg(), new ClientboundFireballExplosionMessage(this.position().x, this.position().y, this.position().z, 0.5, 1));
               if (this.getOwner() != null && this.getOwner() instanceof ServerPlayer player) {
                  player.level
                     .playSound(
                        null,
                        this.getPosition(0.0F).x,
                        this.getPosition(0.0F).y,
                        this.getPosition(0.0F).z,
                        SoundEvents.GENERIC_EXPLODE,
                        SoundSource.PLAYERS,
                        0.2F,
                        1.25F
                     );
               }
            } else if (result.getType() == Type.ENTITY && ((EntityHitResult)result).getEntity() instanceof LivingEntity livingx && !(livingx instanceof Player)
               )
             {
               if (this.getOwner() != null && this.getOwner() instanceof ServerPlayer player) {
                  float attackDamage = this.getDamage();
                  DamageSource damageSource = DamageSource.playerAttack(player);
                  ActiveFlags.IS_AP_ATTACKING.runIfNotSet(() -> ActiveFlags.IS_FIRESHOT_ATTACKING.runIfNotSet(() -> living.hurt(damageSource, attackDamage)));
                  player.level
                     .playSound(
                        null,
                        this.getPosition(0.0F).x,
                        this.getPosition(0.0F).y,
                        this.getPosition(0.0F).z,
                        SoundEvents.GENERIC_EXPLODE,
                        SoundSource.PLAYERS,
                        0.2F,
                        1.25F
                     );
               }

               this.remove(RemovalReason.DISCARDED);
               ModNetwork.CHANNEL
                  .send(PacketDistributor.ALL.noArg(), new ClientboundFireballExplosionMessage(this.position().x, this.position().y, this.position().z, 0.5, 1));
            }
         }
      }

      if (!this.explodeDelayed && this.getFireballType() == VaultFireball.FireballType.BASE) {
         this.explodeDelayed();
      }

      super.onHit(result);
   }

   public void explodeDelayed() {
      this.explodeDelayed = true;
      this.explodeDelayedTicks = 2;
   }

   public void explode(Vec3 pos) {
      if (this.getOwner() != null && this.getOwner() instanceof ServerPlayer player) {
         float radius = AreaOfEffectHelper.adjustAreaOfEffectKey(player, this.getFireballType().getAbilityName(), 3.0F);
         List<LivingEntity> targetEntities = this.getTargetEntities(player.level, player, pos, radius);
         targetEntities.removeIf(livingEntity -> livingEntity.distanceToSqr(pos) > radius * radius);
         float attackDamage = this.getDamage();
         DamageSource damageSource = DamageSource.playerAttack(player);

         for (LivingEntity entity : targetEntities) {
            ActiveFlags.IS_AP_ATTACKING.runIfNotSet(() -> ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
               if (!(entity instanceof Player)) {
                  if (entity.hurt(damageSource, attackDamage)) {
                     double dx = pos.x - entity.getX();
                     double dz = pos.z - entity.getZ();
                     if (dx * dx + dz * dz < 1.0E-4) {
                        dx = (Math.random() - Math.random()) * 0.01;
                        dz = (Math.random() - Math.random()) * 0.01;
                     }

                     entity.knockback(0.6F, dx, dz);
                  }
               } else if (entity.hurtTime == 0 && !((Player)entity).isCreative()) {
                  entity.setHealth(entity.getHealth() - entity.getMaxHealth() * 0.2F);
                  if (entity.isDeadOrDying()) {
                     entity.die(entity.getLastDamageSource() != null ? entity.getLastDamageSource() : DamageSource.GENERIC);
                  }
               }
            }));
         }

         ModNetwork.CHANNEL
            .send(
               PacketDistributor.ALL.noArg(),
               new ClientboundFireballExplosionMessage(this.position().x, this.position().y, this.position().z, radius / 4.0F - 0.5F, 20)
            );
         ServerScheduler.INSTANCE
            .schedule(
               3,
               () -> ModNetwork.CHANNEL
                  .send(
                     PacketDistributor.ALL.noArg(),
                     new ClientboundFireballExplosionMessage(this.position().x, this.position().y, this.position().z, radius / 2.0F - 0.5F, 40)
                  )
            );
         ServerScheduler.INSTANCE
            .schedule(
               6,
               () -> ModNetwork.CHANNEL
                  .send(
                     PacketDistributor.ALL.noArg(),
                     new ClientboundFireballExplosionMessage(this.position().x, this.position().y, this.position().z, radius / 2.0F + radius / 4.0F - 0.5F, 60)
                  )
            );
         ServerScheduler.INSTANCE
            .schedule(
               9,
               () -> ModNetwork.CHANNEL
                  .send(
                     PacketDistributor.ALL.noArg(),
                     new ClientboundFireballExplosionMessage(
                        this.position().x, this.position().y, this.position().z, radius / 2.0F + radius / 4.0F + radius / 8.0F, 80
                     )
                  )
            );
         ServerScheduler.INSTANCE
            .schedule(
               12,
               () -> ModNetwork.CHANNEL
                  .send(
                     PacketDistributor.ALL.noArg(),
                     new ClientboundFireballExplosionMessage(this.position().x, this.position().y, this.position().z, radius, 100)
                  )
            );
         player.level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.2F, 1.0F);
      }

      this.remove(RemovalReason.DISCARDED);
   }

   private void particleTrail() {
      ParticleEngine pm = Minecraft.getInstance().particleEngine;

      for (int i = 0; i < 2; i++) {
         Vec3 offset = new Vec3(
            this.random.nextDouble() / 5.0 * (this.random.nextBoolean() ? 1 : -1), 0.0, this.random.nextDouble() / 5.0 * (this.random.nextBoolean() ? 1 : -1)
         );
         Vec3 direction = this.getDeltaMovement().normalize().scale(0.05F);
         Particle particle = pm.createParticle(
            ParticleTypes.FLAME,
            this.position().x + offset.x + direction.x * this.random.nextFloat() * 0.2F,
            this.position().y + direction.y * this.random.nextFloat() * 0.2F,
            this.position().z + offset.z + direction.z * this.random.nextFloat() * 0.2F,
            direction.x,
            direction.y,
            direction.z
         );
         if (particle != null) {
            particle.setLifetime((int)(10.0 + this.random.nextFloat() * 3.0));
         }
      }

      for (int ix = 0; ix < 1; ix++) {
         Vec3 offset = new Vec3(
            this.random.nextDouble() / 8.0 * (this.random.nextBoolean() ? 1 : -1),
            this.random.nextDouble() / 8.0 * (this.random.nextBoolean() ? 1 : -1),
            this.random.nextDouble() / 8.0 * (this.random.nextBoolean() ? 1 : -1)
         );
         Vec3 direction = this.getDeltaMovement().normalize().scale(0.05F);
         Particle particle = pm.createParticle(
            (ParticleOptions)ModParticles.FIREBALL_CLOUD.get(),
            this.position().x + offset.x + direction.x * this.random.nextFloat() * 0.2F,
            this.position().y + offset.y + direction.y * this.random.nextFloat() * 0.2F,
            this.position().z + offset.z + direction.z * this.random.nextFloat() * 0.2F,
            direction.x,
            direction.y,
            direction.z
         );
         if (particle instanceof FireballParticle) {
            FireballParticle fireballParticle = (FireballParticle)particle;
            float colorOffset = this.random.nextFloat() * 0.2F;
            if (this.random.nextBoolean()) {
               fireballParticle.setStartColor(0.48359376F + colorOffset, 0.06953125F + colorOffset, 0.0703125F);
            } else {
               fireballParticle.setStartColor(0.75F + colorOffset, 0.35F + colorOffset, 0.0F);
            }

            float col = Mth.nextFloat(this.random, 0.01F, 0.15F);
            fireballParticle.setEndColor(col, col, col);
            if (this.getFireballType() == VaultFireball.FireballType.FIRESHOT) {
               fireballParticle.scale(0.5F);
            }
         }
      }
   }

   protected boolean canHitEntity(Entity entity) {
      return !(entity instanceof LivingEntity livingEntity)
         ? false
         : super.canHitEntity(entity) && !(entity instanceof Player) && !(entity instanceof EternalEntity);
   }

   public void onInsideBubbleColumn(boolean pDownwards) {
      super.onInsideBubbleColumn(pDownwards);
   }

   protected void handleNetherPortal() {
   }

   public boolean canChangeDimensions() {
      return false;
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
      return this.grounded ? null : super.findHitEntity(pStartVec, pEndVec);
   }

   protected void onHitEntity(EntityHitResult pResult) {
   }

   protected void doPostHurtEffects(LivingEntity pLiving) {
      super.doPostHurtEffects(pLiving);
   }

   protected void onHitBlock(BlockHitResult p_36755_) {
      super.onHitBlock(p_36755_);
   }

   protected boolean tryPickup(Player p_150196_) {
      return false;
   }

   protected ItemStack getPickupItem() {
      return null;
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.ARROW_HIT;
   }

   public void playSound(SoundEvent pSound, float pVolume, float pPitch) {
      if (pSound != this.getDefaultHitGroundSoundEvent()) {
         super.playSound(pSound, pVolume, pPitch);
      }
   }

   public void playerTouch(Player pEntity) {
   }

   public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      Entity entity = this.level.getEntity(pPacket.getData());
      if (entity != null) {
         this.setOwner(entity);
      }
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.grounded = pCompound.getBoolean("Grounded");
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("Grounded", this.grounded);
   }

   public void setAge(int age) {
      this.entityData.set(AGE, age);
      this.life = age;
   }

   public void tickDespawn() {
      if (this.grounded) {
         this.setAge(this.life + 1);
         if (this.life >= MAX_AGE) {
            this.explode(this.position());
         }
      } else {
         this.setAge(0);
      }
   }

   protected float getWaterInertia() {
      return 0.99F;
   }

   public boolean shouldRender(double pX, double pY, double pZ) {
      return true;
   }

   public static enum FireballType {
      BASE("Fireball_Base"),
      BOUNCING("Fireball_Volley"),
      FIRESHOT("Fireball_Fireshot");

      private final String abilityName;

      private FireballType(String abilityName) {
         this.abilityName = abilityName;
      }

      public String getAbilityName() {
         return this.abilityName;
      }

      @Override
      public String toString() {
         return this.name().toLowerCase(Locale.ROOT);
      }

      public static VaultFireball.FireballType byId(int pId) {
         return MiscUtils.getEnumEntry(VaultFireball.FireballType.class, pId);
      }
   }
}
