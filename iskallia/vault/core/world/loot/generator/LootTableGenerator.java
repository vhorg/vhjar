package iskallia.vault.core.world.loot.generator;

import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.LootGenerationEvent;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.loot.LootTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class LootTableGenerator implements LootGenerator {
   protected final Version version;
   protected final LootTable table;
   public float itemQuantity;
   public Map<ItemPredicate, Float> itemQuantityOverrides = new HashMap<>();
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

   public void setItems(List<ItemStack> items) {
      this.items.clear();
      this.items.addAll(items);
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
         entry.getPool().getRandomFlat(this.version, random).map(e -> {
            OverSizedItemStack stack = e.getOverStack(random);
            double increase = 0.0;

            for (Entry<ItemPredicate, Float> override : this.itemQuantityOverrides.entrySet()) {
               if (override.getKey().test(stack.overSizedStack())) {
                  increase += override.getValue().floatValue();
               }
            }

            double fAmount = stack.amount() * (1.0 + increase);

            int amount;
            for (amount = 0; fAmount > 0.0 && random.nextFloat() < fAmount; fAmount--) {
               amount++;
            }

            return stack.copyAmount(amount).splitByStackSize();
         }).ifPresent(this.items::addAll);
      }
   }
}
