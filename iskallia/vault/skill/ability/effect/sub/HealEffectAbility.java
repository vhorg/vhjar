package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.sub.HealEffectConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractHealAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;

public class HealEffectAbility extends AbstractHealAbility<HealEffectConfig> {
   private final HealEffectAbility.IRemovalStrategy removalStrategy;

   public HealEffectAbility(HealEffectAbility.IRemovalStrategy removalStrategy) {
      this.removalStrategy = removalStrategy;
   }

   protected AbilityActionResult doAction(HealEffectConfig config, ServerPlayer player, boolean active) {
      this.removalStrategy.apply(player, config);
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doParticles(HealEffectConfig config, ServerPlayer player) {
      ((ServerLevel)player.level).sendParticles(ParticleTypes.BUBBLE_POP, player.getX(), player.getY(), player.getZ(), 50, 1.0, 0.5, 1.0, 0.0);
      ((ServerLevel)player.level).sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY(), player.getZ(), 50, 1.0, 0.5, 1.0, 0.0);
   }

   protected void doSound(HealEffectConfig config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.CLEANSE_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
      player.playNotifySound(ModSounds.CLEANSE_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
   }

   @FunctionalInterface
   private interface IRemovalStrategy {
      void apply(ServerPlayer var1, HealEffectConfig var2);
   }

   public static enum RemovalStrategy implements HealEffectAbility.IRemovalStrategy {
      DEFINED_ONLY((player, config) -> config.getRemoveEffects().forEach(player::removeEffect)),
      ALL_HARMFUL(
         (player, config) -> player.getActiveEffects()
            .stream()
            .filter(instance -> instance.getEffect().getCategory() == MobEffectCategory.HARMFUL)
            .forEach(instance -> player.removeEffect(instance.getEffect()))
      );

      private final HealEffectAbility.IRemovalStrategy removalStrategy;

      private RemovalStrategy(HealEffectAbility.IRemovalStrategy removalStrategy) {
         this.removalStrategy = removalStrategy;
      }

      @Override
      public void apply(ServerPlayer player, HealEffectConfig config) {
         this.removalStrategy.apply(player, config);
      }
   }
}
