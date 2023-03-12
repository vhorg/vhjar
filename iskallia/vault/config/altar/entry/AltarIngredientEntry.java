package iskallia.vault.config.altar.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.IntRangeEntry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class AltarIngredientEntry {
   @Expose
   private List<ItemStack> items = new ArrayList<>();
   @Expose
   private IntRangeEntry amount;
   @Expose
   private double scale;

   public AltarIngredientEntry(List<ItemStack> items, int min, int max, double scale) {
      this.items = items;
      this.amount = new IntRangeEntry(min, max);
      this.scale = scale;
   }

   public List<ItemStack> getItems() {
      return this.items;
   }

   public int getAmount() {
      return this.amount.getRandom();
   }

   public double getScale() {
      return this.scale;
   }
}
