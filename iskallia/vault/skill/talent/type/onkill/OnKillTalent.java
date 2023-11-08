package iskallia.vault.skill.talent.type.onkill;

import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.talent.type.EntityFilterTalent;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public abstract class OnKillTalent extends EntityFilterTalent {
   public OnKillTalent(int unlockLevel, int learnPointCost, int regretPointCost, EntityPredicate[] filter) {
      super(unlockLevel, learnPointCost, regretPointCost, filter);
   }

   protected OnKillTalent() {
   }

   public abstract void onDeath(LivingDeathEvent var1);

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void doEntityKill(LivingDeathEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer player) {
         TalentTree talents = PlayerTalentsData.get(player.getLevel()).getTalents(player);

         for (OnKillTalent talent : talents.getAll(OnKillTalent.class, Skill::isUnlocked)) {
            if (talent.isValid(event.getEntity())) {
               talent.onDeath(event);
            }
         }
      }
   }
}
