package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.RampageConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractRampageAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbilityTickResult;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.util.damage.PlayerDamageHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class RampageAbility extends AbstractRampageAbility<RampageConfig> {
   protected AbilityActionResult doToggle(RampageConfig config, ServerPlayer player, boolean active) {
      if (active) {
         ModEffects.RAMPAGE.addTo(player, 0);
         return AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED;
      } else {
         player.removeEffect(ModEffects.RAMPAGE);
         return AbilityActionResult.SUCCESS_COOLDOWN;
      }
   }

   protected void doToggleSound(RampageConfig config, ServerPlayer player, boolean active) {
      if (active) {
         player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.RAMPAGE_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
         player.playNotifySound(ModSounds.RAMPAGE_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
      }
   }

   protected AbilityTickResult doInactiveTick(RampageConfig config, ServerPlayer player) {
      if (player.hasEffect(ModEffects.RAMPAGE)) {
         player.removeEffect(ModEffects.RAMPAGE);
      }

      return AbilityTickResult.PASS;
   }

   protected void doManaDepleted(RampageConfig config, ServerPlayer player) {
      player.removeEffect(ModEffects.RAMPAGE);
   }

   public static class RampageEffect extends ToggleAbilityEffect {
      private static final UUID DAMAGE_MULTIPLIER_ID = UUID.fromString("a69017ec-a50e-40a3-ac07-b19cb0ff705d");

      public RampageEffect(int color, ResourceLocation resourceLocation) {
         super("Rampage", color, resourceLocation);
      }

      @ParametersAreNonnullByDefault
      public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            this.removeExistingDamageBuff(player);
            AbilityTree abilities = PlayerAbilitiesData.get((ServerLevel)player.getCommandSenderWorld()).getAbilities(player);
            AbilityNode<?, ?> abilityNode = abilities.getNodeByName("Rampage");
            RampageConfig config = (RampageConfig)abilityNode.getAbilityConfig();
            if (config != null) {
               PlayerDamageHelper.applyMultiplier(DAMAGE_MULTIPLIER_ID, player, config.getDamageIncrease(), PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY);
            }
         }

         super.addAttributeModifiers(livingEntity, attributeMap, amplifier);
      }

      @Override
      protected void removeAttributeModifiers(ServerPlayer player, AttributeMap attributeMap, int amplifier) {
         super.removeAttributeModifiers(player, attributeMap, amplifier);
         this.removeExistingDamageBuff(player);
      }

      private void removeExistingDamageBuff(ServerPlayer player) {
         PlayerDamageHelper.DamageMultiplier existing = PlayerDamageHelper.getMultiplier(player, DAMAGE_MULTIPLIER_ID);
         if (existing != null) {
            PlayerDamageHelper.removeMultiplier(player, existing);
         }
      }
   }
}
