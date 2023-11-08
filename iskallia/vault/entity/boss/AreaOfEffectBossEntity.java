package iskallia.vault.entity.boss;

import com.google.common.collect.Lists;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModParticles;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AreaOfEffectBossEntity extends AreaEffectCloud {
   private static final int reapplicationDelay = 20;

   protected AreaOfEffectBossEntity(Level p_19707_, double p_19708_, double p_19709_, double p_19710_) {
      this(ModEntities.AREA_OF_EFFECT_BOSS, p_19707_);
      this.setPos(p_19708_, p_19709_, p_19710_);
   }

   public AreaOfEffectBossEntity(EntityType<? extends AreaEffectCloud> entityType, Level level) {
      super(entityType, level);
   }

   public void tick() {
      super.baseTick();
      boolean flag = this.isWaiting();
      float f = this.getRadius();
      if (this.level.isClientSide) {
         if (flag && this.random.nextBoolean()) {
            return;
         }

         ParticleOptions particleoptions = this.getParticle();
         int i;
         if (flag) {
            i = 2;
            float f1 = 0.2F;
         } else {
            i = Mth.ceil((float) Math.PI * f * f);
         }

         for (int j = 0; j < i; j++) {
            int k = this.getColor();
            double d5 = (k >> 16 & 0xFF) / 255.0F;
            double d6 = (k >> 8 & 0xFF) / 255.0F;
            double d7 = (k & 0xFF) / 255.0F;
            this.particles(new Vec3(this.getX(), this.getY(), this.getZ()), f, d5, d6, d7);
         }
      } else {
         if (this.tickCount >= this.getWaitTime() + this.getDuration()) {
            this.discard();
            return;
         }

         boolean flag1 = this.tickCount < this.getWaitTime();
         if (flag != flag1) {
            this.setWaiting(flag1);
         }

         if (flag1) {
            return;
         }

         if (this.getRadiusPerTick() != 0.0F) {
            f += this.getRadiusPerTick();
            if (f < 0.5F) {
               this.discard();
               return;
            }

            this.setRadius(f);
         }

         if (this.tickCount % 5 == 0) {
            this.victims.entrySet().removeIf(p_146784_ -> this.tickCount >= (Integer)p_146784_.getValue());
            List<MobEffectInstance> list = Lists.newArrayList();

            for (MobEffectInstance mobeffectinstance : this.getPotion().getEffects()) {
               list.add(
                  new MobEffectInstance(
                     mobeffectinstance.getEffect(),
                     mobeffectinstance.getDuration() / 4,
                     mobeffectinstance.getAmplifier(),
                     mobeffectinstance.isAmbient(),
                     mobeffectinstance.isVisible()
                  )
               );
            }

            list.addAll(this.effects);
            if (list.isEmpty()) {
               this.victims.clear();
            } else {
               List<LivingEntity> list1 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
               if (!list1.isEmpty()) {
                  for (LivingEntity livingentity : list1) {
                     if (!this.victims.containsKey(livingentity) && livingentity.isAffectedByPotions()) {
                        double d8 = livingentity.getX() - this.getX();
                        double d1 = livingentity.getZ() - this.getZ();
                        double d3 = d8 * d8 + d1 * d1;
                        if (d3 <= f * f) {
                           this.victims.put(livingentity, this.tickCount + 20);

                           for (MobEffectInstance mobeffectinstance1 : list) {
                              if (mobeffectinstance1.getEffect().isInstantenous()) {
                                 mobeffectinstance1.getEffect()
                                    .applyInstantenousEffect(this, this.getOwner(), livingentity, mobeffectinstance1.getAmplifier(), 0.5);
                              } else {
                                 livingentity.addEffect(new MobEffectInstance(mobeffectinstance1), this);
                              }
                           }

                           if (this.getRadiusOnUse() != 0.0F) {
                              f += this.getRadiusOnUse();
                              if (f < 0.5F) {
                                 this.discard();
                                 return;
                              }

                              this.setRadius(f);
                           }

                           if (this.getDurationOnUse() != 0) {
                              this.duration = this.duration + this.getDurationOnUse();
                              if (this.getDuration() <= 0) {
                                 this.discard();
                                 return;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void particles(Vec3 pos, float radius, double r, double g, double b) {
      Random random = new Random();
      ParticleEngine pm = Minecraft.getInstance().particleEngine;

      for (int i = 0; i < 1; i++) {
         float rotation = random.nextFloat() * 360.0F;
         Vec3 offset = new Vec3(radius * Math.cos(rotation), 0.25, radius * Math.sin(rotation));
         float f = -0.5F + random.nextFloat() + (float)offset.x();
         float f1 = -0.5F + random.nextFloat() + (float)offset.y();
         float f2 = -0.5F + random.nextFloat() + (float)offset.z();
         Particle particle = pm.createParticle((ParticleOptions)ModParticles.LUCKY_HIT_VORTEX.get(), pos.x, pos.y + 0.15, pos.z, f, f1, f2);
         if (particle != null) {
            float colorOffset = random.nextFloat() * 0.2F;
            particle.setColor((float)Math.max(0.0, r - colorOffset), (float)Math.max(0.0, g - colorOffset), (float)Math.max(0.0, b - colorOffset));
            particle.setLifetime(40);
         }
      }
   }
}
