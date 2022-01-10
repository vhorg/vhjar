package iskallia.vault.util;

import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.influence.VaultAttributeInfluence;
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
            int rage = PlayerRageHelper.getCurrentRage(sPlayer, LogicalSide.SERVER);
            float multiplier = 1.0F;
            multiplier *= 1.0F - rage / 100.0F / 2.0F;
            VaultRaid vault = VaultRaidData.get(sPlayer.func_71121_q()).getActiveFor(sPlayer);
            if (vault != null) {
               for (VaultAttributeInfluence influence : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
                  if (influence.getType() == VaultAttributeInfluence.Type.HEALING_EFFECTIVENESS && !influence.isMultiplicative()) {
                     multiplier += influence.getValue();
                  }
               }

               for (VaultAttributeInfluence influencex : vault.getInfluences().getInfluences(VaultAttributeInfluence.class)) {
                  if (influencex.getType() == VaultAttributeInfluence.Type.HEALING_EFFECTIVENESS && influencex.isMultiplicative()) {
                     multiplier *= influencex.getValue();
                  }
               }
            }

            event.setAmount(event.getAmount() * multiplier);
         }
      }
   }
}
