package iskallia.vault.core.world.loot.generator;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.LootGenerationEvent;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.loot.LootTable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class LootTableGenerator implements LootGenerator {
   protected final Version version;
   protected final LootTableKey table;
   public Entity source;
   protected final List<ItemStack> items = new ArrayList<>();

   public LootTableGenerator(Version version, LootTableKey table) {
      this.version = version;
      this.table = table;
   }

   public LootTableKey getTable() {
      return this.table;
   }

   @Override
   public Iterator<ItemStack> getItems() {
      return this.items.iterator();
   }

   public float getMeanRolls() {
      float mean = 0.0F;

      for (LootTable.Entry entry : this.table.get(this.version).getEntries()) {
         mean += entry.getRoll().getMean();
      }

      return mean;
   }

   @Override
   public void generate(RandomSource random) {
      CommonEvents.LOOT_GENERATION.invoke(this, LootGenerationEvent.Phase.PRE);
      this.items.clear();
      if (this.table.get(this.version) != null) {
         for (LootTable.Entry entry : this.table.get(this.version).getEntries()) {
            this.generateEntry(entry, random);
         }
      }

      CommonEvents.LOOT_GENERATION.invoke(this, LootGenerationEvent.Phase.POST);
   }

   protected void generateEntry(LootTable.Entry entry, RandomSource random) {
      int count = entry.getRoll().get(random);

      for (int i = 0; i < count; i++) {
         entry.getPool().getRandomFlat(this.version, random).map(e -> e.getStack(random)).ifPresent(this.items::add);
      }
   }
}
