package iskallia.vault.task;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.task.source.EntityTaskSource;
import net.minecraft.world.entity.Entity;

public class UseNoManaTask extends NodeTask {
   @Override
   public boolean isCompleted() {
      return true;
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.MANA_MODIFY.register(this, data -> {
         if (this.parent == null || this.parent.isCompleted()) {
            if (context.getSource() instanceof EntityTaskSource entitySource) {
               if (data.getPlayer() instanceof Entity entity) {
                  if (entitySource.matches(entity)) {
                     if (data.getNewAmount() < data.getOldAmount()) {
                        for (ResettingTask child : this.getChildren(ResettingTask.class)) {
                           child.onReset(context);
                        }
                     }
                  }
               }
            }
         }
      });
      super.onAttach(context);
   }
}
