package iskallia.vault.task;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.CakeObjective;
import iskallia.vault.task.counter.TaskCounter;
import java.util.List;

public class CakeObjectiveTask extends ProgressConfiguredTask<Integer, CakeObjectiveTask.Config> {
   public CakeObjectiveTask() {
      super(new CakeObjectiveTask.Config(), TaskCounter.Adapter.INT);
   }

   public CakeObjectiveTask(TaskCounter<Integer, ?> counter) {
      super(new CakeObjectiveTask.Config(), counter, TaskCounter.Adapter.INT);
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK.register(this, event -> {
         if (this.parent == null || this.parent.hasActiveChildren()) {
            if (context.getVault() != null) {
               List<CakeObjective> cakeObjectives = context.getVault().get(Vault.OBJECTIVES).getAll(CakeObjective.class);
               cakeObjectives.forEach(cakeObjective -> this.counter.onSet(cakeObjective.get(CakeObjective.COUNT), context));
            }
         }
      });
      super.onAttach(context);
   }

   public static class Config extends ConfiguredTask.Config {
   }
}
