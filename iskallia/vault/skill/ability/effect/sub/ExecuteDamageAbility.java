package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.entity.EyesoreEntity;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.ExecuteDamageConfig;
import iskallia.vault.skill.ability.effect.ExecuteAbility;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ExecuteDamageAbility extends ExecuteAbility<ExecuteDamageConfig> {
   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onLivingDamage(LivingDamageEvent event) {
      if (!event.getEntity().func_130014_f_().func_201670_d()) {
         if (event.getSource().func_76346_g() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)event.getSource().func_76346_g();
            if (player.func_130014_f_() instanceof ServerWorld) {
               ServerWorld world = (ServerWorld)player.func_130014_f_();
               EffectInstance execute = player.func_70660_b(ModEffects.EXECUTE);
               if (execute != null) {
                  PlayerAbilitiesData data = PlayerAbilitiesData.get(world);
                  AbilityTree abilities = data.getAbilities(player);
                  AbilityNode<?, ?> node = abilities.getNodeByName("Execute");
                  if (node.getAbility() == this && node.isLearned()) {
                     ExecuteDamageConfig cfg = (ExecuteDamageConfig)node.getAbilityConfig();
                     float missingHealth = event.getEntityLiving().func_110138_aP() - event.getEntityLiving().func_110143_aJ();
                     float damageDealt = missingHealth * cfg.getDamagePercentPerMissingHealthPercent();
                     if (event.getEntityLiving() instanceof EyesoreEntity) {
                        damageDealt = Math.min(5.0F, damageDealt);
                     }

                     event.setAmount(event.getAmount() + damageDealt);
                  }
               }
            }
         }
      }
   }

   @Override
   protected boolean doCulling() {
      return false;
   }
}
