package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.CleanseImmuneConfig;
import iskallia.vault.skill.ability.effect.CleanseAbility;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerImmunityData;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;

public class CleanseImmuneAbility extends CleanseAbility<CleanseImmuneConfig> {
   protected void removeEffects(CleanseImmuneConfig config, ServerPlayerEntity player, List<EffectInstance> effects) {
      if (!effects.isEmpty()) {
         if (player.func_130014_f_() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld)player.func_130014_f_();
            PlayerImmunityData data = PlayerImmunityData.get(world);
            Collections.shuffle(effects);
            data.addEffect(player, effects.get(0));
            effects.forEach(effect -> player.func_195063_d(effect.func_188419_a()));
            EffectInstance activeEffect = player.func_70660_b(ModEffects.IMMUNITY);
            EffectInstance newEffect = new EffectInstance(ModEffects.IMMUNITY, config.getImmunityDuration(), 0, false, false, true);
            if (activeEffect == null) {
               player.func_195064_c(newEffect);
            }
         }
      }
   }

   @SubscribeEvent
   public void onEffect(PotionApplicableEvent event) {
      if (event.getEntityLiving() instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)event.getEntityLiving();
         EffectInstance immunity = player.func_70660_b(ModEffects.IMMUNITY);
         if (immunity != null) {
            EffectInstance effectInstance = event.getPotionEffect();
            if (!effectInstance.func_188419_a().func_188408_i()) {
               if (player.func_130014_f_() instanceof ServerWorld) {
                  ServerWorld world = (ServerWorld)player.func_130014_f_();
                  PlayerAbilitiesData data = PlayerAbilitiesData.get(world);
                  AbilityTree abilities = data.getAbilities(player);
                  AbilityNode<?, ?> node = abilities.getNodeByName("Cleanse");
                  if (node.getAbility() == this && node.isLearned()) {
                     PlayerImmunityData immunityData = PlayerImmunityData.get(world);
                     if (immunityData.getEffects(player.func_110124_au()).stream().anyMatch(effect -> effect.equals(effectInstance.func_188419_a()))) {
                        event.setResult(Result.DENY);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onImmunityRemoved(PotionExpiryEvent event) {
      if (event.getEntityLiving() instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)event.getEntityLiving();
         if (player.func_130014_f_() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld)player.func_130014_f_();
            if (event.getPotionEffect() != null && event.getPotionEffect().func_188419_a() == ModEffects.IMMUNITY) {
               PlayerImmunityData.get(world).removeEffects(player);
            }
         }
      }
   }
}
