package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.base.GroupedSkill;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.TalentTree;
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
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      PlayerTalentsData abilitiesData = PlayerTalentsData.get(level);
      TalentTree talentTree = abilitiesData.getTalents(player);
      if (!ModConfigs.SKILL_GATES.getGates().isLocked(message.talentName, talentTree)) {
         talentTree.getForId(message.talentName).ifPresent(skill -> {
            SkillContext context = SkillContext.of(player);
            if (skill.getParent() instanceof GroupedSkill grouped) {
               grouped.select(skill.getId());
               skill = grouped;
            }

            if (skill instanceof LearnableSkill learnable && learnable.canLearn(context)) {
               learnable.learn(context);
               PlayerVaultStats stats = statsData.getVaultStats(player);
               stats.setSkillPoints(context.getLearnPoints());
               stats.setRegretPoints(context.getRegretPoints());
               talentTree.sync(context);
            }
         });
      }
   }

   private static void downgradeTalent(TalentLevelMessage message, ServerPlayer player) {
      ServerLevel level = player.getLevel();
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      PlayerTalentsData talentsData = PlayerTalentsData.get(level);
      TalentTree talentTree = talentsData.getTalents(player);
      talentTree.getForId(message.talentName).ifPresent(skill -> {
         SkillContext context = SkillContext.of(player);
         if (skill.getParent() instanceof GroupedSkill grouped) {
            grouped.select(skill.getId());
            skill = grouped;
         }

         if (skill instanceof LearnableSkill learnable && learnable.canRegret(context)) {
            learnable.regret(context);
            PlayerVaultStats stats = statsData.getVaultStats(player);
            stats.setSkillPoints(context.getLearnPoints());
            stats.setRegretPoints(context.getRegretPoints());
            talentTree.sync(context);
         }
      });
   }
}
