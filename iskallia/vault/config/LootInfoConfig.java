package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.VaultMod;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class LootInfoConfig extends Config {
   @Expose
   @SerializedName("info")
   private Map<ResourceLocation, LootInfoConfig.LootInfo> lootInfoMap;
   private Map<ResourceLocation, Set<String>> displayNameCache = new HashMap<>();

   @Override
   public String getName() {
      return "loot_info";
   }

   @Override
   protected void reset() {
      this.lootInfoMap = new HashMap<>();
      this.displayNameCache.clear();
      this.lootInfoMap.put(VaultMod.id("coin_pile"), new LootInfoConfig.LootInfo("Coin Piles", List.of(VaultMod.id("coin_pile_lvl0"))));
      this.lootInfoMap
         .put(
            VaultMod.id("wooden_chest"),
            new LootInfoConfig.LootInfo("Wooden Chests", List.of(VaultMod.id("wooden_chest_lvl0"), VaultMod.id("wooden_chest_lvl10")))
         );
      this.lootInfoMap
         .put(
            VaultMod.id("ornate_chest"),
            new LootInfoConfig.LootInfo("Ornate Chests", List.of(VaultMod.id("ornate_chest_lvl0"), VaultMod.id("ornate_chest_lvl11")))
         );
      this.lootInfoMap
         .put(
            VaultMod.id("gilded_chest"),
            new LootInfoConfig.LootInfo("Gilded Chests", List.of(VaultMod.id("gilded_chest_lvl0"), VaultMod.id("gilded_chest_lvl10")))
         );
      this.lootInfoMap
         .put(
            VaultMod.id("living_chest"),
            new LootInfoConfig.LootInfo(
               "Living Chests", List.of(VaultMod.id("living_chest_lvl0"), VaultMod.id("living_chest_lvl9"), VaultMod.id("living_chest_lvl22"))
            )
         );
      this.lootInfoMap.put(VaultMod.id("treasure_sand"), new LootInfoConfig.LootInfo("Treasure Sand", List.of(VaultMod.id("treasure_sand_lvl0"))));
      this.lootInfoMap.put(VaultMod.id("cube_block"), new LootInfoConfig.LootInfo("Cube Blocks", List.of(VaultMod.id("cube_block_lvl0"))));
      this.lootInfoMap.put(VaultMod.id("altar_chest"), new LootInfoConfig.LootInfo("Altar Chests", List.of(VaultMod.id("altar_chest_lvl0"))));
      this.lootInfoMap.put(VaultMod.id("treasure_chest"), new LootInfoConfig.LootInfo("Treasure Chests", List.of(VaultMod.id("treasure_chest_lvl0"))));
      this.lootInfoMap
         .put(
            VaultMod.id("completion_crate"),
            new LootInfoConfig.LootInfo("Completion Crates", List.of(VaultMod.id("completion_crate_lvl0"), VaultMod.id("completion_crate_lvl20")))
         );
      this.lootInfoMap.put(VaultMod.id("wooden_chest_raw"), new LootInfoConfig.LootInfo("Raw Wooden Chests", List.of(VaultMod.id("wooden_chest_raw"))));
      this.lootInfoMap.put(VaultMod.id("ornate_chest_raw"), new LootInfoConfig.LootInfo("Raw Ornate Chests", List.of(VaultMod.id("ornate_chest_raw"))));
      this.lootInfoMap.put(VaultMod.id("gilded_chest_raw"), new LootInfoConfig.LootInfo("Raw Gilded Chests", List.of(VaultMod.id("gilded_chest_raw"))));
      this.lootInfoMap.put(VaultMod.id("living_chest_raw"), new LootInfoConfig.LootInfo("Raw Living Chests", List.of(VaultMod.id("living_chest_raw"))));
   }

   public Set<String> getDisplayNames(ResourceLocation lootTableKey) {
      Set<String> cachedResult = this.displayNameCache.get(lootTableKey);
      if (cachedResult != null) {
         return cachedResult;
      } else {
         Set<String> result = new HashSet<>();

         for (LootInfoConfig.LootInfo lootInfo : this.lootInfoMap.values()) {
            if (lootInfo.lootTableKeys.contains(lootTableKey)) {
               result.add(lootInfo.display);
            }
         }

         if (result.isEmpty()) {
            result.add(lootTableKey.toString());
         }

         this.displayNameCache.put(lootTableKey, result);
         return result;
      }
   }

   public static class LootInfo {
      @Expose
      @SerializedName("display")
      private final String display;
      @Expose
      @SerializedName("lootTableKeys")
      private final List<ResourceLocation> lootTableKeys;

      public LootInfo(String display, List<ResourceLocation> lootTableKeys) {
         this.display = display;
         this.lootTableKeys = lootTableKeys;
      }

      public String getDisplay() {
         return this.display;
      }

      public List<ResourceLocation> getLootTableKeys() {
         return this.lootTableKeys;
      }
   }
}
