package iskallia.vault.entity.entity;

import com.google.common.collect.Lists;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundHunterParticlesFromJavelinMessage;
import iskallia.vault.network.message.ClientboundSightParticlesFromJavelinMessage;
import iskallia.vault.skill.ability.effect.JavelinAbility;
import iskallia.vault.skill.ability.effect.JavelinPiercingAbility;
import iskallia.vault.skill.ability.effect.JavelinScatterAbility;
import iskallia.vault.skill.ability.effect.JavelinSightAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractJavelinAbility;
import iskallia.vault.skill.ability.effect.spi.HunterAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.world.data.PlayerAbilitiesData;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

public class VaultThrownJavelin extends AbstractArrow {
   private static final EntityDataAccessor<Float> ID_DAMAGE = SynchedEntityData.defineId(VaultThrownJavelin.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Optional<UUID>> THROWER_UUID = SynchedEntityData.defineId(
      VaultThrownJavelin.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> ID_TYPE = SynchedEntityData.defineId(VaultThrownJavelin.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(VaultThrownJavelin.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> ID_BOUNCES = SynchedEntityData.defineId(VaultThrownJavelin.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> IS_GHOST = SynchedEntityData.defineId(VaultThrownJavelin.class, EntityDataSerializers.BOOLEAN);
   public static int MAX_AGE = 120;
   private boolean grounded = false;
   private boolean hasHitBlock = false;
   private boolean maxPierced = false;
   private int life;
   public Vec3 prevDeltaMovement = new Vec3(0.0, 0.0, 0.0);
   @Nullable
   private IntOpenHashSet piercingIgnoreEntityIds;
   @Nullable
   private List<Entity> piercedAndKilledEntities;
   private LivingEntity thrower = null;
   private int bounceCount = 0;
   private static ArrayAdapter<TilePredicate> KEYS = Adapters.ofArray(TilePredicate[]::new, Adapters.TILE_PREDICATE);

   public VaultThrownJavelin(EntityType<? extends AbstractArrow> entityType, Level level) {
      super(entityType, level);
   }

   public VaultThrownJavelin(Level level, LivingEntity thrower) {
      super(ModEntities.THROWN_JAVELIN, thrower, level);
      this.thrower = thrower;
      this.entityData.set(THROWER_UUID, Optional.of(thrower.getUUID()));
      this.entityData.set(ID_DAMAGE, this.getDamage());
   }

   public void setType(int id) {
      this.setType(VaultThrownJavelin.JavelinType.byId(id));
   }

   public void setType(VaultThrownJavelin.JavelinType type) {
      this.entityData.set(ID_TYPE, type.ordinal());
   }

   public VaultThrownJavelin.JavelinType getJavelinType() {
      return VaultThrownJavelin.JavelinType.byId((Integer)this.entityData.get(ID_TYPE));
   }

   public boolean getIsGhost() {
      return (Boolean)this.entityData.get(IS_GHOST);
   }

   public void setIsGhost() {
      this.entityData.set(IS_GHOST, true);
   }

   public float getDamage() {
      if (((Optional)this.entityData.get(THROWER_UUID)).isPresent()
         && this.level.getPlayerByUUID((UUID)((Optional)this.entityData.get(THROWER_UUID)).get()) instanceof ServerPlayer serverPlayer) {
         AbilityTree abilities = PlayerAbilitiesData.get(serverPlayer.getLevel()).getAbilities(serverPlayer);
         Iterator var4 = abilities.getAll(AbstractJavelinAbility.class, Skill::isUnlocked).iterator();
         if (var4.hasNext()) {
            AbstractJavelinAbility ability = (AbstractJavelinAbility)var4.next();
            return ability.getAttackDamage(serverPlayer);
         }
      }

      return 0.0F;
   }

   public VaultThrownJavelin createBouncingJavelin(Level level, LivingEntity thrower, int bounceCount) {
      if (thrower == null) {
         return null;
      } else {
         VaultThrownJavelin javelin = new VaultThrownJavelin(level, thrower);
         javelin.bounceCount = bounceCount;
         javelin.entityData.set(ID_BOUNCES, bounceCount);
         javelin.entityData.set(IS_GHOST, (Boolean)this.entityData.get(IS_GHOST));
         javelin.piercingIgnoreEntityIds = this.piercingIgnoreEntityIds;
         javelin.piercedAndKilledEntities = this.piercedAndKilledEntities;
         javelin.maxPierced = this.maxPierced;
         return javelin;
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_DAMAGE, 0.0F);
      this.entityData.define(ID_TYPE, 0);
      this.entityData.define(ID_BOUNCES, 0);
      this.entityData.define(IS_GHOST, false);
      this.entityData.define(AGE, 0);
      this.entityData.define(THROWER_UUID, Optional.empty());
   }

   public byte getPierceLevel() {
      if (((Optional)this.entityData.get(THROWER_UUID)).isPresent()
         && this.level.getPlayerByUUID((UUID)((Optional)this.entityData.get(THROWER_UUID)).get()) instanceof ServerPlayer serverPlayer) {
         AbilityTree abilities = PlayerAbilitiesData.get(serverPlayer.getLevel()).getAbilities(serverPlayer);
         Iterator var4 = abilities.getAll(JavelinPiercingAbility.class, Skill::isUnlocked).iterator();
         if (var4.hasNext()) {
            JavelinPiercingAbility ability = (JavelinPiercingAbility)var4.next();
            return (byte)ability.getPiercing();
         }

         var4 = abilities.getAll(JavelinScatterAbility.class, Skill::isUnlocked).iterator();
         if (var4.hasNext()) {
            JavelinScatterAbility ability = (JavelinScatterAbility)var4.next();
            return (byte)ability.getPiercing();
         }
      }

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

   private void startFalling() {
      this.inGround = false;
      Vec3 vec3 = this.getDeltaMovement();
      this.setDeltaMovement(vec3.multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F));
      this.life = 0;
   }

   private boolean shouldFall() {
      return this.inGround && this.level.noCollision(new AABB(this.position(), this.position()).inflate(0.06));
   }

   public void tick() {
      if (this.inGroundTime > 4) {
         this.grounded = true;
      }

      if (this.level.isClientSide() && !this.grounded && (this.tickCount > 1 || (Integer)this.entityData.get(ID_BOUNCES) > 0)) {
         this.particleTrail();
      }

      this.prevDeltaMovement = this.getDeltaMovement();
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
         if (this.lastState != blockstate && this.shouldFall()) {
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
               if (hitresult != null && hitresult.getType() != Type.MISS && !flag && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                  this.onHitBlock((BlockHitResult)hitresult);
                  this.hasImpulse = true;
               }

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
            this.setDeltaMovement(vec34.x, vec34.y - 0.05F, vec34.z);
         }

         this.setPos(d7, d2, d3);
         this.checkInsideBlocks();
      }

      if (!this.leftOwner) {
         this.leftOwner = this.checkLeftOwner();
      }

      super.baseTick();
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

   public int getBounceMax() {
      if (this.thrower instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         Iterator var3 = abilities.getAll(JavelinScatterAbility.class, Skill::isUnlocked).iterator();
         if (var3.hasNext()) {
            JavelinScatterAbility ability = (JavelinScatterAbility)var3.next();
            return (byte)ability.getNumberOfBounces();
         }
      }

      return 3;
   }

   public int getNumberOfJavelins() {
      if (this.getThrower() instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         Iterator var3 = abilities.getAll(JavelinScatterAbility.class, Skill::isUnlocked).iterator();
         if (var3.hasNext()) {
            JavelinScatterAbility ability = (JavelinScatterAbility)var3.next();
            return (byte)ability.getNumberOfJavelins();
         }
      }

      return 3;
   }

   public int getSightRadius() {
      if (this.getThrower() instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         Iterator var3 = abilities.getAll(JavelinSightAbility.class, Skill::isUnlocked).iterator();
         if (var3.hasNext()) {
            JavelinSightAbility ability = (JavelinSightAbility)var3.next();
            return Math.round(ability.getRadius(player));
         }
      }

      return 10;
   }

   public int getSightDuration() {
      if (this.getThrower() instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         Iterator var3 = abilities.getAll(JavelinSightAbility.class, Skill::isUnlocked).iterator();
         if (var3.hasNext()) {
            JavelinSightAbility ability = (JavelinSightAbility)var3.next();
            return ability.getEffectDuration(player);
         }
      }

      return 40;
   }

   public float getKnockbackValue() {
      if (this.getThrower() instanceof ServerPlayer player) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         Iterator var3 = abilities.getAll(JavelinAbility.class, Skill::isUnlocked).iterator();
         if (var3.hasNext()) {
            JavelinAbility ability = (JavelinAbility)var3.next();
            return ability.getKnockback();
         }
      }

      return 0.0F;
   }

   public void ricochet(Vec3 normal, int numRicochets, Level world) {
      Vec3 motion = this.prevDeltaMovement;

      for (int i = 0; i < numRicochets; i++) {
         double dot = motion.dot(normal) * 1.5;
         Vec3 reflect = motion.subtract(normal.multiply(new Vec3(dot, dot, dot))).add(0.0, 0.15F, 0.0);
         float randomFactor = 0.15F;
         float angle = (float)i / numRicochets * 360.0F;
         Vec3 direction = new Vec3(Math.cos(Math.toRadians(angle)) / 5.0, 0.15F, Math.sin(Math.toRadians(angle)) / 5.0).normalize();
         float pitch = (float)(randomFactor * (Math.random() - 0.5)) * 2.0F;
         float yaw = (float)(randomFactor * (Math.random() - 0.5)) * 2.0F;
         float roll = (float)(randomFactor * (Math.random() - 0.5)) * 2.0F;
         Vec3 result = direction.scale(0.5).add(reflect).normalize();
         result = result.xRot(pitch).yRot(yaw).zRot(Math.abs(roll));
         VaultThrownJavelin thrownJavelin = this.createBouncingJavelin(world, this.getThrower(), this.bounceCount + 1);
         if (thrownJavelin == null) {
            return;
         }

         thrownJavelin.setPos(
            this.position().x() + result.normalize().x / 5.0,
            this.position().y() + result.normalize().y / 5.0,
            this.position().z() + result.normalize().z / 5.0
         );
         thrownJavelin.setDeltaMovement(result);
         double d0 = result.horizontalDistance();
         thrownJavelin.xRotO = (float)(Mth.atan2(result.y, d0) * 180.0F / (float)Math.PI);
         thrownJavelin.yRotO = (float)(Mth.atan2(result.x, result.z) * 180.0F / (float)Math.PI);
         thrownJavelin.updateRotation();
         thrownJavelin.setType(this.getJavelinType().ordinal());
         thrownJavelin.pickup = Pickup.DISALLOWED;
         thrownJavelin.tickCount = this.tickCount;
         world.addFreshEntity(thrownJavelin);
      }
   }

   private void particleTrail() {
      ParticleEngine pm = Minecraft.getInstance().particleEngine;
      Vec3 offset = new Vec3(
         this.random.nextDouble() / 25.0 * (this.random.nextBoolean() ? 1 : -1), 0.0, this.random.nextDouble() / 25.0 * (this.random.nextBoolean() ? 1 : -1)
      );
      Vec3 direction = this.getDeltaMovement().normalize().scale(0.15F);
      pm.createParticle(
         ParticleTypes.SMOKE, this.position().x + offset.x, this.position().y, this.position().z + offset.z, direction.x, direction.y, direction.z
      );
   }

   protected boolean canHitEntity(Entity entity) {
      return entity instanceof LivingEntity livingEntity && !entity.isInvulnerable()
         ? super.canHitEntity(entity)
            && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId()))
            && !(entity instanceof Player)
            && !(entity instanceof EternalEntity)
         : false;
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
      return !this.grounded && !this.maxPierced ? super.findHitEntity(pStartVec, pEndVec) : null;
   }

   private void resetPiercedEntities() {
      if (this.piercedAndKilledEntities != null) {
         this.piercedAndKilledEntities.clear();
      }

      if (this.piercingIgnoreEntityIds != null) {
         this.piercingIgnoreEntityIds.clear();
      }
   }

   protected void onHitEntity(EntityHitResult pResult) {
      if (!this.maxPierced && !this.grounded) {
         Entity entity = pResult.getEntity();
         Entity entity1 = this.getThrower();
         if (!entity.equals(entity1)) {
            if (entity instanceof LivingEntity livingentity) {
               if (this.getPierceLevel() > 0) {
                  if (this.piercingIgnoreEntityIds == null) {
                     this.piercingIgnoreEntityIds = new IntOpenHashSet(30);
                  }

                  if (this.piercedAndKilledEntities == null) {
                     this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(30);
                  }

                  if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                     this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
                     this.maxPierced = true;
                     return;
                  }

                  if (!this.piercingIgnoreEntityIds.contains(entity.getId())) {
                     this.piercingIgnoreEntityIds.add(entity.getId());
                  }
               } else {
                  this.maxPierced = true;
               }
            }

            SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
            ActiveFlags.IS_JAVELIN_ATTACKING.runIfNotSet(() -> {
               DamageSource damagesource = DamageSource.trident(this, (Entity)(entity1 == null ? this : entity1));
               UUID thrower = this.getThrowerUUID();
               if (thrower != null) {
                  Player player = this.level.getPlayerByUUID(thrower);
                  if (player != null) {
                     damagesource = DamageSource.playerAttack(player);
                  }
               }

               if (entity.hurt(damagesource, this.getDamage())) {
                  if (entity.getType() == EntityType.ENDERMAN) {
                     return;
                  }

                  if (entity instanceof LivingEntity livingentity1) {
                     this.doPostHurtEffects(livingentity1);
                  }
               }
            });
            if (!entity.isAlive()
               && this.piercedAndKilledEntities != null
               && entity instanceof LivingEntity livingentityx
               && !this.piercedAndKilledEntities.contains(livingentityx)) {
               this.piercedAndKilledEntities.add(livingentityx);
            }

            this.playSound(soundevent, 1.0F, 0.75F);
         }
      }
   }

   protected void doPostHurtEffects(LivingEntity pLiving) {
      super.doPostHurtEffects(pLiving);
      pLiving.invulnerableTime = 0;
      if (this.getJavelinType() == VaultThrownJavelin.JavelinType.BASE) {
         EntityHelper.knockbackIgnoreResist(pLiving, this.getThrower(), this.getKnockbackValue());
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
      if (this.bounceCount < this.getBounceMax() && this.thrower != null) {
         BlockPos blockPos = result.getBlockPos();
         BlockState state = this.level.getBlockState(blockPos);
         if (state.getMaterial() != Material.AIR) {
            Vec3 motion = this.prevDeltaMovement;
            if (this.getJavelinType() == VaultThrownJavelin.JavelinType.SIGHT && !this.hasHitBlock && this.getThrower() instanceof ServerPlayer sPlayer) {
               float radius = this.getSightRadius();
               ModNetwork.CHANNEL
                  .send(
                     PacketDistributor.ALL.noArg(),
                     new ClientboundSightParticlesFromJavelinMessage(this.position().x, this.position().y, this.position().z, this.getSightRadius(), 0.0, 0.0)
                  );
               ServerLevel sLevel = sPlayer.getLevel();
               BlockPos offset = this.blockPosition();
               int spacing = 5;

               for (int delay = 0; delay < 60 / spacing; delay++) {
                  float rad = radius * Math.min(1.0F, (delay + delay) / (60.0F / spacing));
                  ServerScheduler.INSTANCE
                     .schedule(
                        delay * spacing,
                        () -> {
                           HunterAbility.selectPositions(sLevel, offset, (double)rad)
                              .forEach(
                                 highlightPosition -> {
                                    if (!"blocks".equals(highlightPosition.type())) {
                                       for (int i = 0; i < 8; i++) {
                                          Vec3 v = MiscUtils.getRandomOffset(highlightPosition.blockPos(), sLevel.getRandom());
                                          ModNetwork.CHANNEL
                                             .sendTo(
                                                new ClientboundHunterParticlesFromJavelinMessage(
                                                   v.x, v.y, v.z, this.getSightDuration(), highlightPosition.type()
                                                ),
                                                sPlayer.connection.getConnection(),
                                                NetworkDirection.PLAY_TO_CLIENT
                                             );
                                       }
                                    }
                                 }
                              );

                           for (LivingEntity nearbyEntity : sLevel.getEntitiesOfClass(
                              LivingEntity.class, AABBHelper.create(this.position(), rad), p_186450_ -> true
                           )) {
                              nearbyEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.getSightDuration(), 0, true, false));
                           }
                        }
                     );
               }
            }

            if (this.getJavelinType() == VaultThrownJavelin.JavelinType.SCATTER) {
               Direction face = result.getDirection();
               Vec3 normal = new Vec3(face.getNormal().getX(), face.getNormal().getY(), face.getNormal().getZ());
               if (this.bounceCount > 0) {
                  double dot = motion.dot(normal) * 1.5;
                  Vec3 reflect = motion.subtract(normal.multiply(new Vec3(dot, dot, dot))).add(0.0, 0.1F, 0.0);
                  VaultThrownJavelin thrownJavelin = this.createBouncingJavelin(this.level, this.thrower, this.bounceCount + 1);
                  if (thrownJavelin == null) {
                     return;
                  }

                  thrownJavelin.setPos(
                     result.getLocation().x() + reflect.normalize().x / 5.0,
                     result.getLocation().y() + reflect.normalize().y / 5.0,
                     result.getLocation().z() + reflect.normalize().z / 5.0
                  );
                  thrownJavelin.setDeltaMovement(reflect);
                  double d0 = reflect.horizontalDistance();
                  thrownJavelin.xRotO = (float)(Mth.atan2(reflect.y, d0) * 180.0F / (float)Math.PI);
                  thrownJavelin.yRotO = (float)(Mth.atan2(reflect.x, reflect.z) * 180.0F / (float)Math.PI);
                  thrownJavelin.updateRotation();
                  thrownJavelin.pickup = Pickup.DISALLOWED;
                  thrownJavelin.setType(this.getJavelinType().ordinal());
                  thrownJavelin.tickCount = this.tickCount;
                  this.level.addFreshEntity(thrownJavelin);
               } else {
                  this.ricochet(normal, this.getNumberOfJavelins(), this.level);
               }

               this.remove(RemovalReason.DISCARDED);
            }
         }

         this.resetPiercedEntities();
         this.hasHitBlock = true;
      }
   }

   protected boolean tryPickup(Player p_150196_) {
      return super.tryPickup(p_150196_) || this.isNoPhysics() && this.ownedBy(p_150196_) && p_150196_.getInventory().add(this.getPickupItem());
   }

   protected ItemStack getPickupItem() {
      return null;
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.TRIDENT_HIT_GROUND;
   }

   public void playerTouch(Player pEntity) {
      if (this.ownedBy(pEntity) || this.getOwner() == null) {
         super.playerTouch(pEntity);
      }
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.grounded = pCompound.getBoolean("Grounded");
      this.hasHitBlock = pCompound.getBoolean("HasHitBlock");
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("Grounded", this.grounded);
      pCompound.putBoolean("HasHitBlock", this.hasHitBlock);
   }

   public void setAge(int age) {
      this.entityData.set(AGE, age);
      this.life = age;
   }

   public void tickDespawn() {
      if (this.grounded) {
         this.setAge(this.life + 1);
         if (this.life >= MAX_AGE) {
            this.discard();
         }
      }
   }

   protected float getWaterInertia() {
      return 0.99F;
   }

   public boolean shouldRender(double pX, double pY, double pZ) {
      return true;
   }

   public static enum JavelinType {
      BASE,
      SCATTER,
      PIERCING,
      SIGHT;

      @Override
      public String toString() {
         return this.name().toLowerCase(Locale.ROOT);
      }

      public static VaultThrownJavelin.JavelinType byId(int id) {
         return MiscUtils.getEnumEntry(VaultThrownJavelin.JavelinType.class, id);
      }
   }
}
