package iskallia.vault.event;

import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.world.data.VaultPartyData;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PartyEvents {
   @SubscribeEvent
   public static void onAttack(LivingAttackEvent event) {
      if (event.getSource().getEntity() instanceof LivingEntity attacker) {
         LivingEntity attacked = event.getEntityLiving();
         if (attacked.getCommandSenderWorld() instanceof ServerLevel sWorld) {
            UUID attackerUUID = attacker.getUUID();
            if (attacker instanceof EternalEntity) {
               attackerUUID = (UUID)((EternalEntity)attacker).getOwner().map(Function.identity(), Entity::getUUID);
            }

            UUID attackedUUID = attacked.getUUID();
            if (attacked instanceof EternalEntity) {
               attackerUUID = (UUID)((EternalEntity)attacked).getOwner().map(Function.identity(), Entity::getUUID);
            }

            VaultPartyData.Party party = VaultPartyData.get(sWorld).getParty(attackerUUID).orElse(null);
            if (party != null && party.hasMember(attackedUUID)) {
               event.setCanceled(true);
            }
         }
      }
   }
}
