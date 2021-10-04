package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.util.data.WeightedList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.Items;

public class ModBoxConfig extends Config {
   @Expose
   public Map<String, WeightedList<ProductEntry>> POOL = new HashMap<>();

   @Override
   public String getName() {
      return "mod_box";
   }

   @Override
   protected void reset() {
      WeightedList<ProductEntry> none = new WeightedList<>();
      none.add(new ProductEntry(Items.field_151034_e, 8, null), 3);
      none.add(new ProductEntry(Items.field_151153_ao, 1, null), 1);
      this.POOL.put("None", none);
      WeightedList<ProductEntry> decorator = new WeightedList<>();
      decorator.add(new ProductEntry(Items.field_151034_e, 8, null), 3);
      decorator.add(new ProductEntry(Items.field_151153_ao, 1, null), 1);
      this.POOL.put("Decorator", decorator);
      WeightedList<ProductEntry> refinedStorage = new WeightedList<>();
      refinedStorage.add(new ProductEntry(Items.field_151034_e, 8, null), 3);
      refinedStorage.add(new ProductEntry(Items.field_151153_ao, 1, null), 1);
      this.POOL.put("Storage Refined", refinedStorage);
      WeightedList<ProductEntry> oneWithEnder = new WeightedList<>();
      oneWithEnder.add(new ProductEntry(Items.field_151034_e, 8, null), 3);
      oneWithEnder.add(new ProductEntry(Items.field_151153_ao, 1, null), 1);
      this.POOL.put("One with Ender", oneWithEnder);
   }
}
