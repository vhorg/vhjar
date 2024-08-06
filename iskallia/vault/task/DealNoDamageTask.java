package iskallia.vault.task;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.task.source.EntityTaskSource;
import net.minecraft.world.entity.Entity;

public class DealNoDamageTask extends NodeTask {
   @Override
   public boolean isCompleted() {
      return true;
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.ENTITY_DAMAGE.register(this, event -> {
         if (this.parent == null || this.parent.isCompleted()) {
            if (context.getSource() instanceof EntityTaskSource entitySource) {
               Entity attacker = event.getSource().getEntity();
               if (attacker != null && entitySource.matches(attacker)) {
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
