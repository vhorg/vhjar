package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.stat.ChestStat;
import iskallia.vault.core.vault.stat.MinedBlocksStat;
import iskallia.vault.core.vault.stat.MobsStat;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModEntities;
import iskallia.vault.util.VaultRarity;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;

public class VaultStatsConfig extends Config {
   @Expose
   private Map<VaultChestType, Map<VaultRarity, Float>> chests;
   @Expose
   private Map<ResourceLocation, Float> blocksMined;
   @Expose
   private float treasureRoomsOpened;
   @Expose
   private Map<ResourceLocation, Float> mobsKilled;
   @Expose
   private Map<String, Map<Completion, Float>> completion;
   @Expose
   private float percentOfExperienceDealtAsDurabilityDamage;
   @Expose
   private int freeExperienceNotDealtAsDurabilityDamage;

   @Override
   public String getName() {
      return "vault_stats";
   }

   @Override
   protected void reset() {
      this.chests = new LinkedHashMap<>();
      Map<VaultRarity, Float> chest = new LinkedHashMap<>();
      chest.put(VaultRarity.COMMON, 1.0F);
      chest.put(VaultRarity.RARE, 2.0F);
      chest.put(VaultRarity.EPIC, 4.0F);
      chest.put(VaultRarity.OMEGA, 10.0F);

      for (VaultChestType type : VaultChestType.values()) {
         this.chests.put(type, chest);
      }

      this.blocksMined = new LinkedHashMap<>();
      this.blocksMined.put(ModBlocks.TREASURE_SAND.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.COIN_PILE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.ALEXANDRITE_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.BENITOITE_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.LARIMAR_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.BLACK_OPAL_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.PAINITE_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.ISKALLIUM_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.GORGINITE_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.SPARKLETINE_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.WUTODIE_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.ASHIUM_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.BOMIGNITE_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.TUBIUM_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.UPALINE_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.PUFFIUM_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.PETZANITE_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.XENIUM_ORE.getRegistryName(), 1.0F);
      this.blocksMined.put(ModBlocks.ECHO_ORE.getRegistryName(), 1.0F);
      this.treasureRoomsOpened = 50.0F;
      this.mobsKilled = new LinkedHashMap<>();
      this.mobsKilled.put(new ResourceLocation("default"), 0.3F);
      this.mobsKilled.put(ModEntities.ELITE_SPIDER.getRegistryName(), 2.0F);
      this.completion = new LinkedHashMap<>();
      LinkedHashMap<Completion, Float> defaultPool = new LinkedHashMap<>();
      defaultPool.put(Completion.BAILED, Float.valueOf(0.0F));
      defaultPool.put(Completion.FAILED, Float.valueOf(0.0F));
      defaultPool.put(Completion.COMPLETED, Float.valueOf(1000.0F));
      this.completion.put("default", defaultPool);
      this.percentOfExperienceDealtAsDurabilityDamage = 0.2F;
      this.freeExperienceNotDealtAsDurabilityDamage = 400;
   }

   public int getStatsExperience(StatCollector stats) {
      float exp = 0.0F;

      for (ChestStat chestStat : stats.get(StatCollector.CHESTS)) {
         if (!chestStat.has(ChestStat.TRAPPED)) {
            exp += this.chests.get(chestStat.get(ChestStat.TYPE)).get(chestStat.get(ChestStat.RARITY));
         }
      }

      for (Entry<ResourceLocation, MinedBlocksStat.Entry> entry : stats.get(StatCollector.MINED_BLOCKS).entrySet()) {
         float defaultValue = this.blocksMined.getOrDefault(new ResourceLocation("default"), 0.0F);
         exp += this.blocksMined.getOrDefault(entry.getKey(), defaultValue) * entry.getValue().get(MinedBlocksStat.Entry.COUNT).intValue();
      }

      exp += this.treasureRoomsOpened * stats.get(StatCollector.TREASURE_ROOMS_OPENED).intValue();

      for (Entry<ResourceLocation, MobsStat.Entry> entry : stats.get(StatCollector.MOBS).entrySet()) {
         float defaultValue = this.mobsKilled.getOrDefault(new ResourceLocation("default"), 0.0F);
         exp += this.mobsKilled.getOrDefault(entry.getKey(), defaultValue) * entry.getValue().get(MobsStat.Entry.KILLED).intValue();
      }

      return (int)exp;
   }

   public int getExperience(Vault vault, StatCollector stats) {
      float exp = 0.0F;
      exp += this.getStatsExperience(stats);
      exp += this.getCompletion(vault).get(stats.getCompletion());
      return (int)exp;
   }

   public int getCompletionExperience(Vault vault, StatCollector stats) {
      float exp = this.getCompletion(vault).get(stats.getCompletion());
      return (int)exp;
   }

   public Map<ResourceLocation, Float> getBlocksMined() {
      return this.blocksMined;
   }

   public Map<ResourceLocation, Float> getMobsKilled() {
      return this.mobsKilled;
   }

   public Map<Completion, Float> getCompletion(Vault vault) {
      String pool = vault.getOptional(Vault.OBJECTIVES).flatMap(objectives -> objectives.getOptional(Objectives.KEY)).orElse("default");
      return !this.completion.containsKey(pool) ? this.completion.get("default") : this.completion.get(pool);
   }

   public float getTreasureRoomsOpened() {
      return this.treasureRoomsOpened;
   }

   public Map<VaultChestType, Map<VaultRarity, Float>> getChests() {
      return this.chests;
   }

   public float getPercentOfExperienceDealtAsDurabilityDamage() {
      return this.percentOfExperienceDealtAsDurabilityDamage;
   }

   public int getFreeExperienceNotDealtAsDurabilityDamage() {
      return this.freeExperienceNotDealtAsDurabilityDamage;
   }
}
