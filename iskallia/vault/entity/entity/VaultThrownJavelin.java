package iskallia.vault.entity.entity;

import com.google.common.collect.Lists;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.base.HunterHiddenTileEntity;
import iskallia.vault.client.particles.SphericalParticleOptions;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.network.message.ClientboundHunterParticlesFromJavelinMessage;
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
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleType;
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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.network.NetworkDirection;

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
   private boolean grounded;
   private boolean maxPierced = false;
   private int life;
   public Vec3 prevDeltaMovement = new Vec3(0.0, 0.0, 0.0);
   @Nullable
   private IntOpenHashSet piercingIgnoreEntityIds;
   @Nullable
   private List<Entity> piercedAndKilledEntities;
   private LivingEntity thrower = null;
   private int bounceCount = 0;

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
      this.entityData.set(ID_TYPE, id);
   }

   public void setType(String type) {
      this.entityData.set(ID_TYPE, VaultThrownJavelin.JavelinType.byName(type).ordinal());
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
      VaultThrownJavelin javelin = new VaultThrownJavelin(level, thrower);
      javelin.bounceCount = bounceCount;
      javelin.entityData.set(ID_BOUNCES, bounceCount);
      javelin.entityData.set(IS_GHOST, (Boolean)this.entityData.get(IS_GHOST));
      return javelin;
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

   public void tick() {
      if (this.inGroundTime > 4) {
         this.grounded = true;
      }

      if (this.level.isClientSide() && !this.grounded && (this.tickCount > 1 || (Integer)this.entityData.get(ID_BOUNCES) > 0)) {
         this.particleTrail();
      }

      this.prevDeltaMovement = this.getDeltaMovement();
      super.tick();
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
            return (byte)ability.getRadius();
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
            return (byte)ability.getEffectDuration();
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

   public boolean shouldHighlightTileEntity(BlockEntity tile) {
      if (tile.getType().getRegistryName() == null) {
         return false;
      } else {
         String var2 = tile.getType().getRegistryName().toString();
         switch (var2) {
            case "minecraft:chest":
            case "minecraft:trapped_chest":
            case "the_vault:coin_pile":
            case "the_vault:vault_chest_tile_entity":
               return true;
            default:
               return false;
         }
      }
   }

   protected List<HunterAbility.HighlightPosition> selectPositions(ServerLevel world, ServerPlayer player, double radius) {
      List<HunterAbility.HighlightPosition> result = new ArrayList<>();
      Color c = Color.cyan;
      this.forEachTileEntity(world, player, radius, (pos, tile) -> {
         if (this.shouldHighlightTileEntity(tile)) {
            if (tile instanceof HunterHiddenTileEntity hiddenTile && hiddenTile.isHidden()) {
               return;
            }

            result.add(new HunterAbility.HighlightPosition(pos, c));
         }
      });
      return result;
   }

   protected void forEachTileEntity(Level world, Player player, double radius, BiConsumer<BlockPos, BlockEntity> consumer) {
      BlockPos playerOffset = player.blockPosition();
      double radiusSq = radius * radius;
      int iRadius = Mth.ceil(radius);
      Vec3i radVec = new Vec3i(iRadius, iRadius, iRadius);
      ChunkPos posMin = new ChunkPos(this.blockPosition().subtract(radVec));
      ChunkPos posMax = new ChunkPos(this.blockPosition().offset(radVec));

      for (int xx = posMin.x; xx <= posMax.x; xx++) {
         for (int zz = posMin.z; zz <= posMax.z; zz++) {
            LevelChunk ch = world.getChunkSource().getChunkNow(xx, zz);
            if (ch != null) {
               ch.getBlockEntities().forEach((pos, tile) -> {
                  if (tile != null && pos.distSqr(this.blockPosition()) <= radiusSq) {
                     consumer.accept(pos, tile);
                  }
               });
            }
         }
      }
   }

   protected void onHit(HitResult result) {
      if (!this.level.isClientSide() && this.bounceCount < this.getBounceMax() && this.thrower != null && result.getType() == Type.BLOCK) {
         BlockPos blockPos = ((BlockHitResult)result).getBlockPos();
         BlockState state = this.level.getBlockState(blockPos);
         if (state.getMaterial() != Material.AIR) {
            Vec3 motion = this.prevDeltaMovement;
            if (this.getJavelinType() == VaultThrownJavelin.JavelinType.SIGHT) {
               ServerPlayer player = (ServerPlayer)this.getThrower();
               float radius = this.getSightRadius();
               player.getLevel()
                  .sendParticles(
                     new SphericalParticleOptions(
                        (ParticleType<SphericalParticleOptions>)ModParticles.SIGHT_JAVELIN_RANGE.get(), radius, new Vector3f(0.0F, 1.0F, 1.0F)
                     ),
                     this.position().x,
                     this.position().y,
                     this.position().z,
                     500,
                     0.0,
                     0.0,
                     0.0,
                     0.0
                  );
               int spacing = 5;

               for (int delay = 0; delay < 60 / spacing; delay++) {
                  float rad = radius * Math.min(1.0F, (delay + delay) / (60.0F / spacing));
                  ServerScheduler.INSTANCE
                     .schedule(
                        delay * spacing,
                        () -> {
                           this.selectPositions((ServerLevel)this.level, player, rad)
                              .forEach(
                                 highlightPosition -> {
                                    for (int i = 0; i < 8; i++) {
                                       Vec3 v = MiscUtils.getRandomOffset(highlightPosition.blockPos(), this.level.getRandom());
                                       ModNetwork.CHANNEL
                                          .sendTo(
                                             new ClientboundHunterParticlesFromJavelinMessage(v.x, v.y, v.z, this.getSightDuration(), 0.0, 0.0),
                                             player.connection.getConnection(),
                                             NetworkDirection.PLAY_TO_CLIENT
                                          );
                                    }
                                 }
                              );

                           for (LivingEntity nearbyEntity : player.level
                              .getEntitiesOfClass(LivingEntity.class, AABBHelper.create(this.position(), rad), p_186450_ -> true)) {
                              nearbyEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.getSightDuration(), 0, true, false));
                           }
                        }
                     );
               }
            }

            if (this.getJavelinType() == VaultThrownJavelin.JavelinType.SCATTER) {
               Direction face = ((BlockHitResult)result).getDirection();
               Vec3 normal = new Vec3(face.getNormal().getX(), face.getNormal().getY(), face.getNormal().getZ());
               if (this.bounceCount > 0) {
                  double dot = motion.dot(normal) * 1.5;
                  Vec3 reflect = motion.subtract(normal.multiply(new Vec3(dot, dot, dot))).add(0.0, 0.1F, 0.0);
                  VaultThrownJavelin thrownJavelin = this.createBouncingJavelin(this.level, this.thrower, this.bounceCount + 1);
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
      }

      super.onHit(result);
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
      Particle particle = pm.createParticle(
         ParticleTypes.SMOKE, this.position().x + offset.x, this.position().y, this.position().z + offset.z, direction.x, direction.y, direction.z
      );
   }

   protected boolean canHitEntity(Entity entity) {
      return super.canHitEntity(entity)
         && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId()))
         && !(entity instanceof Player)
         && !(entity instanceof EternalEntity);
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
                     this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
                  }

                  if (this.piercedAndKilledEntities == null) {
                     this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
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

            if (this.getJavelinType() == VaultThrownJavelin.JavelinType.SCATTER) {
               Vec3 motion = this.prevDeltaMovement;
               Direction face = this.getMotionDirection().getOpposite();
               Vec3 normal = new Vec3(motion.normalize().x(), motion.normalize().y(), motion.normalize().z());
               if (this.bounceCount > 0) {
                  double dot = motion.dot(normal) * 1.5;
                  Vec3 reflect = motion.subtract(normal.multiply(new Vec3(dot, dot, dot))).add(0.0, 0.1F, 0.0);
                  VaultThrownJavelin thrownJavelin = this.createBouncingJavelin(this.level, this.getThrower(), this.bounceCount + 1);
                  thrownJavelin.setPos(
                     pResult.getLocation().x() + reflect.normalize().x / 5.0,
                     pResult.getLocation().y() + reflect.normalize().y / 5.0,
                     pResult.getLocation().z() + reflect.normalize().z / 5.0
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

   protected void onHitBlock(BlockHitResult p_36755_) {
      super.onHitBlock(p_36755_);
      this.resetPiercedEntities();
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
      BASE("base"),
      SCATTER("scatter"),
      PIERCING("piercing"),
      SIGHT("sight");

      private final String name;

      private JavelinType(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static VaultThrownJavelin.JavelinType byId(int pId) {
         VaultThrownJavelin.JavelinType[] javelinType = values();
         if (pId < 0 || pId >= javelinType.length) {
            pId = 0;
         }

         return javelinType[pId];
      }

      public static VaultThrownJavelin.JavelinType byName(String pName) {
         VaultThrownJavelin.JavelinType[] javelinType = values();

         for (int i = 0; i < javelinType.length; i++) {
            if (javelinType[i].getName().equals(pName)) {
               return javelinType[i];
            }
         }

         return javelinType[0];
      }
   }
}
