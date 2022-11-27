package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.VaultRarity;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;

public class VaultMetaChestConfig extends Config {
   @Expose
   private final Map<Block, Map<VaultRarity, Double>> catalystChances = new HashMap<>();

   @Override
   public String getName() {
      return "vault_chest_meta";
   }

   public double getCatalystChance(Block block, VaultRarity rarity) {
      return this.catalystChances.getOrDefault(block, Collections.emptyMap()).getOrDefault(rarity, 0.0);
   }

   @Override
   protected void reset() {
      this.catalystChances.clear();
      this.set(ModBlocks.WOODEN_CHEST, 0.0, this.catalystChances);
      this.set(ModBlocks.GILDED_CHEST, 0.2F, this.catalystChances);
      this.set(ModBlocks.LIVING_CHEST, 0.0, this.catalystChances);
      this.set(ModBlocks.ORNATE_CHEST, 0.5, this.catalystChances);
      this.set(ModBlocks.ALTAR_CHEST, 0.7F, this.catalystChances);
      this.set(ModBlocks.TREASURE_CHEST, 0.5, this.catalystChances);
   }

   private void set(Block block, double chance, Map<Block, Map<VaultRarity, Double>> mapOut) {
      for (VaultRarity value : VaultRarity.values()) {
         mapOut.computeIfAbsent(block, block1 -> new LinkedHashMap<>()).put(value, chance);
      }
   }
}
