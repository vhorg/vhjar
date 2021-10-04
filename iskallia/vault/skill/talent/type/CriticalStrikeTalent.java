package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class CriticalStrikeTalent extends PlayerTalent {
   @Expose
   private final float chance;

   public CriticalStrikeTalent(int cost, float chance) {
      super(cost);
      this.chance = chance;
   }

   public float getChance() {
      return this.chance;
   }

   @SubscribeEvent
   public static void onCriticalHit(CriticalHitEvent event) {
      if (!event.getEntity().field_70170_p.func_201670_d()) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
         TalentTree talents = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

         for (CriticalStrikeTalent criticalStrikeTalent : talents.getTalents(CriticalStrikeTalent.class)) {
            if (player.field_70170_p.field_73012_v.nextFloat() < criticalStrikeTalent.getChance()) {
               if (event.getDamageModifier() < 1.5F) {
                  event.setDamageModifier(1.5F);
               }

               event.setResult(Result.ALLOW);
               return;
            }
         }
      }
   }
}
