package iskallia.vault.task.util;

public class IntProgressCounter {
   private IntProgressCounter() {
   }

   public static IProgressCounter<Integer> initialize(IProgressCounter.Config<Integer> config) {
      return (IProgressCounter<Integer>)(config != null && config.getType().equals("sliding_time") && config instanceof SlidingTimeIntProgressCounter.Config c
         ? new SlidingTimeIntProgressCounter(c)
         : new SimpleIntProgressCounter());
   }
}
