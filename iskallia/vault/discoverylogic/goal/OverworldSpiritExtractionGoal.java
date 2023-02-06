package iskallia.vault.discoverylogic.goal;

import iskallia.vault.discoverylogic.goal.base.DiscoveryGoal;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.DiscoveredModelsData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class OverworldSpiritExtractionGoal extends DiscoveryGoal {
   public OverworldSpiritExtractionGoal(float targetProgress) {
      super(targetProgress);
   }

   @Override
   public void onGoalAchieved(ServerPlayer player) {
      DiscoveredModelsData discoversData = DiscoveredModelsData.get(player.getLevel());
      ResourceLocation modelId = ModDynamicModels.Swords.DEATHS_DOOR.getId();
      if (!discoversData.getDiscoveredModels(player.getUUID()).contains(modelId)) {
         discoversData.discoverModelAndBroadcast(ModItems.SWORD, modelId, player);
      }
   }

   public void onSpiritExtracted(ServerPlayer player, int cost) {
      if (cost >= this.targetProgress) {
         this.progress(player, cost);
      }
   }
}
