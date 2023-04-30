package iskallia.vault.discoverylogic;

import iskallia.vault.discoverylogic.goal.base.DiscoveryGoal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;

public class DiscoveryGoalsState implements INBTSerializable<CompoundTag> {
   protected UUID playerUuid;
   protected Set<ResourceLocation> completedGoals;
   protected Map<ResourceLocation, Float> ongoingGoals;

   public DiscoveryGoalsState(ServerPlayer serverPlayer) {
      this(serverPlayer.getUUID());
   }

   public DiscoveryGoalsState(UUID playerUuid) {
      this.playerUuid = playerUuid;
      this.completedGoals = new HashSet<>();
      this.ongoingGoals = new HashMap<>();
   }

   public float getProcess(DiscoveryGoal<?> goal) {
      return this.ongoingGoals.getOrDefault(goal.getId(), 0.0F);
   }

   public void setProgress(ServerPlayer player, DiscoveryGoal<?> goal, float progress) {
      ResourceLocation goalId = goal.getId();
      if (!this.completedGoals.contains(goalId)) {
         this.ongoingGoals.put(goalId, progress);
         if (progress >= goal.getTargetProgress()) {
            goal.onGoalAchieved(player);
            this.ongoingGoals.remove(goalId);
            this.completedGoals.add(goalId);
         }
      }
   }

   public void progress(ServerPlayer player, DiscoveryGoal<?> goal, float deltaProgress) {
      ResourceLocation goalId = goal.getId();
      if (!this.completedGoals.contains(goalId)) {
         float progress = this.ongoingGoals.merge(goalId, deltaProgress, Float::sum);
         if (progress >= goal.getTargetProgress()) {
            goal.onGoalAchieved(player);
            this.ongoingGoals.remove(goalId);
            this.completedGoals.add(goalId);
         }
      }
   }

   public void resetGoal(ResourceLocation goalId) {
      this.ongoingGoals.computeIfPresent(goalId, (id, progress) -> 0.0F);
   }

   public void resetGoalIf(Predicate<ResourceLocation> predicate) {
      this.ongoingGoals.keySet().stream().filter(predicate).toList().forEach(this.ongoingGoals::remove);
   }

   public void deleteCompletions() {
      this.completedGoals.clear();
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag completedGoalsTag = new ListTag();
      this.completedGoals.forEach(goalId -> completedGoalsTag.add(StringTag.valueOf(goalId.toString())));
      nbt.put("CompletedGoals", completedGoalsTag);
      ListTag ongoingGoalKeys = new ListTag();
      ListTag ongoingGoalValues = new ListTag();
      this.ongoingGoals.forEach((goalId, progress) -> {
         ongoingGoalKeys.add(StringTag.valueOf(goalId.toString()));
         ongoingGoalValues.add(FloatTag.valueOf(progress));
      });
      nbt.put("OngoingGoalKeys", ongoingGoalKeys);
      nbt.put("OngoingGoalValues", ongoingGoalValues);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.completedGoals.clear();
      this.ongoingGoals.clear();
      ListTag completedGoalsTag = nbt.getList("CompletedGoals", 8);

      for (int i = 0; i < completedGoalsTag.size(); i++) {
         ResourceLocation goalId = ResourceLocation.tryParse(completedGoalsTag.getString(i));
         this.completedGoals.add(goalId);
      }

      ListTag ongoingGoalKeys = nbt.getList("OngoingGoalKeys", 8);
      ListTag ongoingGoalValues = nbt.getList("OngoingGoalValues", 5);

      for (int i = 0; i < ongoingGoalKeys.size(); i++) {
         ResourceLocation goalId = ResourceLocation.tryParse(ongoingGoalKeys.getString(i));
         float progress = ongoingGoalValues.getFloat(i);
         this.ongoingGoals.put(goalId, progress);
      }
   }
}
