package iskallia.vault.task;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.BingoObjective;
import iskallia.vault.task.counter.TaskCounter;
import java.util.List;

public class BingoObjectiveTask extends ProgressConfiguredTask<Integer, BingoObjectiveTask.Config> {
   public BingoObjectiveTask() {
      super(new BingoObjectiveTask.Config(), TaskCounter.Adapter.INT);
   }

   public BingoObjectiveTask(TaskCounter<Integer, ?> counter) {
      super(new BingoObjectiveTask.Config(), counter, TaskCounter.Adapter.INT);
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK.register(this, event -> {
         if (this.parent == null || this.parent.hasActiveChildren()) {
            if (context.getVault() != null) {
               List<BingoObjective> bingoObjectives = context.getVault().get(Vault.OBJECTIVES).getAll(BingoObjective.class);
               bingoObjectives.forEach(bingoObjective -> this.counter.onSet(bingoObjective.getBingos(), context));
            }
         }
      });
      super.onAttach(context);
   }

   public static class Config extends ConfiguredTask.Config {
   }
}
