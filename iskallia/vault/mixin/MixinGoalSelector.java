package iskallia.vault.mixin;

import iskallia.vault.world.entity.ai.goal.GoalSelectorStack;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({GoalSelector.class})
public class MixinGoalSelector implements GoalSelectorStack {
   @Shadow
   @Final
   private Set<WrappedGoal> availableGoals;
   private final Stack<Set<WrappedGoal>> goalSetStack = new Stack<>();

   @Override
   public void pushGoalSet() {
      this.availableGoals.stream().filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
      this.goalSetStack.push(new HashSet<>(this.availableGoals));
      this.availableGoals.clear();
   }

   @Override
   public void popGoalSet() {
      if (!this.goalSetStack.isEmpty()) {
         this.availableGoals.stream().filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
         this.availableGoals.clear();
         this.availableGoals.addAll(this.goalSetStack.pop());
      }
   }
}
