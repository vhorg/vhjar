package iskallia.vault.entity.boss;

import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModParticles;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class BloodOrbEntity extends Entity {
   public BloodOrbEntity(Level world) {
      this(ModEntities.BLOOD_ORB, world);
   }

   public BloodOrbEntity(EntityType<BloodOrbEntity> type, Level level) {
      super(type, level);
   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (!this.level.isClientSide) {
         this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
         ((ServerLevel)this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
         this.discard();
      }

      return true;
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         this.particles(this.position(), 0.75F, 0.8F, 0.05F, 0.05F);
      }
   }

   private void particles(Vec3 pos, float radius, double r, double g, double b) {
      Random random = new Random();
      ParticleEngine pm = Minecraft.getInstance().particleEngine;

      for (int i = 0; i < 5; i++) {
         float rotation = random.nextFloat() * 360.0F;
         Vec3 offset = new Vec3(radius * Math.cos(rotation), 0.25, radius * Math.sin(rotation));
         float f = (-0.5F + random.nextFloat()) / 8.0F + (float)offset.x();
         float f1 = (-0.5F + random.nextFloat()) / 8.0F + (float)offset.y();
         float f2 = (-0.5F + random.nextFloat()) / 8.0F + (float)offset.z();
         Particle particle = pm.createParticle((ParticleOptions)ModParticles.LUCKY_HIT_VORTEX.get(), pos.x, pos.y + 0.25, pos.z, f, f1, f2);
         if (particle != null) {
            float colorOffset = random.nextFloat() * 0.2F;
            particle.setColor((float)Math.max(0.0, r - colorOffset), (float)Math.max(0.0, g - colorOffset), (float)Math.max(0.0, b - colorOffset));
            particle.setLifetime(20);
         }
      }

      for (int ix = 0; ix < 3; ix++) {
         float rotation = random.nextFloat() * 360.0F;
         Vec3 offset = new Vec3((radius + 0.5F) * Math.cos(rotation), 0.25, (radius + 0.25F) * Math.sin(rotation));
         float f = (-0.5F + random.nextFloat()) / 8.0F + (float)offset.x();
         float f1 = (-0.5F + random.nextFloat()) / 8.0F + (float)offset.y();
         float f2 = (-0.5F + random.nextFloat()) / 8.0F + (float)offset.z();
         Particle particle = pm.createParticle((ParticleOptions)ModParticles.LUCKY_HIT_VORTEX.get(), pos.x, pos.y + 0.25, pos.z, f, f1, f2);
         if (particle != null) {
            float colorOffset = random.nextFloat() * 0.3F;
            particle.setColor(
               (float)Math.max(0.0, r - colorOffset - 0.4), (float)Math.max(0.0, g - colorOffset - 0.4), (float)Math.max(0.0, b - colorOffset - 0.4)
            );
            particle.setLifetime(20);
         }
      }
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundTag nbt) {
   }

   protected void addAdditionalSaveData(CompoundTag nbt) {
   }

   public Packet<?> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
