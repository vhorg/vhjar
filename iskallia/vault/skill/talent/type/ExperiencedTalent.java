package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerFavourData;
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
         TalentTree talents = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);
         ExperienceOrbEntity orb = event.getOrb();
         float increase = 0.0F;

         for (ExperiencedTalent talent : talents.getTalents(ExperiencedTalent.class)) {
            increase += talent.getIncreasedExpPercentage();
         }

         int favour = PlayerFavourData.get(player.func_71121_q()).getFavour(player.func_110124_au(), PlayerFavourData.VaultGodType.OMNISCIENT);
         if (favour >= 4) {
            increase += favour * 0.2F;
         } else if (favour <= -4) {
            increase -= Math.min(Math.abs(favour), 8) * 0.0625F;
         }

         orb.field_70530_e = (int)(orb.field_70530_e * (1.0F + increase));
      }
   }
}
