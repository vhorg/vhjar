package iskallia.vault.task;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.task.source.EntityTaskSource;

public class TakeNoDamageTask extends NodeTask {
   @Override
   public boolean isCompleted() {
      return true;
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.ENTITY_DAMAGE.register(this, event -> {
         if (this.parent == null || this.parent.isCompleted()) {
            if (context.getSource() instanceof EntityTaskSource entitySource) {
               if (entitySource.matches(event.getEntity())) {
                  for (ResettingTask child : this.getChildren(ResettingTask.class)) {
                     child.onReset(context);
                  }
               }
            }
         }
      });
      super.onAttach(context);
   }
}
