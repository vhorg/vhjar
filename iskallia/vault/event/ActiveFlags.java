package iskallia.vault.event;

public enum ActiveFlags {
   IS_AOE_MINING,
   IS_FORTUNE_MINING,
   IS_DOT_ATTACKING,
   IS_LEECHING,
   IS_AOE_ATTACKING,
   IS_REFLECT_ATTACKING;

   private final ThreadLocal<Integer> activeReferences = ThreadLocal.withInitial(() -> 0);

   public boolean isSet() {
      return this.activeReferences.get() > 0;
   }

   public synchronized void runIfNotSet(Runnable run) {
      if (!this.isSet()) {
         this.activeReferences.set(this.activeReferences.get() + 1);

         try {
            run.run();
         } finally {
            this.activeReferences.set(this.activeReferences.get() - 1);
         }
      }
   }
}
