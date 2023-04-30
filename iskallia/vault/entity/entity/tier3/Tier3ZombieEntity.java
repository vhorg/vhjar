package iskallia.vault.entity.entity.tier3;

import iskallia.vault.init.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class Tier3ZombieEntity extends Zombie {
   public Tier3ZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide()) {
         if (this.tickCount % 40 == 0) {
            if (this.getHealth() != this.getMaxHealth()) {
               if (this.level.random.nextFloat() <= 0.25) {
                  this.level.playSound(null, this.blockPosition(), ModSounds.BURP, this.getSoundSource(), 1.0F, 1.0F);
                  float healAmount = this.getMaxHealth() * 0.3F;
                  this.heal(healAmount);
                  ((ServerLevel)this.level)
                     .sendParticles(ParticleTypes.SNEEZE, this.getEyePosition().x, this.getEyeY() + 0.2, this.getEyePosition().z, 25, 0.2, 0.1, 0.2, 0.001);
               }
            }
         }
      }
   }
}
