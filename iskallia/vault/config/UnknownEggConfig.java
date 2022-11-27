package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class UnknownEggConfig extends Config {
   @Expose
   private LevelEntryList<UnknownEggConfig.Level> OVERRIDES = new LevelEntryList<>();

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
               new WeightedList<String>()
                  .add(Items.ZOMBIE_SPAWN_EGG.getRegistryName().toString(), 2)
                  .add(Items.SKELETON_SPAWN_EGG.getRegistryName().toString(), 1)
            )
         );
   }

   @Nullable
   public UnknownEggConfig.Level getForLevel(int level) {
      return this.OVERRIDES.getForLevel(level).orElse(null);
   }

   public static class Level implements LevelEntryList.ILevelEntry {
      @Expose
      public int MIN_LEVEL;
      @Expose
      public WeightedList<String> EGG_POOL;

      public Level(int level, WeightedList<String> pool) {
         this.MIN_LEVEL = level;
         this.EGG_POOL = pool;
      }

      @Override
      public int getLevel() {
         return this.MIN_LEVEL;
      }
   }
}
