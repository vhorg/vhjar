package iskallia.vault.core.world.loot.entry;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootPoolKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.VaultRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ReferenceLootEntry implements LootEntry {
   private final ResourceLocation reference;

   public ReferenceLootEntry(ResourceLocation reference) {
      this.reference = reference;
   }

   public ResourceLocation getReferenceId() {
      return this.reference;
   }

   public LootPoolKey getReference() {
      return VaultRegistry.LOOT_POOL.getKey(this.reference);
   }

   @Override
   public ItemStack getStack(RandomSource random) {
      throw new UnsupportedOperationException("Indirect entry, flatten first");
   }

   @Override
   public LootEntry flatten(Version version, RandomSource random) {
      LootEntry flattened = this.getReference().get(version).getRandom(random).orElse(null);
      return flattened == null ? null : flattened.flatten(version, random);
   }

   @Override
   public boolean validate() {
      return this.getReference() != null;
   }

   @Override
   public String toString() {
      return "{reference=" + this.reference.toString() + "}";
   }
}
