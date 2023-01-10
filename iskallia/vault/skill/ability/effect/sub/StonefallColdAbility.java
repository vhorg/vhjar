package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.sub.StonefallColdConfig;
import iskallia.vault.skill.ability.effect.AbstractStonefallAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StonefallColdAbility extends AbstractStonefallAbility<StonefallColdConfig> {
   protected boolean canDoAction(StonefallColdConfig config, ServerPlayer player, boolean active) {
      return !player.hasEffect(ModEffects.STONEFALL_COLD) && super.canDoAction(config, player, active);
   }

   protected AbilityActionResult doAction(StonefallColdConfig config, ServerPlayer player, boolean active) {
      player.clearFire();
      player.addEffect(new MobEffectInstance(ModEffects.STONEFALL_COLD, config.getDurationTicks(), 0, false, false, true));
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doSound(StonefallColdConfig config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.NOVA_SPEED, SoundSource.PLAYERS, 0.2F, 1.0F);
      player.playNotifySound(ModSounds.NOVA_SPEED, SoundSource.PLAYERS, 0.2F, 1.0F);
   }

   @SubscribeEvent
   public void on(LivingAttackEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         MobEffectInstance effectInstance = serverPlayer.getEffect(ModEffects.STONEFALL_COLD);
         if (effectInstance != null) {
            DamageSource source = event.getSource();
            if (source.isFire()) {
               event.setCanceled(true);
            }
         }
      }
   }

   public static class StonefallColdEffect extends MobEffect {
      public StonefallColdEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(resourceLocation);
      }

      public boolean isDurationEffectTick(int duration, int amplifier) {
         return true;
      }

      public void applyEffectTick(@Nonnull LivingEntity livingEntity, int amplifier) {
         livingEntity.clearFire();
      }
   }
}
