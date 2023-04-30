package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.entity.eyesore.EyesoreEntity;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class LaserAttackTask extends EyesoreTask<EyesoreEntity> {
   public int tick = 0;
   public UUID target;

   public LaserAttackTask(EyesoreEntity entity) {
      super(entity);
   }

   public void tick() {
      if (!this.isFinished()) {
         if (this.target == null) {
            List<ServerPlayer> players = new ArrayList<>();
            if (players.size() == 0) {
               return;
            }

            ServerPlayer player = players.get(this.getRandom().nextInt(players.size()));
            this.target = player.getUUID();
            this.getEntity().getEntityData().set(EyesoreEntity.LASER_TARGET, player.getId());
         }

         Entity entity = this.getWorld().getEntity(this.target);
         LivingEntity targetEntity = entity instanceof LivingEntity ? (LivingEntity)entity : null;
         if (targetEntity != null) {
            double distance = this.getEntity().blockPosition().distSqr(targetEntity.blockPosition());
            this.getEntity().path.stayInRange(this.getEntity(), targetEntity, 0.15, 30.0, 2.0);
            this.lookAtTarget(targetEntity);
            Vec3 eyePos1 = this.getEntity().getEyePosition(1.0F);
            Vec3 eyePos2 = this.getPosition(targetEntity);
            ClipContext context = new ClipContext(eyePos1, eyePos2, Block.COLLIDER, Fluid.NONE, this.getEntity());
            BlockHitResult result = this.getWorld().clip(context);
            if (result.getType() == Type.MISS) {
               targetEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 2, false, false));
               DamageSource source = new EntityDamageSource("laser", this.getEntity()).setMagic();
               float damage = ModConfigs.EYESORE.laserAttack.getDamage(this.getEntity(), this.tick);
               if (damage > 0.0F) {
                  targetEntity.hurt(source, damage);
               }

               if (this.getWorld().getGameTime() % 10L == 0L) {
                  this.getWorld().sendParticles(ParticleTypes.SMOKE, targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), 300, 0.0, 0.0, 0.0, 0.001);
               }
            } else {
               if (this.getWorld().getGameTime() % 6L == 0L) {
                  this.getWorld().destroyBlock(result.getBlockPos(), true, this.getEntity());
               }

               if (this.getWorld().getGameTime() % 10L == 0L) {
                  this.getWorld()
                     .sendParticles(ParticleTypes.SMOKE, result.getLocation().x, result.getLocation().y, result.getLocation().z, 300, 0.0, 0.0, 0.0, 0.001);
               }
            }
         }

         this.tick++;
         if (this.isFinished()) {
            this.getEntity().getEntityData().set(EyesoreEntity.LASER_TARGET, -1);
         }
      }
   }

   protected void lookAtTarget(LivingEntity target) {
      this.getEntity().setXRot(this.getTargetPitch(target));
      this.getEntity().yHeadRot = this.getTargetYaw(target);
   }

   private double getEyePosition(Entity entity) {
      return entity instanceof LivingEntity ? entity.getEyeY() : (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
   }

   protected float getTargetPitch(LivingEntity target) {
      double d0 = target.getX() - this.getEntity().getX();
      double d1 = this.getEyePosition(target) - this.getEntity().getEyeY();
      double d2 = target.getZ() - this.getEntity().getZ();
      double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
      return (float)(-(Mth.atan2(d1, d3) * 180.0F / (float)Math.PI));
   }

   protected float getTargetYaw(LivingEntity target) {
      double d0 = target.getX() - this.getEntity().getX();
      double d1 = target.getZ() - this.getEntity().getZ();
      return (float)(Mth.atan2(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
   }

   private Vec3 getPosition(Entity entityLivingBaseIn) {
      double d0 = entityLivingBaseIn.getX();
      double d1 = entityLivingBaseIn.getY() + entityLivingBaseIn.getBbHeight() / 2.0F;
      double d2 = entityLivingBaseIn.getZ();
      return new Vec3(d0, d1, d2);
   }

   public boolean isFinished() {
      return this.tick >= 100;
   }

   public void reset() {
      this.tick = 0;
      this.target = null;
   }
}
