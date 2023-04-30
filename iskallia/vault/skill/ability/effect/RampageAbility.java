package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractRampageAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
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

public class RampageAbility extends AbstractRampageAbility {
   public RampageAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCostPerSecond, float damageIncrease) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond, damageIncrease);
   }

   public RampageAbility() {
   }

   @Override
   protected Ability.ActionResult doToggle(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (this.isActive()) {
            ModEffects.RAMPAGE.addTo(player, 0);
            return Ability.ActionResult.successCooldownDeferred();
         } else {
            player.removeEffect(ModEffects.RAMPAGE);
            return Ability.ActionResult.successCooldownImmediate();
         }
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doToggleSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         if (this.isActive()) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.RAMPAGE_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
            player.playNotifySound(ModSounds.RAMPAGE_SFX, SoundSource.PLAYERS, 0.2F, 1.0F);
         }
      });
   }

   @Override
   public Ability.TickResult doInactiveTick(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         if (player.hasEffect(ModEffects.RAMPAGE)) {
            player.removeEffect(ModEffects.RAMPAGE);
         }

         return Ability.TickResult.PASS;
      }).orElse(Ability.TickResult.PASS);
   }

   @Override
   protected void doManaDepleted(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.RAMPAGE));
   }

   @Override
   public void onRemove(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.RAMPAGE));
   }

   public static class RampageEffect extends ToggleAbilityEffect {
      private static final UUID DAMAGE_MULTIPLIER_ID = UUID.fromString("a69017ec-a50e-40a3-ac07-b19cb0ff705d");

      protected RampageEffect(Class<?> type, int color, ResourceLocation resourceLocation) {
         super(type, color, resourceLocation);
      }

      public RampageEffect(int color, ResourceLocation resourceLocation) {
         super(RampageAbility.class, color, resourceLocation);
      }

      @ParametersAreNonnullByDefault
      public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         if (livingEntity instanceof ServerPlayer player) {
            this.removeExistingDamageBuff(player);
            AbilityTree abilities = PlayerAbilitiesData.get((ServerLevel)player.level).getAbilities(player);

            for (RampageAbility ability : abilities.getAll(RampageAbility.class, Skill::isUnlocked)) {
               PlayerDamageHelper.applyMultiplier(
                  DAMAGE_MULTIPLIER_ID, player, ability.getDamageIncrease(player), PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY
               );
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
