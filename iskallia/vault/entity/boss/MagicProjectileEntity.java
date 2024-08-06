package iskallia.vault.entity.boss;

import iskallia.vault.client.particles.FireballParticle;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.StonefallParticleMessage;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PacketDistributor;

public class MagicProjectileEntity extends AbstractHurtingProjectile {
   private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(MagicProjectileEntity.class, EntityDataSerializers.INT);
   private float damage;
   @Nullable
   private Player target;
   private boolean hasBeenShot;

   public MagicProjectileEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
      super(entityType, level);
   }

   @Nullable
   public Player getTarget() {
      return this.target;
   }

   public MagicProjectileEntity(Level level, ArtifactBossEntity artifactBossEntity, double x, double y, double z, Player target, float damage) {
      this(ModEntities.MAGIC_PROJECTILE, level, artifactBossEntity, x, y, z, target, damage);
   }

   protected MagicProjectileEntity(
      EntityType<? extends AbstractHurtingProjectile> entityType,
      Level level,
      ArtifactBossEntity artifactBossEntity,
      double x,
      double y,
      double z,
      Player target,
      float damage
   ) {
      super(entityType, artifactBossEntity, x, y, z, level);
      this.target = target;
      this.damage = damage;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(COLOR, 16777215);
   }

   protected ParticleOptions getTrailParticle() {
      return ParticleTypes.SMOKE;
   }

   private void particleTrail() {
      ParticleEngine pm = Minecraft.getInstance().particleEngine;
      Vec3 offset = new Vec3(
         this.random.nextDouble() / 8.0 * (this.random.nextBoolean() ? 1 : -1),
         this.random.nextDouble() / 8.0 * (this.random.nextBoolean() ? 1 : -1),
         this.random.nextDouble() / 8.0 * (this.random.nextBoolean() ? 1 : -1)
      );
      Vec3 direction = this.getDeltaMovement().normalize().scale(0.05F);
      if (pm.createParticle(
         (ParticleOptions)ModParticles.FIREBALL_CLOUD.get(),
         this.position().x + offset.x + direction.x * this.random.nextFloat() * 0.2F,
         this.position().y + offset.y + direction.y * this.random.nextFloat() * 0.2F,
         this.position().z + offset.z + direction.z * this.random.nextFloat() * 0.2F,
         direction.x,
         direction.y,
         direction.z
      ) instanceof FireballParticle fireballParticle) {
         float colorOffset = this.random.nextFloat() * 0.2F;
         int col = this.getColor();
         float r = (col >> 16 & 0xFF) / 255.0F;
         float g = (col >> 8 & 0xFF) / 255.0F;
         float b = (col & 0xFF) / 255.0F;
         fireballParticle.setStartColor(r - 0.2F + colorOffset, g - 0.2F + colorOffset, b);
         float col2 = Mth.nextFloat(this.random, 0.01F, 0.15F);
         fireballParticle.setEndColor(col2, col2, col2);
      }
   }

   public void setColor(int color) {
      this.entityData.set(COLOR, color);
   }

   public int getColor() {
      return (Integer)this.entityData.get(COLOR);
   }

   protected boolean shouldBurn() {
      return false;
   }

   protected void onHitEntity(EntityHitResult hitResult) {
      super.onHitEntity(hitResult);
      if (!this.level.isClientSide) {
         hitResult.getEntity().hurt(DamageSource.MAGIC, this.damage);
      }
   }

   protected void updateRotation() {
      Vec3 vec3 = this.getDeltaMovement();
      double d0 = vec3.horizontalDistance();
      this.setXRot((float)(Mth.atan2(vec3.y, d0) * 180.0F / (float)Math.PI));
      this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
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

   public void tick() {
      Entity entity = this.getOwner();
      if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
         super.baseTick();
         if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner(), this.blockPosition());
            this.hasBeenShot = true;
         }

         if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
         }

         if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
         }

         HitResult hitresult = ProjectileUtil.getHitResult(this, x$0 -> this.canHitEntity(x$0));
         if (hitresult.getType() != Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
         }

         this.checkInsideBlocks();
         Vec3 vec3 = this.getDeltaMovement();
         double d0 = this.getX() + vec3.x;
         double d1 = this.getY() + vec3.y;
         double d2 = this.getZ() + vec3.z;
         ProjectileUtil.rotateTowardsMovement(this, 0.2F);
         float f = this.getInertia();
         if (this.isInWater()) {
            for (int i = 0; i < 4; i++) {
               float f1 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25, d1 - vec3.y * 0.25, d2 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
            }

            f = 0.8F;
         }

         this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale(f));
         this.setPos(d0, d1, d2);
      } else {
         this.discard();
      }

      this.updateRotation();
      if (this.level.isClientSide) {
         this.particleTrail();
      }

      if (this.target == null && !this.level.isClientSide()) {
         this.remove(RemovalReason.DISCARDED);
      }
   }

   protected void onHit(HitResult result) {
      super.onHit(result);
      if (!this.level.isClientSide) {
         ModNetwork.CHANNEL
            .send(PacketDistributor.ALL.noArg(), new StonefallParticleMessage(new Vec3(this.position().x(), this.position().y(), this.position().z()), 2.0F));
         this.level
            .playSound(
               null,
               this.position().x(),
               this.position().y(),
               this.position().z(),
               ModSounds.ARTIFACT_BOSS_MAGIC_ATTACK_HIT,
               SoundSource.BLOCKS,
               1.0F,
               0.75F + new Random().nextFloat() * 0.35F
            );
         this.discard();
      }
   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      return false;
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putFloat("Damage", this.damage);
      tag.putUUID("Target", this.target.getUUID());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.damage = tag.getFloat("Damage");
      this.target = this.level.getPlayerByUUID(tag.getUUID("Target"));
   }
}
