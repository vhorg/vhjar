package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.VaultRarity;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;

public class VaultMetaChestConfig extends Config {
   @Expose
   private int catalystMinLevel;
   @Expose
   private final Map<Block, Map<VaultRarity, Double>> catalystChances = new LinkedHashMap<>();
   @Expose
   private final Map<Block, Map<VaultRarity, Double>> runeChances = new LinkedHashMap<>();

   @Override
   public String getName() {
      return "vault_chest_meta";
   }

   public double getCatalystChance(Block block, VaultRarity rarity) {
      return this.catalystChances.getOrDefault(block, Collections.emptyMap()).getOrDefault(rarity, 0.0);
   }

   public double getRuneChance(Block block, VaultRarity rarity) {
      return this.runeChances.getOrDefault(block, Collections.emptyMap()).getOrDefault(rarity, 0.0);
   }

   public int getCatalystMinLevel() {
      return this.catalystMinLevel;
   }

   @Override
   protected void reset() {
      this.catalystMinLevel = 25;
      this.catalystChances.clear();
      this.set(ModBlocks.WOODEN_CHEST, 0.0, this.catalystChances);
      this.set(ModBlocks.GILDED_CHEST, 0.2F, this.catalystChances);
      this.set(ModBlocks.LIVING_CHEST, 0.0, this.catalystChances);
      this.set(ModBlocks.ORNATE_CHEST, 0.5, this.catalystChances);
      this.set(ModBlocks.ALTAR_CHEST, 0.7F, this.catalystChances);
      this.set(ModBlocks.TREASURE_CHEST, 0.5, this.catalystChances);
      this.set(ModBlocks.ORNATE_STRONGBOX, 0.5, this.catalystChances);
      this.set(ModBlocks.GILDED_STRONGBOX, 0.2F, this.catalystChances);
      this.set(ModBlocks.LIVING_STRONGBOX, 0.0, this.catalystChances);
      this.runeChances.clear();
      this.set(ModBlocks.WOODEN_CHEST, 0.0, this.runeChances);
      this.set(ModBlocks.GILDED_CHEST, 0.2F, this.runeChances);
      this.set(ModBlocks.LIVING_CHEST, 0.0, this.runeChances);
      this.set(ModBlocks.ORNATE_CHEST, 0.5, this.runeChances);
      this.set(ModBlocks.ALTAR_CHEST, 0.7F, this.runeChances);
      this.set(ModBlocks.TREASURE_CHEST, 0.5, this.runeChances);
      this.set(ModBlocks.ORNATE_STRONGBOX, 0.5, this.runeChances);
      this.set(ModBlocks.GILDED_STRONGBOX, 0.2F, this.runeChances);
      this.set(ModBlocks.LIVING_STRONGBOX, 0.0, this.runeChances);
   }

   private void set(Block block, double chance, Map<Block, Map<VaultRarity, Double>> mapOut) {
      for (VaultRarity value : VaultRarity.values()) {
         mapOut.computeIfAbsent(block, block1 -> new LinkedHashMap<>()).put(value, chance);
      }
   }
}
