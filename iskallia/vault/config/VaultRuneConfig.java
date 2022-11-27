package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.data.WeightedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultRuneConfig extends Config {
   @Expose
   private final WeightedList<String> runeWeights = new WeightedList<>();
   @Expose
   private final List<VaultRuneConfig.RuneLevel> runeLevels = new ArrayList<>();

   public Item getRandomRune() {
      return (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.runeWeights.getRandom(rand)));
   }

   public Optional<Integer> getMinimumLevel(Item item) {
      String itemRegistryName = item.getRegistryName().toString();

      for (VaultRuneConfig.RuneLevel runeLevel : this.runeLevels) {
         if (runeLevel.item.equals(itemRegistryName)) {
            return Optional.of(runeLevel.minLevel);
         }
      }

      return Optional.empty();
   }

   @Override
   public String getName() {
      return "vault_rune";
   }

   @Override
   protected void reset() {
      this.runeWeights.clear();
      this.runeLevels.clear();
   }

   public static class RuneLevel {
      @Expose
      private String item;
      @Expose
      private int minLevel;

      public RuneLevel(String item, int minLevel) {
         this.item = item;
         this.minLevel = minLevel;
      }

      public String getItem() {
         return this.item;
      }

      public int getMinLevel() {
         return this.minLevel;
      }
   }
}
