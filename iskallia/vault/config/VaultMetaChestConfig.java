package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.util.data.WeightedDoubleList;
import iskallia.vault.util.data.WeightedList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class VaultMetaChestConfig extends Config {
   @Expose
   private final Map<String, Map<String, Float>> catalystChances = new HashMap<>();
   @Expose
   private final Map<String, Float> pityWeight = new HashMap<>();

   @Override
   public String getName() {
      return "vault_chest_meta";
   }

   public float getCatalystChance(ResourceLocation chestKey, VaultRarity chestRarity) {
      return this.catalystChances.getOrDefault(chestKey.toString(), Collections.emptyMap()).getOrDefault(chestRarity.name(), 0.0F);
   }

   public WeightedDoubleList<String> getPityAdjustedRarity(WeightedList<String> chestWeights, int ticksSinceLastChest) {
      float multiplier = ticksSinceLastChest / 1200.0F;
      WeightedDoubleList<String> adjusted = new WeightedDoubleList<>();
      chestWeights.forEach((rarityKey, weight) -> {
         float modifier = this.pityWeight.getOrDefault(rarityKey, 1.0F);
         float newWeight = weight.floatValue() + weight.floatValue() * modifier * multiplier;
         if (newWeight > 0.0F) {
            adjusted.add(rarityKey, newWeight);
         }
      });
      return adjusted;
   }

   @Override
   protected void reset() {
      this.pityWeight.clear();
      this.pityWeight.put(VaultRarity.COMMON.name(), -0.2F);
      this.pityWeight.put(VaultRarity.RARE.name(), -0.14F);
      this.pityWeight.put(VaultRarity.EPIC.name(), 0.1F);
      this.pityWeight.put(VaultRarity.OMEGA.name(), 0.3F);
      this.catalystChances.clear();
      this.setupEmptyChances(ModBlocks.VAULT_CHEST);
      this.setupEmptyChances(ModBlocks.VAULT_ALTAR_CHEST);
      this.setupEmptyChances(ModBlocks.VAULT_TREASURE_CHEST);
      this.setupEmptyChances(ModBlocks.VAULT_COOP_CHEST);
      this.setupEmptyChances(ModBlocks.VAULT_BONUS_CHEST);
      Map<String, Float> chestChances = this.catalystChances.get(ModBlocks.VAULT_CHEST.getRegistryName().toString());
      chestChances.put(VaultRarity.RARE.name(), 0.1F);
      chestChances.put(VaultRarity.EPIC.name(), 0.4F);
      chestChances.put(VaultRarity.OMEGA.name(), 0.5F);
      chestChances = this.catalystChances.get(ModBlocks.VAULT_ALTAR_CHEST.getRegistryName().toString());

      for (VaultRarity rarity : VaultRarity.values()) {
         chestChances.put(rarity.name(), 1.0F);
      }
   }

   private void setupEmptyChances(Block block) {
      Map<String, Float> chances = new HashMap<>();

      for (VaultRarity rarity : VaultRarity.values()) {
         chances.put(rarity.name(), 0.0F);
      }

      this.catalystChances.put(block.getRegistryName().toString(), chances);
   }
}
