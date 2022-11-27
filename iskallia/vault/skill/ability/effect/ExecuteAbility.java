package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.ExecuteConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbstractInstantAbility;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ExecuteAbility<C extends ExecuteConfig> extends AbstractInstantAbility<C> {
   @Override
   public String getAbilityGroupName() {
      return "Execute";
   }

   protected AbilityActionResult doAction(C config, ServerPlayer player, boolean active) {
      if (player.hasEffect(ModEffects.EXECUTE)) {
         return AbilityActionResult.FAIL;
      } else {
         MobEffectInstance newEffect = new MobEffectInstance(ModEffects.EXECUTE, config.getEffectDurationTicks(), 0, false, false, true);
         player.addEffect(newEffect);
         return AbilityActionResult.SUCCESS_COOLDOWN;
      }
   }

   protected void doParticles(C config, ServerPlayer player) {
   }

   protected void doSound(C config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.EXECUTION_SFX, SoundSource.PLAYERS, 0.4F, 1.0F);
      player.playNotifySound(ModSounds.EXECUTION_SFX, SoundSource.PLAYERS, 0.4F, 1.0F);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void on(LivingDamageEvent event) {
      if (!event.getEntity().getCommandSenderWorld().isClientSide()
         && event.getSource().getEntity() instanceof ServerPlayer serverPlayer
         && serverPlayer.getCommandSenderWorld() instanceof ServerLevel serverLevel) {
         MobEffectInstance effectInstance = serverPlayer.getEffect(ModEffects.EXECUTE);
         if (effectInstance != null) {
            PlayerAbilitiesData data = PlayerAbilitiesData.get(serverLevel);
            AbilityTree abilities = data.getAbilities(serverPlayer);
            AbilityNode<?, ?> node = abilities.getNodeByName("Execute");
            if (node.getAbility() == this && node.isLearned()) {
               ExecuteConfig config = (ExecuteConfig)node.getAbilityConfig();
               if (config != null) {
                  float maxHealth = event.getEntityLiving().getMaxHealth();
                  float damageDealt = maxHealth * config.getDamageHealthPercentage();
                  event.setAmount(event.getAmount() + damageDealt);
                  serverPlayer.removeEffect(ModEffects.EXECUTE);
               }
            }
         }
      }
   }

   public static class ExecuteEffect extends MobEffect {
      public ExecuteEffect(MobEffectCategory type, int color, ResourceLocation id) {
         super(type, color);
         this.setRegistryName(id);
      }
   }
}
