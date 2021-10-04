package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class TalentUpgradeMessage {
   private final String talentName;

   public TalentUpgradeMessage(String talentName) {
      this.talentName = talentName;
   }

   public static void encode(TalentUpgradeMessage message, PacketBuffer buffer) {
      buffer.func_180714_a(message.talentName);
   }

   public static TalentUpgradeMessage decode(PacketBuffer buffer) {
      return new TalentUpgradeMessage(buffer.func_150789_c(32767));
   }

   public static void handle(TalentUpgradeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayerEntity sender = context.getSender();
         if (sender != null) {
            TalentGroup<?> talentGroup = ModConfigs.TALENTS.getByName(message.talentName);
            PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld)sender.field_70170_p);
            PlayerTalentsData abilitiesData = PlayerTalentsData.get((ServerWorld)sender.field_70170_p);
            TalentTree talentTree = abilitiesData.getTalents(sender);
            if (!ModConfigs.SKILL_GATES.getGates().isLocked(talentGroup, talentTree)) {
               TalentNode<?> talentNode = talentTree.getNodeByName(message.talentName);
               PlayerVaultStats stats = statsData.getVaultStats(sender);
               if (talentNode.getLevel() < talentGroup.getMaxLevel()) {
                  if (stats.getVaultLevel() >= talentNode.getGroup().getTalent(talentNode.getLevel() + 1).getLevelRequirement()) {
                     int requiredSkillPts = talentGroup.cost(talentNode.getLevel() + 1);
                     if (stats.getUnspentSkillPts() >= requiredSkillPts) {
                        abilitiesData.upgradeTalent(sender, talentNode);
                        statsData.spendSkillPts(sender, requiredSkillPts);
                     }
                  }
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
