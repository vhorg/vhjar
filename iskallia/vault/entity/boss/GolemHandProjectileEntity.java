package iskallia.vault.entity.boss;

import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.StonefallParticleMessage;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
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

public class GolemHandProjectileEntity extends AbstractHurtingProjectile {
   private float damage;
   @Nullable
   private LivingEntity target;
   private boolean rightHand;
   private boolean hasBeenShot;

   public GolemHandProjectileEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public GolemHandProjectileEntity(Level level, VaultBossBaseEntity boss, double x, double y, double z, LivingEntity target, float damage, boolean rightHand) {
      this(ModEntities.GOLEM_HAND_PROJECTILE, level, boss, x, y, z, target, damage, rightHand);
   }

   protected GolemHandProjectileEntity(
      EntityType<? extends AbstractHurtingProjectile> entityType,
      Level level,
      VaultBossBaseEntity boss,
      double x,
      double y,
      double z,
      LivingEntity target,
      float damage,
      boolean rightHand
   ) {
      super(entityType, boss, x, y, z, level);
      this.target = target;
      this.damage = damage;
      this.rightHand = rightHand;
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
      Vec3 center = this.getBoundingBox().getCenter();
      pm.createParticle(
         ParticleTypes.LARGE_SMOKE,
         center.x + offset.x + direction.x * this.random.nextFloat() * 0.2F,
         center.y + offset.y + direction.y * this.random.nextFloat() * 0.2F,
         center.z + offset.z + direction.z * this.random.nextFloat() * 0.2F,
         direction.x,
         direction.y,
         direction.z
      );
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

      if (this.level.isClientSide && this.getLevel().getGameTime() % 2L == 0L) {
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
         if (this.getOwner() instanceof GolemBossEntity golem) {
            if (this.isRightHand()) {
               golem.setShowRightHand(true);
            } else {
               golem.setShowLeftHand(true);
            }
         }

         this.discard();
      }
   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource source, float amount) {
      return false;
   }

   public boolean isRightHand() {
      return this.rightHand;
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putFloat("Damage", this.damage);
      if (this.target != null) {
         tag.putUUID("Target", this.target.getUUID());
      }

      tag.putBoolean("RightHand", this.rightHand);
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.damage = tag.getFloat("Damage");
      this.target = tag.contains("Target") ? this.level.getPlayerByUUID(tag.getUUID("Target")) : null;
      this.rightHand = tag.getBoolean("RightHand");
   }
}
