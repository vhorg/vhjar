package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.Map;

public class DurabilityConfig extends Config {
   @Expose
   private final Map<Integer, Float> durabilityOverride = new HashMap<>();
   @Expose
   private final Map<Integer, Float> armorDurabilityOverride = new HashMap<>();

   @Override
   public String getName() {
      return "durability";
   }

   public float getDurabilityIgnoreChance(int unbreakingLevel) {
      return this.getIgnoreChance(this.durabilityOverride, unbreakingLevel);
   }

   public float getArmorDurabilityIgnoreChance(int unbreakingLevel) {
      return this.getIgnoreChance(this.armorDurabilityOverride, unbreakingLevel);
   }

   private float getIgnoreChance(Map<Integer, Float> chanceMap, int unbreakingLevel) {
      if (unbreakingLevel < 1) {
         return 0.0F;
      } else {
         int overrideLevel = chanceMap.keySet().stream().filter(level -> level <= unbreakingLevel).mapToInt(level -> level).max().orElse(0);
         return overrideLevel <= 0 ? 0.0F : chanceMap.get(overrideLevel);
      }
   }

   @Override
   protected void reset() {
      this.durabilityOverride.clear();
      this.armorDurabilityOverride.clear();
      this.durabilityOverride.put(1, 0.5F);
      this.durabilityOverride.put(2, 0.66667F);
      this.durabilityOverride.put(3, 0.75F);
      this.durabilityOverride.put(4, 0.78F);
      this.durabilityOverride.put(5, 0.8F);
      this.durabilityOverride.put(6, 0.82F);
      this.durabilityOverride.put(7, 0.84F);
      this.durabilityOverride.put(8, 0.86F);
      this.durabilityOverride.put(9, 0.88F);
      this.durabilityOverride.put(10, 0.9F);
      this.armorDurabilityOverride.put(1, 0.2F);
      this.armorDurabilityOverride.put(2, 0.27F);
      this.armorDurabilityOverride.put(3, 0.3F);
      this.armorDurabilityOverride.put(4, 0.33F);
      this.armorDurabilityOverride.put(5, 0.36F);
      this.armorDurabilityOverride.put(6, 0.39F);
      this.armorDurabilityOverride.put(7, 0.42F);
      this.armorDurabilityOverride.put(8, 0.45F);
      this.armorDurabilityOverride.put(9, 0.48F);
      this.armorDurabilityOverride.put(10, 0.51F);
   }
}
