package iskallia.vault.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.Range;

public enum VaultDifficulty {
   EASY(0, 1, "easy", 0.5, 0.5, false, Range.is(1), 0.5, 0.75),
   NORMAL(1, 2, "normal", 0.75, 0.75, false, Range.between(1, 2), 0.75, 1.0),
   HARD(2, 3, "hard", 1.0, 1.0, true, Range.between(1, 3), 1.0, 1.0),
   IMPOSSIBLE(3, 4, "impossible", 2.0, 2.0, true, Range.between(2, 3), 1.5, 1.5),
   FRAGGED(4, 5, "fragged", 4.0, 2.0, true, Range.is(3), 2.0, 3.0),
   PIECE_OF_CAKE(5, 0, "piece_of_cake", 0.25, 0.25, false, Range.is(0), 0.25, 0.5);

   private static final VaultDifficulty[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(VaultDifficulty::getId))
      .toArray(VaultDifficulty[]::new);
   private final int id;
   private final int displayOrder;
   private final String key;
   private final double damageMultiplier;
   private final double heathMultiplier;
   private final boolean antiNerdPoleAi;
   private final Range<Integer> championAffixCount;
   private final double bossDamageMultiplier;
   private final double bossHealthMultiplier;
   private static final VaultDifficulty[] ORDERED_DIFFICULTIES = Arrays.stream(values())
      .sorted(Comparator.comparingInt(VaultDifficulty::getDisplayOrder))
      .toArray(VaultDifficulty[]::new);

   private VaultDifficulty(
      int id,
      int displayOrder,
      String key,
      double damageMultiplier,
      double heathMultiplier,
      boolean antiNerdPoleAi,
      Range<Integer> championAffixCount,
      double bossDamageMultiplier,
      double bossHealthMultiplier
   ) {
      this.id = id;
      this.displayOrder = displayOrder;
      this.key = key;
      this.damageMultiplier = damageMultiplier;
      this.heathMultiplier = heathMultiplier;
      this.antiNerdPoleAi = antiNerdPoleAi;
      this.championAffixCount = championAffixCount;
      this.bossDamageMultiplier = bossDamageMultiplier;
      this.bossHealthMultiplier = bossHealthMultiplier;
   }

   public int getId() {
      return this.id;
   }

   public Component getDisplayName() {
      return new TranslatableComponent("the_vault.options.difficulty." + this.key);
   }

   public static VaultDifficulty byId(int pId) {
      return BY_ID[pId % BY_ID.length];
   }

   public static VaultDifficulty[] getOrderedDifficulties() {
      return ORDERED_DIFFICULTIES;
   }

   private int getDisplayOrder() {
      return this.displayOrder;
   }

   public Range<Integer> getChampionAffixCount() {
      return this.championAffixCount;
   }

   @Nullable
   public static VaultDifficulty byName(String pName) {
      for (VaultDifficulty difficulty : values()) {
         if (difficulty.key.equals(pName)) {
            return difficulty;
         }
      }

      return null;
   }

   public boolean shouldAddAntiNerdPoleAi() {
      return this.antiNerdPoleAi;
   }

   public String getKey() {
      return this.key;
   }

   public double getDamageMultiplier() {
      return this.damageMultiplier;
   }

   public double getHeathMultiplier() {
      return this.heathMultiplier;
   }

   public double getBossDamageMultiplier() {
      return this.bossDamageMultiplier;
   }

   public double getBossHealthMultiplier() {
      return this.bossHealthMultiplier;
   }
}
