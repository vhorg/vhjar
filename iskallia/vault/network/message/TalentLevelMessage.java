package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class TalentLevelMessage {
   private final String talentName;
   private final boolean isUpgrade;

   public TalentLevelMessage(String talentName, boolean isUpgrade) {
      this.talentName = talentName;
      this.isUpgrade = isUpgrade;
   }

   public static void encode(TalentLevelMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.talentName);
      buffer.writeBoolean(message.isUpgrade);
   }

   public static TalentLevelMessage decode(FriendlyByteBuf buffer) {
      return new TalentLevelMessage(buffer.readUtf(), buffer.readBoolean());
   }

   public static void handle(TalentLevelMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            if (message.isUpgrade) {
               upgradeTalent(message, sender);
            } else {
               downgradeTalent(message, sender);
            }
         }
      });
      context.setPacketHandled(true);
   }

   private static void upgradeTalent(TalentLevelMessage message, ServerPlayer player) {
      ServerLevel level = player.getLevel();
      TalentGroup<?> talentGroup = ModConfigs.TALENTS.getByName(message.talentName);
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      PlayerTalentsData abilitiesData = PlayerTalentsData.get(level);
      TalentTree talentTree = abilitiesData.getTalents(player);
      if (!ModConfigs.SKILL_GATES.getGates().isLocked(talentGroup, talentTree)) {
         TalentNode<?> talentNode = talentTree.getNodeByName(message.talentName);
         PlayerVaultStats stats = statsData.getVaultStats(player);
         if (talentNode.getLevel() < talentGroup.getMaxLevel()) {
            if (stats.getVaultLevel() >= talentNode.getGroup().getTalent(talentNode.getLevel() + 1).getLevelRequirement()) {
               int requiredSkillPts = talentGroup.cost(talentNode.getLevel() + 1);
               if (stats.getUnspentSkillPoints() >= requiredSkillPts) {
                  abilitiesData.upgradeTalent(player, talentNode);
                  statsData.spendSkillPoints(player, requiredSkillPts);
               }
            }
         }
      }
   }

   private static void downgradeTalent(TalentLevelMessage message, ServerPlayer player) {
      ServerLevel level = player.getLevel();
      TalentGroup<?> talentGroup = ModConfigs.TALENTS.getByName(message.talentName);
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      PlayerTalentsData talentsData = PlayerTalentsData.get(level);
      TalentTree talentTree = talentsData.getTalents(player);
      TalentNode<?> talentNode = talentTree.getNodeByName(message.talentName);
      PlayerVaultStats stats = statsData.getVaultStats(player);
      if (talentNode.isLearned()) {
         int requiredRegretPoints = talentNode.getTalent().getRegretCost();
         if (talentNode.getLevel() == 1) {
            for (TalentGroup<?> dependent : ModConfigs.SKILL_GATES.getGates().getTalentsDependingOn(talentGroup.getParentName())) {
               if (talentTree.getNodeOf(dependent).isLearned()) {
                  return;
               }
            }
         }

         if (stats.getUnspentRegretPoints() >= requiredRegretPoints) {
            talentsData.downgradeTalent(player, talentNode);
            statsData.spendSkillPoints(player, -talentNode.getTalent().getLearningCost());
            statsData.spendRegretPoints(player, requiredRegretPoints);
         }
      }
   }
}
