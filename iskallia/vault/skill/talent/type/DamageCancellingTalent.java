package iskallia.vault.skill.talent.type;

import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

@Deprecated(
   forRemoval = true
)
public abstract class DamageCancellingTalent extends PlayerTalent {
   public DamageCancellingTalent(int cost) {
      super(cost);
   }

   public static void onLivingHurt(LivingAttackEvent event) {
      if (!event.getEntity().getCommandSenderWorld().isClientSide()) {
         if (event.getEntityLiving() instanceof ServerPlayer player) {
            TalentTree abilities = PlayerTalentsData.get(player.getLevel()).getTalents(player);

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
