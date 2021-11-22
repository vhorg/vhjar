package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.data.WeightedList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class EtchingConfig extends Config {
   @Expose
   public Map<VaultGear.Set, EtchingConfig.Etching> ETCHINGS;

   @Override
   public String getName() {
      return "etching";
   }

   public VaultGear.Set getRandomSet() {
      return this.getRandomSet(new Random());
   }

   public VaultGear.Set getRandomSet(Random random) {
      WeightedList<VaultGear.Set> list = new WeightedList<>();
      this.ETCHINGS.forEach((set, etching) -> {
         if (set != VaultGear.Set.NONE) {
            list.add(set, etching.weight);
         }
      });
      return list.getRandom(random);
   }

   public EtchingConfig.Etching getFor(VaultGear.Set set) {
      return this.ETCHINGS.get(set);
   }

   @Override
   protected void reset() {
      this.ETCHINGS = new LinkedHashMap<>();

      for (VaultGear.Set set : VaultGear.Set.values()) {
         this.ETCHINGS.put(set, new EtchingConfig.Etching(1, 1, 2, "yes", 5636095));
      }
   }

   public static class Etching {
      @Expose
      public int weight;
      @Expose
      public int minValue;
      @Expose
      public int maxValue;
      @Expose
      public String effectText;
      @Expose
      public int color;

      public Etching(int weight, int minValue, int maxValue, String effectText, int color) {
         this.weight = weight;
         this.minValue = minValue;
         this.maxValue = maxValue;
         this.effectText = effectText;
         this.color = color;
      }
   }
}
