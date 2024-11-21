package iskallia.vault.entity.entity;

import iskallia.vault.config.VaultEntitiesConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.util.EntityHelper;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SwampZombieEntity extends Zombie {
   public long prevPuffTick = 0L;

   public SwampZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.OVERGROWN_ZOMBIE_IDLE;
   }

   public int getAmbientSoundInterval() {
      return 240;
   }

   @Nonnull
   protected SoundEvent getStepSound() {
      return ModSounds.OVERGROWN_ZOMBIE_STEP;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return ModSounds.OVERGROWN_ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.OVERGROWN_ZOMBIE_DEATH;
   }

   public void tick() {
      super.tick();
      VaultEntitiesConfig.AuraEffect config = ModConfigs.VAULT_ENTITIES.getSwampZombieEffect();
      List<Player> nearbyPlayers = EntityHelper.getNearby(this.level, this.blockPosition(), config.getRange(), Player.class);
      nearbyPlayers.forEach(player -> {
         if (!this.level.isClientSide()) {
            player.addEffect(new MobEffectInstance(ModEffects.POISON_OVERRIDE, config.getEffectDuration(), config.getEffectAmplifier(), true, true));
         }
      });
   }
}
