package iskallia.vault.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum VaultDifficulty {
   EASY(0, 1, "easy", 0.5, 0.5, false),
   NORMAL(1, 2, "normal", 0.75, 0.75, false),
   HARD(2, 3, "hard", 1.0, 1.0, true),
   IMPOSSIBLE(3, 4, "impossible", 2.0, 2.0, true),
   FRAGGED(4, 5, "fragged", 4.0, 2.0, true),
   PIECE_OF_CAKE(5, 0, "piece_of_cake", 0.25, 0.25, false);

   private static final VaultDifficulty[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(VaultDifficulty::getId))
      .toArray(VaultDifficulty[]::new);
   private final int id;
   private final int displayOrder;
   private final String key;
   private final double damageMultiplier;
   private final double heathMultiplier;
   private final boolean antiNerdPoleAi;
   private static final VaultDifficulty[] ORDERED_DIFFICULTIES = Arrays.stream(values())
      .sorted(Comparator.comparingInt(VaultDifficulty::getDisplayOrder))
      .toArray(VaultDifficulty[]::new);

   private VaultDifficulty(int id, int displayOrder, String key, double damageMultiplier, double heathMultiplier, boolean antiNerdPoleAi) {
      this.id = id;
      this.displayOrder = displayOrder;
      this.key = key;
      this.damageMultiplier = damageMultiplier;
      this.heathMultiplier = heathMultiplier;
      this.antiNerdPoleAi = antiNerdPoleAi;
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
}
