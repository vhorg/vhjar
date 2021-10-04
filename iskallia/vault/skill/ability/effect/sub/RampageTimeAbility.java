package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.sub.RampageTimeConfig;
import iskallia.vault.skill.ability.effect.RampageAbility;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RampageTimeAbility extends RampageAbility<RampageTimeConfig> {
   private static final Map<UUID, Integer> tickMap = new HashMap<>();

   @SubscribeEvent
   public void onLivingDamage(LivingDamageEvent event) {
      if (!event.getEntity().func_130014_f_().func_201670_d()) {
         if (event.getSource().func_76346_g() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)event.getSource().func_76346_g();
            if (player instanceof ServerPlayerEntity) {
               ServerPlayerEntity sPlayer = (ServerPlayerEntity)player;
               ServerWorld world = sPlayer.func_71121_q();
               int tick = tickMap.getOrDefault(sPlayer.func_110124_au(), 0);
               if (sPlayer.field_70173_aa != tick) {
                  tickMap.put(sPlayer.func_110124_au(), sPlayer.field_70173_aa);
                  EffectInstance rampage = sPlayer.func_70660_b(ModEffects.RAMPAGE);
                  if (rampage != null) {
                     AbilityTree abilities = PlayerAbilitiesData.get(world).getAbilities(sPlayer);
                     AbilityNode<?, ?> node = abilities.getNodeByName("Rampage");
                     if (node.getAbility() == this && node.isLearned()) {
                        RampageTimeConfig cfg = (RampageTimeConfig)node.getAbilityConfig();
                        rampage.field_76460_b = rampage.field_76460_b + cfg.getTickTimeIncreasePerHit();
                        sPlayer.field_71135_a.func_147359_a(new SPlayEntityEffectPacket(sPlayer.func_145782_y(), rampage));
                     }
                  }
               }
            }
         }
      }
   }
}
