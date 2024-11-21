package iskallia.vault.task;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.task.counter.TaskCounter;

public class RepeatedTask extends ProgressConfiguredTask<Integer, RepeatedTask.Config> {
   public RepeatedTask() {
      super(new RepeatedTask.Config(), TaskCounter.Adapter.INT);
   }

   public RepeatedTask(RepeatedTask.Config config, TaskCounter<Integer, ?> counter) {
      super(config, counter, TaskCounter.Adapter.INT);
   }

   @Override
   public boolean hasActiveChildren() {
      return this.parent == null || this.parent.hasActiveChildren();
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK.register(this, event -> {
         if (this.parent == null || !this.parent.hasActiveChildren()) {
            for (Task child : this.getChildren()) {
               if (child.isCompleted() && child instanceof RepeatingTask repeatingTask) {
                  repeatingTask.onRepeat(context);
                  this.getCounter().onAdd(1, context);
               }
            }
         }
      });
      super.onAttach(context);
   }

   public static class Config extends ConfiguredTask.Config {
   }
}
