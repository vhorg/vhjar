package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class GlobalTraderConfig extends Config {
   @Expose
   public Map<Integer, GlobalTraderConfig.GlobalTradePool> POOLS = new HashMap<>();
   @Expose
   public int SKIN_UPDATE_RATE_SECONDS;

   @Override
   public String getName() {
      return "global_trader";
   }

   @Override
   protected void reset() {
      this.SKIN_UPDATE_RATE_SECONDS = 60;

      for (int i = 0; i < 4; i++) {
         this.POOLS.put(i, new GlobalTraderConfig.GlobalTradePool());
      }
   }

   public GlobalTraderConfig.GlobalTradePool getPool(int id) {
      return this.POOLS.get(id);
   }

   public static class GlobalTradePool implements INBTSerializable<CompoundNBT> {
      @Expose
      public WeightedList<Trade> POOL = new WeightedList<>();
      @Expose
      public int TOTAL_TRADE_COUNT;
      @Expose
      public int MAX_TRADES;
      @Expose
      public int RESET_INTERVAL_HOUR;
      @Expose
      public String skin;
      private int currentTick = 0;
      private boolean isReset = false;
      private int resetCounter = 0;

      public GlobalTradePool() {
         this.TOTAL_TRADE_COUNT = 3;
         this.MAX_TRADES = 1;
         this.RESET_INTERVAL_HOUR = 24;
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

      public void tick() {
         if (this.currentTick++ >= this.RESET_INTERVAL_HOUR * 60 * 60 * 20) {
            this.currentTick = 0;
            this.isReset = true;
         }

         if (this.isReset && this.resetCounter++ >= 600) {
            this.isReset = false;
            this.resetCounter = 0;
         }
      }

      public boolean ready() {
         return this.currentTick == 0 && this.isReset;
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74768_a("CurrentTick", this.currentTick);
         nbt.func_74757_a("IsReset", this.isReset);
         nbt.func_74768_a("ResetCounter", this.resetCounter);
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.currentTick = nbt.func_74762_e("CurrentTick");
         this.isReset = nbt.func_74767_n("IsReset");
         this.resetCounter = nbt.func_74762_e("ResetCounter");
      }
   }
}
