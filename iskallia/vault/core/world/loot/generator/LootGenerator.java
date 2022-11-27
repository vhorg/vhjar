package iskallia.vault.core.world.loot.generator;

import iskallia.vault.core.random.RandomSource;
import java.util.Iterator;
import net.minecraft.world.item.ItemStack;

public interface LootGenerator {
   Iterator<ItemStack> getItems();

   void generate(RandomSource var1);
}
