package iskallia.vault.discoverylogic.goal.base;

import iskallia.vault.discoverylogic.DiscoveryGoalsState;
import iskallia.vault.world.data.DiscoveryGoalStatesData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public abstract class DiscoveryGoal<G extends DiscoveryGoal<G>> {
   protected ResourceLocation id;
   protected DiscoveryGoal.RewardGranter<G> reward;
   protected float targetProgress;

   public DiscoveryGoal(float targetProgress) {
      this.targetProgress = targetProgress;
   }

   protected G getSelf() {
      return (G)this;
   }

   public G setReward(DiscoveryGoal.RewardGranter<G> reward) {
      this.reward = reward;
      return this.getSelf();
   }

   public void onGoalAchieved(ServerPlayer player) {
      if (this.reward != null) {
         this.reward.grant(player, this.getSelf());
      }
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

   @FunctionalInterface
   public interface RewardGranter<G extends DiscoveryGoal<G>> {
      void grant(ServerPlayer var1, G var2);
   }
}
