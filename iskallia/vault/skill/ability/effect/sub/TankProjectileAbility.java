package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.TankProjectileConfig;
import iskallia.vault.skill.ability.effect.TankAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractTankAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbilityTickResult;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TankProjectileAbility extends AbstractTankAbility<TankProjectileConfig> {
   protected AbilityActionResult doToggle(TankProjectileConfig config, ServerPlayer player, boolean active) {
      if (active) {
         int amplifier = (int)Mth.clamp(config.getKnockbackResistance() * 100.0F, 0.0F, 100.0F);
         ModEffects.TANK_PROJECTILE.addTo(player, amplifier);
         return AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED;
      } else {
         player.removeEffect(ModEffects.TANK_PROJECTILE);
         return AbilityActionResult.SUCCESS_COOLDOWN;
      }
   }

   protected void doToggleSound(TankProjectileConfig config, ServerPlayer player, boolean active) {
      if (active) {
         player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TANK_PROJECTILE, SoundSource.MASTER, 0.7F, 1.0F);
         player.playNotifySound(ModSounds.TANK_PROJECTILE, SoundSource.MASTER, 0.7F, 1.0F);
      }
   }

   protected AbilityTickResult doInactiveTick(TankProjectileConfig config, ServerPlayer player) {
      if (player.hasEffect(ModEffects.TANK_PROJECTILE)) {
         player.removeEffect(ModEffects.TANK_PROJECTILE);
      }

      return AbilityTickResult.PASS;
   }

   protected void doManaDepleted(TankProjectileConfig config, ServerPlayer player) {
      player.removeEffect(ModEffects.TANK_PROJECTILE);
   }

   @SubscribeEvent
   public void on(LivingDamageEvent event) {
      if (event.getEntityLiving() instanceof ServerPlayer player && player.hasEffect(ModEffects.TANK_PROJECTILE)) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         AbilityNode<?, ?> node = abilities.getNodeByName("Tank");
         if (node.getAbility() == this && node.isLearned() && node.getAbilityConfig() instanceof TankProjectileConfig config) {
            if (event.getSource().isProjectile()) {
               float percentageReduced = Mth.clamp(config.getPercentageReducedProjectileDamage(), 0.0F, 1.0F);
               if (Mth.equal(percentageReduced, 1.0F)) {
                  event.setCanceled(true);
               } else if (!Mth.equal(percentageReduced, 0.0F)) {
                  float amount = event.getAmount();
                  event.setAmount(amount - amount * percentageReduced);
               }
            }

            float pitch = 1.0F + (RANDOM.nextFloat() * 0.5F - 0.5F);
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TANK_PROJECTILE_HIT, SoundSource.MASTER, 0.3F, pitch);
            player.playNotifySound(ModSounds.TANK_PROJECTILE_HIT, SoundSource.MASTER, 0.3F, pitch);
         }
      }
   }

   public static class TankProjectileEffect extends TankAbility.TankEffect {
      public TankProjectileEffect(int color, ResourceLocation resourceLocation) {
         super("Tank", color, resourceLocation);
         this.addAttributeModifier(
            Attributes.KNOCKBACK_RESISTANCE, Mth.createInsecureUUID(new Random(resourceLocation.hashCode())).toString(), 0.01, Operation.ADDITION
         );
      }
   }
}
