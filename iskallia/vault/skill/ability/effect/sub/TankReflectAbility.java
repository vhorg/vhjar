package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.TankReflectConfig;
import iskallia.vault.skill.ability.effect.TankAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractTankAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbilityTickResult;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TankReflectAbility extends AbstractTankAbility<TankReflectConfig> {
   public TankReflectAbility() {
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.THORNS_CHANCE)
         .filter(data -> this.getConfig(data.getEntity()).isPresent())
         .register(this, data -> this.getConfig(data.getEntity()).ifPresent(config -> data.setValue(data.getValue() + config.getAdditionalThornsChance())));
      CommonEvents.PLAYER_STAT
         .of(PlayerStat.THORNS_DAMAGE)
         .filter(data -> this.getConfig(data.getEntity()).isPresent())
         .register(this, data -> this.getConfig(data.getEntity()).ifPresent(config -> data.setValue(data.getValue() + config.getThornsDamageMultiplier())));
   }

   private Optional<TankReflectConfig> getConfig(LivingEntity livingEntity) {
      if (livingEntity instanceof ServerPlayer player && player.hasEffect(ModEffects.TANK_REFLECT)) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         AbilityNode<?, ?> node = abilities.getNodeByName("Tank");
         return node.getAbility() == this && node.isLearned() && node.getAbilityConfig() instanceof TankReflectConfig config
            ? Optional.of(config)
            : Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   protected AbilityActionResult doToggle(TankReflectConfig config, ServerPlayer player, boolean active) {
      if (active) {
         ModEffects.TANK_REFLECT.addTo(player, 0);
         return AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED;
      } else {
         player.removeEffect(ModEffects.TANK_REFLECT);
         return AbilityActionResult.SUCCESS_COOLDOWN;
      }
   }

   protected void doToggleSound(TankReflectConfig config, ServerPlayer player, boolean active) {
      if (active) {
         player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TANK_REFLECT, SoundSource.MASTER, 0.7F, 1.0F);
         player.playNotifySound(ModSounds.TANK_REFLECT, SoundSource.MASTER, 0.7F, 1.0F);
      }
   }

   protected AbilityTickResult doInactiveTick(TankReflectConfig config, ServerPlayer player) {
      if (player.hasEffect(ModEffects.TANK_REFLECT)) {
         player.removeEffect(ModEffects.TANK_REFLECT);
      }

      return AbilityTickResult.PASS;
   }

   protected void doManaDepleted(TankReflectConfig config, ServerPlayer player) {
      player.removeEffect(ModEffects.TANK_REFLECT);
   }

   @SubscribeEvent
   public void on(LivingDamageEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayer player && player.hasEffect(ModEffects.TANK_REFLECT)) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         AbilityNode<?, ?> node = abilities.getNodeByName("Tank");
         if (node.getAbility() == this && node.isLearned()) {
            if (event.getSource().getEntity() instanceof LivingEntity) {
               float pitch = 1.0F + RANDOM.nextFloat() * 0.25F;
               player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TANK_REFLECT_HIT, SoundSource.MASTER, 0.3F, pitch);
               player.playNotifySound(ModSounds.TANK_REFLECT_HIT, SoundSource.MASTER, 0.3F, pitch);
            } else {
               float pitch = 1.0F + (RANDOM.nextFloat() * 0.5F - 0.5F);
               player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TANK_HIT, SoundSource.MASTER, 0.3F, pitch);
               player.playNotifySound(ModSounds.TANK_HIT, SoundSource.MASTER, 0.3F, pitch);
            }
         }
      }
   }

   public static class TankReflectEffect extends TankAbility.TankEffect {
      public TankReflectEffect(int color, ResourceLocation resourceLocation) {
         super("Tank", color, resourceLocation);
      }
   }
}
