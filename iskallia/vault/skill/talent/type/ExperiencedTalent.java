package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ExperiencedTalent extends PlayerTalent {
   @Expose
   private final float increasedExpPercentage;

   public ExperiencedTalent(int cost, float increasedExpPercentage) {
      super(cost);
      this.increasedExpPercentage = increasedExpPercentage;
   }

   public float getIncreasedExpPercentage() {
      return this.increasedExpPercentage;
   }

   @SubscribeEvent
   public static void onOrbPickup(PickupXp event) {
      if (event.getPlayer() instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
         TalentTree abilities = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

         for (TalentNode<?> node : abilities.getNodes()) {
            if (node.getTalent() instanceof ExperiencedTalent) {
               ExperiencedTalent experienced = (ExperiencedTalent)node.getTalent();
               ExperienceOrbEntity orb = event.getOrb();
               orb.field_70530_e = (int)(orb.field_70530_e * (1.0F + experienced.getIncreasedExpPercentage()));
            }
         }
      }
   }
}
