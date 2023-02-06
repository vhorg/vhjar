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
   protected final LootTable table;
   public float itemQuantity;
   public Entity source;
   protected final List<ItemStack> items = new ArrayList<>();

   public LootTableGenerator(Version version, LootTableKey table, float itemQuantity) {
      this(version, table.get(version), itemQuantity);
   }

   public LootTableGenerator(Version version, LootTable table, float itemQuantity) {
      this.version = version;
      this.table = table;
      this.itemQuantity = itemQuantity;
   }

   public LootTable getTable() {
      return this.table;
   }

   @Override
   public Iterator<ItemStack> getItems() {
      return this.items.iterator();
   }

   @Override
   public void generate(RandomSource random) {
      CommonEvents.LOOT_GENERATION.invoke(this, LootGenerationEvent.Phase.PRE);
      this.items.clear();
      if (this.table != null) {
         for (LootTable.Entry entry : this.table.getEntries()) {
            this.generateEntry(entry, random);
         }
      }

      CommonEvents.LOOT_GENERATION.invoke(this, LootGenerationEvent.Phase.POST);
   }

   protected void generateEntry(LootTable.Entry entry, RandomSource random) {
      int roll = entry.getRoll().get(random);
      float fRoll = roll * (1.0F + this.itemQuantity);
      roll = (int)fRoll + (random.nextFloat() < fRoll - roll ? 1 : 0);

      for (int i = 0; i < roll; i++) {
         entry.getPool().getRandomFlat(this.version, random).map(e -> e.getStack(random)).ifPresent(this.items::add);
      }
   }
}
