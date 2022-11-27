package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.RangeEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.item.Item;

public class EternalConfig extends Config {
   @Expose
   private int expPerLevel;
   @Expose
   private final Map<String, RangeEntry> foodExpRanges = new HashMap<>();

   @Override
   public String getName() {
      return "eternal";
   }

   public int getExpForLevel(int nextLevel) {
      return this.expPerLevel * nextLevel;
   }

   public Optional<Integer> getFoodExp(Item foodItem) {
      return Optional.ofNullable(this.foodExpRanges.get(foodItem.getRegistryName().toString())).map(RangeEntry::getRandom);
   }

   @Override
   protected void reset() {
      this.expPerLevel += 150;
      this.foodExpRanges.clear();
   }
}
