package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.item.Item;

public class SpiritConfig extends Config {
   @Expose
   public double recoveryCostMultiplier;
   @Expose
   public Set<SpiritConfig.LevelCost> levelCosts;

   @Override
   public String getName() {
      return "spirit";
   }

   @Override
   protected void reset() {
      this.recoveryCostMultiplier = 1.05;
      this.levelCosts = new HashSet<>();
      this.levelCosts.add(new SpiritConfig.LevelCost(0, ModBlocks.VAULT_BRONZE, 20));
      this.levelCosts.add(new SpiritConfig.LevelCost(5, ModBlocks.VAULT_SILVER, 3));
   }

   public static class LevelCost {
      @Expose
      public int minLevel;
      @Expose
      public Item item;
      @Expose
      public int count;

      public LevelCost(int minLevel, Item item, int count) {
         this.minLevel = minLevel;
         this.item = item;
         this.count = count;
      }
   }
}
