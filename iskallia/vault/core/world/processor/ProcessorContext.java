package iskallia.vault.core.world.processor;

import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;

public class ProcessorContext {
   public RandomSource random;
   public Vault vault;

   public ProcessorContext() {
   }

   public ProcessorContext(Vault vault, RandomSource random) {
      this.vault = vault;
      this.random = random;
   }
}
