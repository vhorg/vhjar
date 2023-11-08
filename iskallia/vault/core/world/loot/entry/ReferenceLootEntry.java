package iskallia.vault.core.world.loot.entry;

import com.google.gson.JsonElement;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.LootPoolKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.VaultRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ReferenceLootEntry implements LootEntry {
   private ResourceLocation reference;

   public ReferenceLootEntry() {
   }

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
   public List<ItemStack> getStack(RandomSource random) {
      throw new UnsupportedOperationException("Indirect entry, flatten first");
   }

   @Override
   public OverSizedItemStack getOverStack(RandomSource random) {
      throw new UnsupportedOperationException("Indirect entry, flatten first");
   }

   @Override
   public LootEntry flatten(Version version, RandomSource random) {
      LootEntry flattened = this.getReference().get(version).getRandom(random).orElse(null);
      return flattened == null ? null : flattened.flatten(version, random);
   }

   @Override
   public Optional<JsonElement> writeJson() {
      return Adapters.IDENTIFIER.writeJson(this.reference);
   }

   @Override
   public void readJson(JsonElement json) {
      Adapters.IDENTIFIER.readJson(json).ifPresent(value -> this.reference = value);
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
