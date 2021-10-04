package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.ExecuteConfig;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ExecuteAbility<C extends ExecuteConfig> extends AbilityEffect<C> {
   @Override
   public String getAbilityGroupName() {
      return "Execute";
   }

   public boolean onAction(C config, PlayerEntity player, boolean active) {
      if (player.func_70644_a(ModEffects.EXECUTE)) {
         return false;
      } else {
         EffectInstance newEffect = new EffectInstance(
            config.getEffect(), config.getEffectDuration(), config.getAmplifier(), false, config.getType().showParticles, config.getType().showIcon
         );
         player.field_70170_p
            .func_184148_a(
               player, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), ModSounds.EXECUTION_SFX, SoundCategory.MASTER, 0.7F, 1.0F
            );
         player.func_213823_a(ModSounds.EXECUTION_SFX, SoundCategory.MASTER, 0.7F, 1.0F);
         player.func_195064_c(newEffect);
         return false;
      }
   }

   @SubscribeEvent
   public void onDamage(LivingDamageEvent event) {
      if (this.doCulling() && !event.getEntity().func_130014_f_().func_201670_d()) {
         if (event.getSource().func_76346_g() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)event.getSource().func_76346_g();
            if (player.func_130014_f_() instanceof ServerWorld) {
               ServerWorld world = (ServerWorld)player.func_130014_f_();
               EffectInstance execute = player.func_70660_b(ModEffects.EXECUTE);
               if (execute != null) {
                  PlayerAbilitiesData data = PlayerAbilitiesData.get(world);
                  AbilityTree abilities = data.getAbilities(player);
                  AbilityNode<?, ?> node = abilities.getNodeByName("Execute");
                  if (node.getAbility() != this || node.isLearned()) {
                     C cfg = (C)node.getAbilityConfig();
                     LivingEntity entity = event.getEntityLiving();
                     float dmgDealt = entity.func_110138_aP() * cfg.getHealthPercentage();
                     event.setAmount(event.getAmount() + dmgDealt);
                     player.func_184614_ca().func_222118_a(1, player, playerEntity -> {});
                     player.field_70170_p
                        .func_184148_a(
                           null,
                           player.func_226277_ct_(),
                           player.func_226278_cu_(),
                           player.func_226281_cx_(),
                           SoundEvents.field_191244_bn,
                           SoundCategory.MASTER,
                           1.0F,
                           1.0F
                        );
                     if (this.removeEffect(cfg)) {
                        player.func_195063_d(ModEffects.EXECUTE);
                     } else {
                        execute.field_76460_b = cfg.getEffectDuration();
                     }
                  }
               }
            }
         }
      }
   }

   protected boolean removeEffect(C cfg) {
      return true;
   }

   protected boolean doCulling() {
      return true;
   }
}
