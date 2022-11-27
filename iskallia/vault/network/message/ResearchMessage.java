package iskallia.vault.network.message;

import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.world.data.DiscoveredModelsData;
import iskallia.vault.world.data.PlayerResearchesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent.Context;

public class ResearchMessage {
   public String researchName;

   public ResearchMessage() {
   }

   public ResearchMessage(String researchName) {
      this.researchName = researchName;
   }

   public static void encode(ResearchMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.researchName, 32767);
   }

   public static ResearchMessage decode(FriendlyByteBuf buffer) {
      ResearchMessage message = new ResearchMessage();
      message.researchName = buffer.readUtf(32767);
      return message;
   }

   public static void handle(ResearchMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
               Research research = ModConfigs.RESEARCHES.getByName(message.researchName);
               if (research != null) {
                  PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerLevel)sender.level);
                  PlayerResearchesData researchesData = PlayerResearchesData.get((ServerLevel)sender.level);
                  ResearchTree researchTree = researchesData.getResearches(sender);
                  if (!researchTree.isResearched(research)) {
                     int researchCost = researchTree.getResearchCost(research);
                     if (!ModConfigs.SKILL_GATES.getGates().isLocked(research.getName(), researchTree)) {
                        PlayerVaultStats stats = statsData.getVaultStats(sender);
                        int currentPoints = stats.getUnspentKnowledgePoints();
                        if (currentPoints >= researchCost) {
                           researchesData.research(sender, research);
                           List<String> discoversModels = research.getDiscoversModels();
                           statsData.spendKnowledgePoints(sender, researchCost);
                           if (discoversModels != null && !discoversModels.isEmpty()) {
                              DiscoveredModelsData discoveredModelsData = DiscoveredModelsData.get(sender.server);
                              discoversModels.stream()
                                 .<ResourceLocation>map(ResourceLocation::new)
                                 .forEach(modelId -> ModDynamicModels.REGISTRIES.getModelAndAssociatedItem(modelId).ifPresent(pair -> {
                                    DynamicModel gearModel = (DynamicModel)pair.getFirst();
                                    Item associatedItem = (Item)pair.getSecond();
                                    discoveredModelsData.discoverModelAndBroadcast(associatedItem, gearModel.getId(), sender);
                                 }));
                           }
                        }
                     }
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
