package iskallia.vault.util.calc;

import iskallia.vault.client.ClientTalentData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.AbsorptionTalent;
import iskallia.vault.skill.talent.type.PlayerTalent;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class AbsorptionHelper {
   public static float getMaxAbsorption(PlayerEntity player) {
      if (getTalent(player, ModConfigs.TALENTS.BARBARIC).map(TalentNode::isLearned).orElse(false)) {
         return 0.0F;
      } else {
         int maxAbsorptionHealth = 12;
         float maxHealthPerc = 0.0F;
         maxHealthPerc += getTalent(player, ModConfigs.TALENTS.BARRIER)
            .map(TalentNode::getTalent)
            .map(AbsorptionTalent::getIncreasedAbsorptionLimit)
            .orElse(0.0F);
         return maxAbsorptionHealth + player.func_110138_aP() * maxHealthPerc;
      }
   }

   private static <T extends PlayerTalent> Optional<TalentNode<T>> getTalent(PlayerEntity player, TalentGroup<T> talentGroup) {
      if (player instanceof ServerPlayerEntity) {
         TalentTree talents = PlayerTalentsData.get(((ServerPlayerEntity)player).func_71121_q()).getTalents(player);
         return Optional.of(talents.getNodeOf(talentGroup));
      } else {
         return Optional.ofNullable(ClientTalentData.getLearnedTalentNode(talentGroup));
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.START && event.side.isServer() && event.player.field_70173_aa % 10 == 0) {
         PlayerEntity player = event.player;
         float absorption = player.func_110139_bj();
         if (absorption > 0.0F && absorption > getMaxAbsorption(player)) {
            player.func_110149_m(getMaxAbsorption(player));
         }
      }
   }
}
