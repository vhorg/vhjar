package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class VaultVendingConfig extends Config {
   @Expose
   public int MAX_CIRCUITS;
   @Expose
   public List<Trade> TRADES = new ArrayList<>();

   @Override
   public String getName() {
      return "vault_vending";
   }

   @Override
   protected void reset() {
      this.MAX_CIRCUITS = 16;
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
}
