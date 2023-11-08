package iskallia.vault.core.world.processor;

import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;

public class ProcessorContext {
   private RandomSource random;
   private Vault vault;
   private final Map<Object, RandomSource> overrides = new HashMap<>();

   public ProcessorContext() {
   }

   public ProcessorContext(Vault vault, RandomSource random) {
      this.vault = vault;
      this.random = random;
   }

   public RandomSource getRandom(Object key) {
      return this.overrides.getOrDefault(key, this.random);
   }

   public void setRandom(BlockPos pos, RandomSource random) {
      this.overrides.put(pos, random);
   }

   public Vault getVault() {
      return this.vault;
   }
}
