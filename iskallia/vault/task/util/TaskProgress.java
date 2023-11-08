package iskallia.vault.task.util;

public class TaskProgress {
   private Number current;
   private Number target;

   public TaskProgress(Number current, Number target) {
      this.current = current;
      this.target = target;
   }

   public Number getCurrent() {
      return this.current;
   }

   public Number getTarget() {
      return this.target;
   }

   public double getProgress() {
      return this.getCurrent().doubleValue() / this.getTarget().doubleValue();
   }
}
