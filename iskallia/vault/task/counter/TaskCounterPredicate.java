package iskallia.vault.task.counter;

public enum TaskCounterPredicate {
   EQUAL {
      @Override
      public <N> boolean test(N a, N b, TaskCounter.Group<N> group) {
         return group.getOrdering().compare(a, b) == 0;
      }
   },
   GREATER_THAN {
      @Override
      public <N> boolean test(N a, N b, TaskCounter.Group<N> group) {
         return group.getOrdering().compare(a, b) > 0;
      }
   },
   GREATER_OR_EQUAL_TO {
      @Override
      public <N> boolean test(N a, N b, TaskCounter.Group<N> group) {
         return group.getOrdering().compare(a, b) >= 0;
      }
   },
   LESS_THAN {
      @Override
      public <N> boolean test(N a, N b, TaskCounter.Group<N> group) {
         return group.getOrdering().compare(a, b) < 0;
      }
   },
   LESS_OR_EQUAL_TO {
      @Override
      public <N> boolean test(N a, N b, TaskCounter.Group<N> group) {
         return group.getOrdering().compare(a, b) <= 0;
      }
   };

   public abstract <N> boolean test(N var1, N var2, TaskCounter.Group<N> var3);
}
