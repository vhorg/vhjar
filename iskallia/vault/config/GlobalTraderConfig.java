package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.WeightedList;
import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class GlobalTraderConfig extends Config {
   @Expose
   public WeightedList<Trade> POOL = new WeightedList<>();
   @Expose
   public int TOTAL_TRADE_COUNT;
   @Expose
   public int MAX_TRADES;
   @Expose
   public int SKIN_UPDATE_RATE_SECONDS;

   @Override
   public String getName() {
      return "global_trader";
   }

   @Override
   protected void reset() {
      this.SKIN_UPDATE_RATE_SECONDS = 60;
      this.TOTAL_TRADE_COUNT = 3;
      this.MAX_TRADES = 1;
      this.POOL.add(new Trade(new Product(Items.field_151034_e, 8, null), null, new Product(Items.field_151153_ao, 1, null)), 20);
      this.POOL.add(new Trade(new Product(Items.field_151153_ao, 8, null), null, new Product(Items.field_196100_at, 1, null)), 3);
      this.POOL.add(new Trade(new Product(Items.field_221574_b, 64, null), null, new Product(Items.field_221585_m, 64, null)), 20);
      this.POOL.add(new Trade(new Product(Items.field_221577_e, 64, null), null, new Product(Items.field_151045_i, 8, null)), 20);
      CompoundNBT nbt = new CompoundNBT();
      ListNBT enchantments = new ListNBT();
      CompoundNBT knockback = new CompoundNBT();
      knockback.func_74778_a("id", "minecraft:knockback");
      knockback.func_74768_a("lvl", 10);
      enchantments.add(knockback);
      nbt.func_218657_a("Enchantments", enchantments);
      nbt.func_218657_a("ench", enchantments);
      this.POOL.add(new Trade(new Product(Items.field_196100_at, 8, null), null, new Product(Items.field_151055_y, 1, nbt)), 1);
   }
}
