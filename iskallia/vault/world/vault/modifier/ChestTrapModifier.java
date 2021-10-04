package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.VaultChestConfig;
import iskallia.vault.util.data.RandomListAccess;
import iskallia.vault.util.data.WeightedDoubleList;
import iskallia.vault.world.vault.chest.VaultChestEffect;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;

public class ChestTrapModifier extends TexturedVaultModifier {
   @Expose
   private final double chanceOfTrappedChests;

   public ChestTrapModifier(String name, ResourceLocation icon, double chanceOfTrappedChests) {
      super(name, icon);
      this.chanceOfTrappedChests = chanceOfTrappedChests;
   }

   public double getChanceOfTrappedChests() {
      return this.chanceOfTrappedChests;
   }

   @Nonnull
   public RandomListAccess<String> modifyWeightedList(VaultChestConfig config, RandomListAccess<String> chestOutcomes) {
      WeightedDoubleList<String> reWeightedList = new WeightedDoubleList<>();
      chestOutcomes.forEach((entry, weight) -> {
         VaultChestEffect effect = config.getEffectByName(entry);
         if (effect != null && effect.isTrapEffect()) {
            reWeightedList.add(entry, weight.doubleValue() * this.getChanceOfTrappedChests());
         } else {
            reWeightedList.add(entry, weight.doubleValue());
         }
      });
      return reWeightedList;
   }
}
