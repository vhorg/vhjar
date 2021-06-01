package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class TraderCoreConfig extends Config {
   @Expose
   public List<Trade> TRADES = new ArrayList<>();

   @Override
   public String getName() {
      return "trader_core";
   }

   @Override
   protected void reset() {
      this.TRADES.add(new Trade(new Product(Items.field_151034_e, 8, null), null, new Product(Items.field_151153_ao, 1, null)));
      this.TRADES.add(new Trade(new Product(Items.field_151153_ao, 8, null), null, new Product(Items.field_196100_at, 1, null)));
      CompoundNBT nbt = new CompoundNBT();
      ListNBT enchantments = new ListNBT();
      CompoundNBT knockback = new CompoundNBT();
      knockback.func_74778_a("id", "minecraft:knockback");
      knockback.func_74768_a("lvl", 10);
      enchantments.add(knockback);
      nbt.func_218657_a("Enchantments", enchantments);
      nbt.func_218657_a("ench", enchantments);
      this.TRADES.add(new Trade(new Product(Items.field_196100_at, 8, null), null, new Product(Items.field_151055_y, 1, nbt)));
   }

   public static class TraderCoreCommonConfig extends TraderCoreConfig {
      @Override
      public String getName() {
         return "trader_core_common";
      }
   }

   public static class TraderCoreOmegaConfig extends TraderCoreConfig {
      @Override
      public String getName() {
         return "trader_core_omega";
      }
   }

   public static class TraderCoreRaffleConfig extends TraderCoreConfig {
      @Override
      public String getName() {
         return "trader_core_raffle";
      }
   }
}
