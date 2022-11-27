package iskallia.vault.client.gui.screen.bounty.element.task;

import iskallia.vault.bounty.task.Task;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;

public class TaskScrollContainerElement extends VerticalScrollClipContainer<TaskScrollContainerElement> {
   private AbstractTaskElement<?> taskElement;

   public TaskScrollContainerElement(ISpatial spatial) {
      super(spatial);
   }

   public void setTaskElement(Task<?> task) {
      if (this.taskElement != null) {
         this.removeElement(this.taskElement);
      }

      this.taskElement = this.addElement(AbstractTaskElement.create(task.getTaskType(), Spatials.positionXY(0, 0).width(this.innerWidth()), task));
   }
}
