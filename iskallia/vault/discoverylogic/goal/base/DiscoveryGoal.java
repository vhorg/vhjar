package iskallia.vault.discoverylogic.goal.base;

import iskallia.vault.discoverylogic.DiscoveryGoalsState;
import iskallia.vault.world.data.DiscoveryGoalStatesData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public abstract class DiscoveryGoal {
   protected ResourceLocation id;
   protected float targetProgress;

   public DiscoveryGoal(float targetProgress) {
      this.targetProgress = targetProgress;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void setId(ResourceLocation id) {
      if (this.id != null) {
         throw new IllegalStateException();
      } else {
         this.id = id;
      }
   }

   public float getTargetProgress() {
      return this.targetProgress;
   }

   public abstract void onGoalAchieved(ServerPlayer var1);

   public void resetProgress(ServerPlayer player) {
      DiscoveryGoalStatesData worldData = DiscoveryGoalStatesData.get(player.getLevel());
      DiscoveryGoalsState state = worldData.getState(player);
      state.resetGoal(this.getId());
      worldData.setDirty();
   }

   public void progress(ServerPlayer player, float deltaProgress) {
      DiscoveryGoalStatesData worldData = DiscoveryGoalStatesData.get(player.getLevel());
      DiscoveryGoalsState state = worldData.getState(player);
      state.progress(player, this, deltaProgress);
      worldData.setDirty();
   }
}
