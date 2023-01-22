package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.entity.guardian.GuardianStats;
import iskallia.vault.entity.entity.guardian.GuardianType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VaultGuardianConfig extends Config {
   @Expose
   private LevelEntryList<VaultGuardianConfig.Entry> entries;

   @Override
   public String getName() {
      return "vault_guardian";
   }

   public GuardianStats get(int level, GuardianType type) {
      return (GuardianStats)(switch (type) {
         case BRUISER -> this.entries.getForLevel(level).get().bruiser;
         case ARBALIST -> this.entries.getForLevel(level).get().arbalist;
         default -> null;
      });
   }

   public GuardianType getType(int level, RandomSource random) {
      return this.entries.getForLevel(level).get().pool.getRandom(random).orElse(GuardianType.ARBALIST);
   }

   @Override
   protected void reset() {
      this.entries = new LevelEntryList<>();
      this.entries
         .add(
            new VaultGuardianConfig.Entry(
               0,
               new WeightedList<GuardianType>().add(GuardianType.BRUISER, 1).add(GuardianType.ARBALIST, 1),
               new GuardianStats.Bruiser(new ItemStack(Items.GOLDEN_AXE)),
               new GuardianStats.Arbalist(
                  new ItemStack(Items.STICK),
                  new ItemStack(Items.CROSSBOW),
                  GuardianStats.Arbalist.MeleeActivation.ON_HIT,
                  Math.sqrt(7.36F) + 1.0,
                  Math.sqrt(7.36F) + 4.0,
                  1.0
               )
            )
         );
   }

   public static class Entry implements LevelEntryList.ILevelEntry {
      @Expose
      private int level;
      @Expose
      private WeightedList<GuardianType> pool;
      @Expose
      private GuardianStats.Bruiser bruiser;
      @Expose
      private GuardianStats.Arbalist arbalist;

      public Entry(int level, WeightedList<GuardianType> pool, GuardianStats.Bruiser bruiser, GuardianStats.Arbalist arbalist) {
         this.level = level;
         this.pool = pool;
         this.bruiser = bruiser;
         this.arbalist = arbalist;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }
}
