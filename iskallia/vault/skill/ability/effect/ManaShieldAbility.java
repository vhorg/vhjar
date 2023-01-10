package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.ManaShieldConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbilityTickResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractToggleManaAbility;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ManaShieldAbility<C extends ManaShieldConfig> extends AbstractToggleManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Mana Shield";
   }

   protected AbilityActionResult doToggle(C config, ServerPlayer player, boolean active) {
      if (active) {
         ModEffects.MANA_SHIELD.addTo(player, 0);
         return AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED;
      } else {
         player.removeEffect(ModEffects.MANA_SHIELD);
         return AbilityActionResult.SUCCESS_COOLDOWN;
      }
   }

   protected void doToggleSound(C config, ServerPlayer player, boolean active) {
      if (active) {
         player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANA_SHIELD, SoundSource.MASTER, 0.4F, 1.0F);
         player.playNotifySound(ModSounds.MANA_SHIELD, SoundSource.MASTER, 0.4F, 1.0F);
      }
   }

   protected AbilityTickResult doInactiveTick(C config, ServerPlayer player) {
      if (player.hasEffect(ModEffects.MANA_SHIELD)) {
         player.removeEffect(ModEffects.MANA_SHIELD);
      }

      return AbilityTickResult.PASS;
   }

   protected void doManaDepleted(C config, ServerPlayer player) {
      player.removeEffect(ModEffects.MANA_SHIELD);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void on(LivingDamageEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && player.hasEffect(ModEffects.MANA_SHIELD)) {
         AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
         AbilityNode<?, ?> node = abilities.getNodeOf(this);
         if (node.getAbility() == this && node.isLearned() && node.getAbilityConfig() instanceof ManaShieldConfig config) {
            float var12 = Mth.clamp(config.getPercentageDamageAbsorbed(), 0.0F, 1.0F);
            float manaUsed = Math.min(event.getAmount() * var12 * config.getManaPerDamageScalar(), Mana.get(player));
            float damageAbsorbed = manaUsed / config.getManaPerDamageScalar();
            if (!Mth.equal(damageAbsorbed, 0.0F)) {
               if (Mth.equal(damageAbsorbed, event.getAmount())) {
                  event.setCanceled(true);
               } else {
                  event.setAmount(event.getAmount() - damageAbsorbed);
               }

               float mana = Mana.decrease(player, manaUsed);
               float pitch = 1.25F + -0.5F * (mana / Mana.getMax(player));
               player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANA_SHIELD_HIT, SoundSource.MASTER, 0.2F, pitch);
               player.playNotifySound(ModSounds.MANA_SHIELD_HIT, SoundSource.MASTER, 0.2F, pitch);
            }
         }
      }
   }

   public static class ManaShieldEffect extends ToggleAbilityEffect {
      public ManaShieldEffect(int color, ResourceLocation resourceLocation) {
         super("Mana Shield", color, resourceLocation);
      }
   }
}
