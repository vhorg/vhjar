package iskallia.vault.skill.ability.effect;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.TankConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractTankAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbilityTickResult;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TankAbility extends AbstractTankAbility<TankConfig> {
   public TankAbility() {
      CommonEvents.PLAYER_STAT.of(PlayerStat.RESISTANCE).filter(data -> data.getEntity().hasEffect(ModEffects.TANK_RESISTANCE)).register(this, data -> {
         int amplifier = data.getEntity().getEffect(ModEffects.TANK_RESISTANCE).getAmplifier();
         float resistance = (amplifier + 1) / 100.0F;
         data.setValue(data.getValue() + resistance);
      });
   }

   protected AbilityActionResult doToggle(TankConfig config, ServerPlayer player, boolean active) {
      if (active) {
         ModEffects.TANK.addTo(player, 0);
         return AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED;
      } else {
         player.removeEffect(ModEffects.TANK);
         player.removeEffect(ModEffects.TANK_RESISTANCE);
         return AbilityActionResult.SUCCESS_COOLDOWN;
      }
   }

   protected void doToggleSound(TankConfig config, ServerPlayer player, boolean active) {
      if (active) {
         player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TANK, SoundSource.MASTER, 0.7F, 1.0F);
         player.playNotifySound(ModSounds.TANK, SoundSource.MASTER, 0.7F, 1.0F);
      }
   }

   protected AbilityTickResult doInactiveTick(TankConfig config, ServerPlayer player) {
      if (player.hasEffect(ModEffects.TANK)) {
         player.removeEffect(ModEffects.TANK);
      }

      if (player.hasEffect(ModEffects.TANK_RESISTANCE)) {
         player.removeEffect(ModEffects.TANK_RESISTANCE);
      }

      return AbilityTickResult.PASS;
   }

   protected void doManaDepleted(TankConfig config, ServerPlayer player) {
      player.removeEffect(ModEffects.TANK);
      player.removeEffect(ModEffects.TANK_RESISTANCE);
   }

   @SubscribeEvent
   public void on(LivingDamageEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayer player && player.hasEffect(ModEffects.TANK) && event.getSource().getEntity() instanceof LivingEntity) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         AbilityNode<?, ?> node = abilities.getNodeByName("Tank");
         if (node.getAbility() == this && node.isLearned() && node.getAbilityConfig() instanceof TankConfig config) {
            MobEffectInstance resistanceEffectInstance = player.getEffect(ModEffects.TANK_RESISTANCE);
            int existingAmplifier = resistanceEffectInstance == null ? 0 : resistanceEffectInstance.getAmplifier();
            int newAmplifier = (int)(existingAmplifier + Math.max(config.getResistancePercentAddedPerHit() * 100.0F, 1.0F));
            int clampedAmplifier = Math.min(newAmplifier, (int)(config.getResistancePercentCap() * 100.0F));
            player.removeEffect(ModEffects.TANK_RESISTANCE);
            player.addEffect(new MobEffectInstance(ModEffects.TANK_RESISTANCE, config.getDurationTicksPerHit(), clampedAmplifier, false, false, true));
            float pitch = 1.0F + (RANDOM.nextFloat() * 0.5F - 0.5F);
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TANK_HIT, SoundSource.MASTER, 0.7F, pitch);
            player.playNotifySound(ModSounds.TANK_HIT, SoundSource.MASTER, 0.7F, pitch);
         }
      }
   }

   public static boolean hasTankEffectActive(LivingEntity entity) {
      for (MobEffectInstance instance : entity.getActiveEffects()) {
         if (instance.getEffect() instanceof TankAbility.TankEffect) {
            return true;
         }
      }

      return false;
   }

   public static class TankEffect extends ToggleAbilityEffect {
      protected TankEffect(String abilityGroup, int color, ResourceLocation resourceLocation) {
         super(abilityGroup, color, resourceLocation);
      }

      public TankEffect(int color, ResourceLocation resourceLocation) {
         this("Tank", color, resourceLocation);
      }
   }

   public static class TankResistanceEffect extends MobEffect {
      public TankResistanceEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(resourceLocation);
      }
   }
}
