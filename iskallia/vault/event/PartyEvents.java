package iskallia.vault.event;

import iskallia.vault.entity.EternalEntity;
import iskallia.vault.world.data.VaultPartyData;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PartyEvents {
   @SubscribeEvent
   public static void onAttack(LivingAttackEvent event) {
      Entity source = event.getSource().func_76346_g();
      if (source instanceof LivingEntity) {
         LivingEntity attacker = (LivingEntity)source;
         LivingEntity attacked = event.getEntityLiving();
         World world = attacked.func_130014_f_();
         if (world instanceof ServerWorld) {
            ServerWorld sWorld = (ServerWorld)world;
            UUID attackerUUID = attacker.func_110124_au();
            if (attacker instanceof EternalEntity) {
               attackerUUID = (UUID)((EternalEntity)attacker).getOwner().map(Function.identity(), Entity::func_110124_au);
            }

            UUID attackedUUID = attacked.func_110124_au();
            if (attacked instanceof EternalEntity) {
               attackerUUID = (UUID)((EternalEntity)attacked).getOwner().map(Function.identity(), Entity::func_110124_au);
            }

            VaultPartyData.Party party = VaultPartyData.get(sWorld).getParty(attackerUUID).orElse(null);
            if (party != null && party.hasMember(attackedUUID)) {
               event.setCanceled(true);
            }
         }
      }
   }
}
