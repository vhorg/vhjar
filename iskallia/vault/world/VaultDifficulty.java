package iskallia.vault.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum VaultDifficulty {
   EASY(0, "easy", 0.5),
   NORMAL(1, "normal", 0.75),
   HARD(2, "hard", 1.0),
   IMPOSSIBLE(3, "impossible", 2.0);

   private static final VaultDifficulty[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(VaultDifficulty::getId))
      .toArray(VaultDifficulty[]::new);
   private final int id;
   private final String key;
   private final double mobDifficultyMultiplier;

   private VaultDifficulty(int id, String key, double mobDifficultyMultiplier) {
      this.id = id;
      this.key = key;
      this.mobDifficultyMultiplier = mobDifficultyMultiplier;
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

   @Nullable
   public static VaultDifficulty byName(String pName) {
      for (VaultDifficulty difficulty : values()) {
         if (difficulty.key.equals(pName)) {
            return difficulty;
         }
      }

      return null;
   }

   public String getKey() {
      return this.key;
   }

   public double getMobDifficultyMultiplier() {
      return this.mobDifficultyMultiplier;
   }
}
