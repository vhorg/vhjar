package iskallia.vault.event;

public enum ActiveFlags {
   IS_AOE_MINING,
   IS_FORTUNE_MINING,
   IS_DOT_ATTACKING,
   IS_LEECHING,
   IS_AOE_ATTACKING,
   IS_REFLECT_ATTACKING,
   IS_TOTEM_ATTACKING,
   IS_CHARMED_ATTACKING,
   IS_EFFECT_ATTACKING,
   IS_JAVELIN_ATTACKING,
   IS_SMITE_ATTACKING,
   IS_SMITE_BASE_ATTACKING,
   IS_CHAINING_ATTACKING,
   IS_THORNS_REFLECTING,
   IS_FIRESHOT_ATTACKING,
   IS_GLACIAL_SHATTER_ATTACKING,
   IS_AP_ATTACKING;

   private final ThreadLocal<Integer> activeReferences = ThreadLocal.withInitial(() -> 0);

   public boolean isSet() {
      return this.activeReferences.get() > 0;
   }

   public synchronized void runIfNotSet(Runnable run) {
      if (!this.isSet()) {
         this.push();

         try {
            run.run();
         } finally {
            this.pop();
         }
      }
   }

   public synchronized void push() {
      this.activeReferences.set(this.activeReferences.get() + 1);
   }

   public synchronized void pop() {
      this.activeReferences.set(this.activeReferences.get() - 1);
   }
}
