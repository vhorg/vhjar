package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.data.WeightedList;
import java.util.Random;
import net.minecraft.item.ItemStack;

public class UnidentifiedTreasureKeyConfig extends Config {
   @Expose
   private WeightedList<ProductEntry> treasureKeys;

   @Override
   public String getName() {
      return "unidentified_treasure_key";
   }

   public ItemStack getRandomKey(Random random) {
      ProductEntry product = this.treasureKeys.getRandom(random);
      return product == null ? ItemStack.field_190927_a : product.generateItemStack();
   }

   @Override
   protected void reset() {
      this.treasureKeys = new WeightedList<>();
      this.treasureKeys.add(new ProductEntry(ModItems.ISKALLIUM_KEY, 1, null), 1);
   }
}
