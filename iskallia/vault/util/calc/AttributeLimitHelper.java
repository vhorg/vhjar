package iskallia.vault.util.calc;

import iskallia.vault.client.ClientTalentData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.ParryTalent;
import iskallia.vault.skill.talent.type.PlayerTalent;
import iskallia.vault.skill.talent.type.ResistanceTalent;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class AttributeLimitHelper {
   public static float getCooldownReductionLimit(PlayerEntity player) {
      return 0.8F;
   }

   public static float getParryLimit(PlayerEntity player) {
      float limit = 0.4F;
      return limit + getTalent(player, ModConfigs.TALENTS.PARRY).map(TalentNode::getTalent).map(ParryTalent::getAdditionalParryLimit).orElse(0.0F);
   }

   public static float getResistanceLimit(PlayerEntity player) {
      float limit = 0.3F;
      return limit
         + getTalent(player, ModConfigs.TALENTS.RESISTANCE).map(TalentNode::getTalent).map(ResistanceTalent::getAdditionalResistanceLimit).orElse(0.0F);
   }

   private static <T extends PlayerTalent> Optional<TalentNode<T>> getTalent(PlayerEntity player, TalentGroup<T> talentGroup) {
      if (player instanceof ServerPlayerEntity) {
         TalentTree talents = PlayerTalentsData.get(((ServerPlayerEntity)player).func_71121_q()).getTalents(player);
         return Optional.of(talents.getNodeOf(talentGroup));
      } else {
         return Optional.ofNullable(ClientTalentData.getLearnedTalentNode(talentGroup));
      }
   }
}
