package iskallia.vault.core.world.loot.entry;

import iskallia.vault.core.Version;
import iskallia.vault.core.random.RandomSource;
import net.minecraft.world.item.ItemStack;

public interface LootEntry {
   ItemStack getStack(RandomSource var1);

   LootEntry flatten(Version var1, RandomSource var2);

   boolean validate();
}
