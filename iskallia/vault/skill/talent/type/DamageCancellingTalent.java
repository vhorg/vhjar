package iskallia.vault.skill.talent.type;

import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public abstract class DamageCancellingTalent extends PlayerTalent {
   public DamageCancellingTalent(int cost) {
      super(cost);
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingAttackEvent event) {
      if (!event.getEntity().func_130014_f_().func_201670_d()) {
         if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
            TalentTree abilities = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

            for (TalentNode<?> node : abilities.getNodes()) {
               if (node.getTalent() instanceof DamageCancellingTalent && ((DamageCancellingTalent)node.getTalent()).shouldCancel(event.getSource())) {
                  event.setCanceled(true);
                  return;
               }
            }
         }
      }
   }

   protected abstract boolean shouldCancel(DamageSource var1);
}
