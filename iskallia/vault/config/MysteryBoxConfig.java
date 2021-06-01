package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.WeightedList;
import iskallia.vault.vending.Product;
import net.minecraft.item.Items;

public class MysteryBoxConfig extends Config {
   @Expose
   public WeightedList<Product> POOL = new WeightedList<>();

   @Override
   public String getName() {
      return "mystery_box";
   }

   @Override
   protected void reset() {
      this.POOL.add(new Product(Items.field_151034_e, 8, null), 3);
      this.POOL.add(new Product(Items.field_151153_ao, 1, null), 1);
   }
}
