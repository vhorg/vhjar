package iskallia.vault.event;

public enum ActiveFlags {
   IS_AOE_MINING,
   IS_FORTUNE_MINING,
   IS_LEECHING,
   IS_CHAIN_ATTACKING,
   IS_AOE_ATTACKING;

   private int activeReferences = 0;

   public boolean isSet() {
      return this.activeReferences > 0;
   }

   public synchronized void runIfNotSet(Runnable run) {
      if (!this.isSet()) {
         this.activeReferences++;

         try {
            run.run();
         } finally {
            this.activeReferences--;
         }
      }
   }
}
