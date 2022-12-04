package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.config.StonefallConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantManaAbility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StonefallAbility<C extends StonefallConfig> extends AbstractInstantManaAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Stonefall";
   }

   protected boolean canDoAction(C config, ServerPlayer player, boolean active) {
      return !player.hasEffect(ModEffects.STONEFALL) && super.canDoAction(config, player, active);
   }

   protected AbilityActionResult doAction(C config, ServerPlayer player, boolean active) {
      player.addEffect(new MobEffectInstance(ModEffects.STONEFALL, config.getDurationTicks(), 0, false, false, true));
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   @SubscribeEvent
   public void on(LivingAttackEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         MobEffectInstance effectInstance = serverPlayer.getEffect(ModEffects.STONEFALL);
         if (effectInstance != null) {
            if (event.getSource() == DamageSource.FALL) {
               event.setCanceled(true);
            }
         }
      }
   }

   public static class StonefallEffect extends MobEffect {
      public StonefallEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.BENEFICIAL, color);
         this.setRegistryName(resourceLocation);
      }
   }
}
