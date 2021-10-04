package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.data.WeightedList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Items;

public class UnknownEggConfig extends Config {
   @Expose
   private List<UnknownEggConfig.Level> OVERRIDES = new ArrayList<>();

   @Override
   public String getName() {
      return "unknown_egg";
   }

   @Override
   protected void reset() {
      this.OVERRIDES
         .add(
            new UnknownEggConfig.Level(
               0,
               new WeightedList<String>().add(Items.field_196177_df.getRegistryName().toString(), 2).add(Items.field_196138_cT.getRegistryName().toString(), 1)
            )
         );
   }

   public UnknownEggConfig.Level getForLevel(int level) {
      for (int i = 0; i < this.OVERRIDES.size(); i++) {
         if (level < this.OVERRIDES.get(i).MIN_LEVEL) {
            if (i != 0) {
               return this.OVERRIDES.get(i - 1);
            }
            break;
         }

         if (i == this.OVERRIDES.size() - 1) {
            return this.OVERRIDES.get(i);
         }
      }

      return null;
   }

   public static class Level {
      @Expose
      public int MIN_LEVEL;
      @Expose
      public WeightedList<String> EGG_POOL;

      public Level(int level, WeightedList<String> pool) {
         this.MIN_LEVEL = level;
         this.EGG_POOL = pool;
      }
   }
}
