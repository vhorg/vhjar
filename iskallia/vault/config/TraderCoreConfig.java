package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.config.entry.vending.TradeEntry;
import iskallia.vault.util.data.WeightedList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Items;

public abstract class TraderCoreConfig extends Config {
   public static class Level {
      @Expose
      private final int level;
      @Expose
      private final WeightedList<TradeEntry> trades;

      public Level(int level, WeightedList<TradeEntry> trades) {
         this.level = level;
         this.trades = trades;
      }
   }

   public static class TraderCoreCommonConfig extends TraderCoreConfig {
      @Expose
      private final List<TraderCoreConfig.Level> levels = new ArrayList<>();

      @Override
      public String getName() {
         return "trader_core_common";
      }

      @Override
      protected void reset() {
         WeightedList<TradeEntry> options = new WeightedList<>();
         options.add(new TradeEntry(new ProductEntry(Items.field_151034_e, 8, 8, null), new ProductEntry(Items.field_151153_ao, 1, 1, null), 3), 1);
         this.levels.add(new TraderCoreConfig.Level(0, options));
         options = new WeightedList<>();
         options.add(new TradeEntry(new ProductEntry(Items.field_151172_bF, 8, 8, null), new ProductEntry(Items.field_151150_bK, 1, 1, null), 3), 1);
         this.levels.add(new TraderCoreConfig.Level(25, options));
      }

      @Nullable
      public TradeEntry getRandomTrade(int vaultLevel) {
         TraderCoreConfig.Level levelConfig = this.getForLevel(this.levels, vaultLevel);
         return levelConfig == null ? null : levelConfig.trades.getRandom(rand);
      }

      @Nullable
      public TraderCoreConfig.Level getForLevel(List<TraderCoreConfig.Level> levels, int level) {
         for (int i = 0; i < levels.size(); i++) {
            if (level < levels.get(i).level) {
               if (i != 0) {
                  return levels.get(i - 1);
               }
               break;
            }

            if (i == levels.size() - 1) {
               return levels.get(i);
            }
         }

         return null;
      }
   }
}
