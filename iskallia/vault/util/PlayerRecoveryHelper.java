package iskallia.vault.util;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerRecoveryHelper {
   @SubscribeEvent
   public static void onHeal(LivingHealEvent event) {
      LivingEntity healed = event.getEntityLiving();
      if (!healed.func_130014_f_().func_201670_d()) {
         if (healed instanceof ServerPlayerEntity) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)healed;
            float healMultiplier = 1.0F;
            if (PlayerTalentsData.get(sPlayer.func_71121_q()).getTalents(sPlayer).hasLearnedNode(ModConfigs.TALENTS.BARBARIC)) {
               int rage = PlayerRageHelper.getCurrentRage(sPlayer, LogicalSide.SERVER);
               healMultiplier = 1.0F - rage / 100.0F;
            }

            if (healMultiplier > 0.01F) {
               event.setAmount(event.getAmount() * healMultiplier);
            }
         }
      }
   }
}
