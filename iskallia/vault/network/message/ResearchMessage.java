package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ResearchMessage {
   public String researchName;

   public ResearchMessage() {
   }

   public ResearchMessage(String researchName) {
      this.researchName = researchName;
   }

   public static void encode(ResearchMessage message, PacketBuffer buffer) {
      buffer.func_211400_a(message.researchName, 32767);
   }

   public static ResearchMessage decode(PacketBuffer buffer) {
      ResearchMessage message = new ResearchMessage();
      message.researchName = buffer.func_150789_c(32767);
      return message;
   }

   public static void handle(ResearchMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayerEntity sender = context.getSender();
         if (sender != null) {
            Research research = ModConfigs.RESEARCHES.getByName(message.researchName);
            if (research != null) {
               PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld)sender.field_70170_p);
               PlayerResearchesData researchesData = PlayerResearchesData.get((ServerWorld)sender.field_70170_p);
               ResearchTree researchTree = researchesData.getResearches(sender);
               int researchCost = researchTree.getResearchCost(research);
               if (!ModConfigs.SKILL_GATES.getGates().isLocked(research.getName(), researchTree)) {
                  PlayerVaultStats stats = statsData.getVaultStats(sender);
                  int currentPoints = research.usesKnowledge() ? stats.getUnspentKnowledgePts() : stats.getUnspentSkillPts();
                  if (currentPoints >= researchCost) {
                     researchesData.research(sender, research);
                     if (research.usesKnowledge()) {
                        statsData.spendKnowledgePts(sender, researchCost);
                     } else {
                        statsData.spendSkillPts(sender, researchCost);
                     }
                  }
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
