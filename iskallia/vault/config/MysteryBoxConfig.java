package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.item.Items;

public class MysteryBoxConfig extends Config {
   @Expose
   public WeightedList<ProductEntry> POOL = new WeightedList<>();

   @Override
   public String getName() {
      return "mystery_box";
   }

   @Override
   protected void reset() {
      this.POOL.add(new ProductEntry(Items.field_151034_e, 8, null), 3);
      this.POOL.add(new ProductEntry(Items.field_151153_ao, 1, null), 1);
   }
}
