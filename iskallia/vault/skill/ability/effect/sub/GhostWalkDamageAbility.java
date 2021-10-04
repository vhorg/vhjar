package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.GhostWalkDamageConfig;
import iskallia.vault.skill.ability.effect.GhostWalkAbility;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GhostWalkDamageAbility extends GhostWalkAbility<GhostWalkDamageConfig> {
   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onLivingDamage(LivingDamageEvent event) {
      if (!event.getEntity().func_130014_f_().func_201670_d()) {
         if (event.getSource().func_76346_g() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)event.getSource().func_76346_g();
            if (player.func_130014_f_() instanceof ServerWorld) {
               ServerWorld world = (ServerWorld)player.func_130014_f_();
               EffectInstance ghostWalk = player.func_70660_b(ModEffects.GHOST_WALK);
               if (ghostWalk != null) {
                  AbilityTree abilities = PlayerAbilitiesData.get(world).getAbilities(player);
                  AbilityNode<?, ?> node = abilities.getNodeByName("Ghost Walk");
                  if (node.getAbility() == this && node.isLearned()) {
                     GhostWalkDamageConfig cfg = (GhostWalkDamageConfig)node.getAbilityConfig();
                     event.setAmount(event.getAmount() * cfg.getDamageMultiplierInGhostWalk());
                  }
               }
            }
         }
      }
   }

   @Override
   protected boolean doRemoveWhenDealingDamage() {
      return false;
   }

   @Override
   protected boolean preventsDamage() {
      return false;
   }
}
