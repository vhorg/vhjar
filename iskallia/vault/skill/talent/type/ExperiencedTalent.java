package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
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
      if (event.getPlayer() instanceof ServerPlayer) {
         ServerPlayer player = (ServerPlayer)event.getPlayer();
         TalentTree talents = PlayerTalentsData.get(player.getLevel()).getTalents(player);
         ExperienceOrb orb = event.getOrb();
         float increase = 0.0F;

         for (ExperiencedTalent talent : talents.getTalents(ExperiencedTalent.class)) {
            increase += talent.getIncreasedExpPercentage();
         }

         orb.value = (int)(orb.value * (1.0F + increase));
      }
   }
}
