package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.entity.EternalEntity;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.TankSlowConfig;
import iskallia.vault.skill.ability.effect.TankAbility;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TankSlowAbility extends TankAbility<TankSlowConfig> {
   @SubscribeEvent
   public void onWorldTick(PlayerTickEvent event) {
      if (event.phase != Phase.END) {
         EffectInstance tank = event.player.func_70660_b(ModEffects.TANK);
         if (tank != null) {
            if (event.player.func_130014_f_() instanceof ServerWorld) {
               ServerWorld sWorld = (ServerWorld)event.player.func_130014_f_();
               if (sWorld.func_73046_m().func_71259_af() % 20 != 0) {
                  return;
               }

               AbilityTree abilities = PlayerAbilitiesData.get(sWorld).getAbilities(event.player);
               AbilityNode<?, ?> tankNode = abilities.getNodeByName("Tank");
               if (tankNode.getAbility() == this && tankNode.isLearned()) {
                  TankSlowConfig config = (TankSlowConfig)tankNode.getAbilityConfig();
                  List<LivingEntity> entities = EntityHelper.getNearby(sWorld, event.player.func_233580_cy_(), config.getSlowAreaRadius(), LivingEntity.class);
                  entities.removeIf(e -> e instanceof PlayerEntity || e instanceof EternalEntity);

                  for (LivingEntity entity : entities) {
                     entity.func_195064_c(new EffectInstance(Effects.field_76421_d, 80, config.getSlownessAmplifier(), false, false, false));
                  }
               }
            }
         }
      }
   }
}
