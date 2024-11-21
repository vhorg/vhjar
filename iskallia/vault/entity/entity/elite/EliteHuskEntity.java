package iskallia.vault.entity.entity.elite;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModEntities;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EliteHuskEntity extends Husk {
   protected int scarabSpawnCooldownTicks = 60;

   public EliteHuskEntity(EntityType<? extends Husk> entityType, Level world) {
      super(entityType, world);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         BlockState blockState = new BlockState(Blocks.SAND, ImmutableMap.of(), MapCodec.unit(this.getBlockStateOn()));
         this.level
            .addParticle(
               new BlockParticleOption(ParticleTypes.FALLING_DUST, blockState), this.getRandomX(0.9), this.getY() + 1.9F, this.getRandomZ(0.9), 0.0, 0.01, 0.0
            );
      }
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.scarabSpawnCooldownTicks = Math.max(this.scarabSpawnCooldownTicks - 1, 0);
   }

   public boolean canBeAffected(MobEffectInstance potionEffect) {
      return potionEffect.getEffect() == ModEffects.GLACIAL_SHATTER ? false : super.canBeAffected(potionEffect);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      boolean hurt = super.hurt(pSource, pAmount);
      if (this.level.isClientSide) {
         return hurt;
      } else {
         if (hurt && this.scarabSpawnCooldownTicks == 0) {
            this.scarabSpawnCooldownTicks = this.random.nextInt(60, 100);
            int count = this.random.nextInt(2, 4);

            for (int i = 0; i < count; i++) {
               ScarabEntity scarab = (ScarabEntity)ModEntities.SCARAB.create(this.level);
               if (scarab != null) {
                  if (this.isPersistenceRequired()) {
                     scarab.setPersistenceRequired();
                  }

                  Vec3 offset = new Vec3(1.0, 0.0, 0.0).yRot((float)(i * (Math.PI * 2) / count));
                  scarab.setDeltaMovement(offset.add(0.0, 1.5, 0.0));
                  scarab.moveTo(this.position());
                  this.level.addFreshEntity(scarab);
                  scarab.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 10.0F, 0.95F + scarab.getRandom().nextFloat() * 0.1F);
               }
            }
         }

         return hurt;
      }
   }

   public void die(DamageSource pCause) {
      super.die(pCause);
      this.level
         .getEntitiesOfClass(ScarabEntity.class, this.getBoundingBox().inflate(128.0))
         .forEach(e -> e.hurt(DamageSource.mobAttack(this).bypassArmor().bypassInvul().bypassMagic(), e.getHealth()));
   }
}
