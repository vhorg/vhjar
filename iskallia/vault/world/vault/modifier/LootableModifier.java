package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.data.RandomListAccess;
import iskallia.vault.util.data.WeightedDoubleList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

public class LootableModifier extends TexturedVaultModifier {
   @Expose
   private final String pool;
   @Expose
   private final Map<String, Float> resultMultipliers = new HashMap<>();

   public LootableModifier(String name, ResourceLocation icon, String affectedPool, Map<String, Float> resultMultipliers) {
      super(name, icon);
      this.pool = affectedPool;
      this.resultMultipliers.putAll(resultMultipliers);
   }

   public RandomListAccess<String> adjustLootWeighting(String pool, RandomListAccess<String> weightedList) {
      if (pool.equalsIgnoreCase(this.pool)) {
         WeightedDoubleList<String> resultList = new WeightedDoubleList<>();
         weightedList.forEach((entry, weight) -> resultList.add(entry, weight.doubleValue() * this.resultMultipliers.getOrDefault(entry, 1.0F).floatValue()));
         return resultList;
      } else {
         return weightedList;
      }
   }

   public static Map<String, Float> getDefaultOreModifiers(float multiplier) {
      Map<String, Float> oreResults = new HashMap<>();
      oreResults.put(ModBlocks.BENITOITE_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.LARIMAR_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.ALEXANDRITE_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.WUTODIE_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.PAINITE_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.BLACK_OPAL_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.ISKALLIUM_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.PUFFIUM_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.GORGINITE_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.SPARKLETINE_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.ASHIUM_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.BOMIGNITE_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.FUNSOIDE_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.TUBIUM_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.UPALINE_ORE.getRegistryName().toString(), multiplier);
      oreResults.put(ModBlocks.ECHO_ORE.getRegistryName().toString(), multiplier);
      return oreResults;
   }
}
