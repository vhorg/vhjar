package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.RampageDotConfig;
import iskallia.vault.skill.ability.effect.RampageAbility;
import iskallia.vault.util.DamageOverTimeHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RampageDotAbility extends RampageAbility<RampageDotConfig> {
   @SubscribeEvent
   public void onLivingDamage(LivingDamageEvent event) {
      if (!(event.getSource() instanceof RampageDotAbility.PlayerDamageOverTimeSource)) {
         if (!event.getEntity().func_130014_f_().func_201670_d()) {
            if (event.getSource().func_76346_g() instanceof PlayerEntity) {
               PlayerEntity player = (PlayerEntity)event.getSource().func_76346_g();
               if (player.func_130014_f_() instanceof ServerWorld) {
                  ServerWorld world = (ServerWorld)player.func_130014_f_();
                  EffectInstance ghostWalk = player.func_70660_b(ModEffects.RAMPAGE);
                  if (ghostWalk != null) {
                     AbilityTree abilities = PlayerAbilitiesData.get(world).getAbilities(player);
                     AbilityNode<?, ?> node = abilities.getNodeByName("Rampage");
                     if (node.getAbility() instanceof RampageDotAbility && node.isLearned()) {
                        RampageDotConfig cfg = (RampageDotConfig)node.getAbilityConfig();
                        DamageOverTimeHelper.applyDamageOverTime(
                           event.getEntityLiving(),
                           RampageDotAbility.PlayerDamageOverTimeSource.causeDoTDamage(player),
                           event.getAmount(),
                           cfg.getDotSecondDuration()
                        );
                        event.setAmount(0.0F);
                     }
                  }
               }
            }
         }
      }
   }

   public static class PlayerDamageOverTimeSource extends EntityDamageSource {
      private PlayerDamageOverTimeSource(@Nullable Entity damageSource) {
         super("player", damageSource);
      }

      public static RampageDotAbility.PlayerDamageOverTimeSource causeDoTDamage(PlayerEntity player) {
         return new RampageDotAbility.PlayerDamageOverTimeSource(player);
      }
   }
}
