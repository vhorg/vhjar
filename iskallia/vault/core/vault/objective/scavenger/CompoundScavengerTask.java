package iskallia.vault.core.vault.objective.scavenger;

import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.List;
import java.util.Optional;

public class CompoundScavengerTask extends ScavengeTask {
   public List<ScavengeTask> children;

   public CompoundScavengerTask(List<ScavengeTask> children) {
      this.children = children;
   }

   @Override
   public Optional<ScavengerGoal> generateGoal(int count, RandomSource random) {
      ScavengerGoal goal = null;

      for (ScavengeTask child : this.children) {
         ScavengerGoal other = child.generateGoal(count, random).orElse(null);
         if (other != null) {
            if (goal == null) {
               goal = other;
            } else {
               goal = goal.merge(other);
            }
         }
      }

      return Optional.ofNullable(goal);
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, Objective objective) {
      for (ScavengeTask child : this.children) {
         child.initServer(world, vault, objective);
      }
   }
}
