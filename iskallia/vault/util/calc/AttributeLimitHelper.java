package iskallia.vault.util.calc;

import iskallia.vault.client.ClientTalentData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.set.GolemSet;
import iskallia.vault.skill.set.NinjaSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.ParryTalent;
import iskallia.vault.skill.talent.type.PlayerTalent;
import iskallia.vault.skill.talent.type.ResistanceTalent;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class AttributeLimitHelper {
   public static float getCooldownReductionLimit(PlayerEntity player) {
      return 0.8F;
   }

   public static float getParryLimit(PlayerEntity player) {
      float limit = 0.4F;
      limit += getTalent(player, ModConfigs.TALENTS.PARRY).map(TalentNode::getTalent).map(ParryTalent::getAdditionalParryLimit).orElse(0.0F);
      SetTree sets = PlayerSetsData.get((ServerWorld)player.field_70170_p).getSets(player);

      for (SetNode<?> node : sets.getNodes()) {
         if (node.getSet() instanceof NinjaSet) {
            NinjaSet set = (NinjaSet)node.getSet();
            limit += set.getBonusParryCap();
         }
      }

      return limit;
   }

   public static float getResistanceLimit(PlayerEntity player) {
      float limit = 0.3F;
      limit += getTalent(player, ModConfigs.TALENTS.RESISTANCE).map(TalentNode::getTalent).map(ResistanceTalent::getAdditionalResistanceLimit).orElse(0.0F);
      SetTree sets = PlayerSetsData.get((ServerWorld)player.field_70170_p).getSets(player);

      for (SetNode<?> node : sets.getNodes()) {
         if (node.getSet() instanceof GolemSet) {
            GolemSet set = (GolemSet)node.getSet();
            limit += set.getBonusResistanceCap();
         }
      }

      return limit;
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
