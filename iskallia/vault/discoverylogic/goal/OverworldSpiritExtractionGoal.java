package iskallia.vault.discoverylogic.goal;

import iskallia.vault.discoverylogic.goal.base.DiscoveryGoal;
import net.minecraft.server.level.ServerPlayer;

public class OverworldSpiritExtractionGoal extends DiscoveryGoal<OverworldSpiritExtractionGoal> {
   public OverworldSpiritExtractionGoal(float targetProgress) {
      super(targetProgress);
   }

   public void onSpiritExtracted(ServerPlayer player, int cost) {
      if (cost >= this.targetProgress) {
         this.progress(player, cost);
      }
   }
}
