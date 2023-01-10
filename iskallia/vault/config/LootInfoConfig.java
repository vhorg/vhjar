package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.VaultMod;
import iskallia.vault.core.world.loot.LootTableInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LootInfoConfig extends Config {
   @Expose
   @SerializedName("excludeFromTooltip")
   private Set<ResourceLocation> excludeFromTooltipSet = new HashSet<>();
   @Expose
   @SerializedName("info")
   private Map<ResourceLocation, LootInfoConfig.LootInfo> lootInfoMap = new HashMap<>();
   private LootInfoConfig.TooltipLinesProvider tooltipLinesProvider;

   @Override
   public String getName() {
      return "loot_info";
   }

   @Override
   public <T extends Config> T readConfig() {
      LootInfoConfig config = super.readConfig();
      config.tooltipLinesProvider = new LootInfoConfig.TooltipLinesProvider(config.lootInfoMap, config.excludeFromTooltipSet);
      return (T)config;
   }

   @Override
   protected void reset() {
      this.lootInfoMap = new HashMap<>();
      this.lootInfoMap
         .put(VaultMod.id("coin_pile"), new LootInfoConfig.LootInfo("Coin Piles", Map.of(VaultMod.id("coin_pile_lvl0"), new LootInfoConfig.LootTableData(0))));
   }

   public Set<String> getTooltipLines(Item item) {
      return this.tooltipLinesProvider.getTooltipLines(item);
   }

   public Map<ResourceLocation, LootInfoConfig.LootInfo> getLootInfoMap() {
      return Collections.unmodifiableMap(this.lootInfoMap);
   }

   public static class LootInfo {
      @Expose
      @SerializedName("display")
      private final String display;
      @Expose
      @SerializedName("lootTableKeys")
      private final Map<ResourceLocation, LootInfoConfig.LootTableData> lootTableKeys;

      public LootInfo(String display, Map<ResourceLocation, LootInfoConfig.LootTableData> lootTableKeys) {
         this.display = display;
         this.lootTableKeys = lootTableKeys;
      }

      public String getDisplay() {
         return this.display;
      }

      public Set<ResourceLocation> getLootTableKeys() {
         return this.lootTableKeys.keySet();
      }
   }

   public static class LootTableData {
      @Expose
      @SerializedName("level")
      private final int level;

      public LootTableData(int level) {
         this.level = level;
      }

      public int getLevel() {
         return this.level;
      }
   }

   private static class TooltipLinesProvider {
      private final Map<ResourceLocation, LootInfoConfig.LootInfo> lootInfoMap;
      private final Set<ResourceLocation> excludeFromTooltipSet;
      private final Map<ResourceLocation, Set<String>> tooltipLinesCache;

      public TooltipLinesProvider(Map<ResourceLocation, LootInfoConfig.LootInfo> lootInfoMap, Set<ResourceLocation> excludeFromTooltipSet) {
         this.lootInfoMap = lootInfoMap;
         this.excludeFromTooltipSet = excludeFromTooltipSet;
         this.tooltipLinesCache = new HashMap<>();
      }

      public Set<String> getTooltipLines(Item item) {
         ResourceLocation itemResourceLocation = item.getRegistryName();
         if (itemResourceLocation == null) {
            return Collections.emptySet();
         } else {
            Set<String> cachedResult = this.tooltipLinesCache.get(itemResourceLocation);
            if (cachedResult != null) {
               return cachedResult;
            } else {
               Set<String> calculatedResult = LootTableInfo.getLootTableKeysForItem(itemResourceLocation)
                  .stream()
                  .filter(this::isLootTableKeyDisplayAllowed)
                  .flatMap(this::getLootInfoGroupKeysForLootTableKey)
                  .distinct()
                  .map(lootInfoGroupKey -> this.getTooltipLineForLootInfoGroup(lootInfoGroupKey, itemResourceLocation))
                  .filter(Objects::nonNull)
                  .collect(Collectors.toCollection(TreeSet::new));
               this.tooltipLinesCache.put(itemResourceLocation, calculatedResult);
               return calculatedResult;
            }
         }
      }

      @Nullable
      private String getTooltipLineForLootInfoGroup(ResourceLocation lootInfoGroupKey, ResourceLocation itemResourceLocation) {
         List<LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData> list = this.lootInfoMap.get(lootInfoGroupKey)
            .lootTableKeys
            .entrySet()
            .stream()
            .map(
               entry -> new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(
                  entry.getValue().getLevel(), this.doesLootTableContainItem(entry.getKey(), itemResourceLocation)
               )
            )
            .sorted()
            .toList();
         return this.getTooltipLineForLootInfoGroup(this.lootInfoMap.get(lootInfoGroupKey).getDisplay(), list);
      }

      public static void main(String[] args) {
         LootInfoConfig.TooltipLinesProvider tooltipLinesProvider = new LootInfoConfig.TooltipLinesProvider(Collections.emptyMap(), Collections.emptySet());
         System.out.println("Levels: 0");
         System.out
            .println(tooltipLinesProvider.getTooltipLineForLootInfoGroup("+", List.of(new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(0, true))));
         System.out.println();
         System.out.println("Levels: 0/1");
         System.out
            .println(
               tooltipLinesProvider.getTooltipLineForLootInfoGroup(
                  "+ -",
                  List.of(
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(0, true),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(1, false)
                  )
               )
            );
         System.out.println();
         System.out.println("Levels: 0/10/20");
         System.out
            .println(
               tooltipLinesProvider.getTooltipLineForLootInfoGroup(
                  "+ - -",
                  List.of(
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(0, true),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(10, false),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(20, false)
                  )
               )
            );
         System.out.println();
         System.out.println("Levels: 0/10/20");
         System.out
            .println(
               tooltipLinesProvider.getTooltipLineForLootInfoGroup(
                  "- + +",
                  List.of(
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(0, false),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(10, true),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(20, true)
                  )
               )
            );
         System.out.println();
         System.out.println("Levels: 0/10/20");
         System.out
            .println(
               tooltipLinesProvider.getTooltipLineForLootInfoGroup(
                  "+ + -",
                  List.of(
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(0, true),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(10, true),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(20, false)
                  )
               )
            );
         System.out.println();
         System.out.println("Levels: 0/10/20");
         System.out
            .println(
               tooltipLinesProvider.getTooltipLineForLootInfoGroup(
                  "+ - +",
                  List.of(
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(0, true),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(10, false),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(20, true)
                  )
               )
            );
         System.out.println();
         System.out.println("Levels: 0/10/20/30/40/50");
         System.out
            .println(
               tooltipLinesProvider.getTooltipLineForLootInfoGroup(
                  "+ - + + - +",
                  List.of(
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(0, true),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(10, false),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(20, true),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(30, true),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(40, false),
                     new LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData(50, true)
                  )
               )
            );
         System.out.println();
      }

      @Nullable
      private String getTooltipLineForLootInfoGroup(String displayName, List<LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData> list) {
         long tablesContainingItem = list.stream().filter(LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData::containsItem).count();
         if (tablesContainingItem == 0L) {
            return null;
         } else if (list.size() == tablesContainingItem) {
            return "%s (Level: %d+)".formatted(displayName, list.get(0).level);
         } else {
            int startLevel = -1;
            int rangesConcatenated = 0;
            StringBuilder stringBuilder = new StringBuilder(" (Level: ");

            for (int i = 0; i < list.size(); i++) {
               LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData data = list.get(i);
               int currentLevel = data.level();
               if (data.containsItem() && startLevel < 0) {
                  startLevel = currentLevel;
               }

               if (!data.containsItem() && startLevel > -1) {
                  if (currentLevel - 1 > startLevel) {
                     stringBuilder.append(rangesConcatenated > 0 ? ", " : "").append("%d to %d".formatted(startLevel, currentLevel - 1));
                  } else {
                     stringBuilder.append(rangesConcatenated > 0 ? ", " : "").append("%d".formatted(startLevel));
                  }

                  rangesConcatenated++;
                  startLevel = -1;
               }

               if (i == list.size() - 1) {
                  if (startLevel > -1) {
                     stringBuilder.append(rangesConcatenated > 0 ? ", " : "").append("%d+".formatted(startLevel));
                     rangesConcatenated++;
                  }

                  if (rangesConcatenated > 0) {
                     return displayName + stringBuilder.append(")");
                  }
               }
            }

            return displayName + " (ERROR)";
         }
      }

      private boolean doesLootTableContainItem(ResourceLocation lootTableKey, ResourceLocation itemResourceLocation) {
         return LootTableInfo.getItemsForLootTableKey(lootTableKey).contains(itemResourceLocation);
      }

      @NotNull
      private Stream<ResourceLocation> getLootInfoGroupKeysForLootTableKey(ResourceLocation lootTableKey) {
         return this.lootInfoMap.entrySet().stream().filter(entry -> entry.getValue().getLootTableKeys().contains(lootTableKey)).map(Entry::getKey).distinct();
      }

      private boolean isLootTableKeyDisplayAllowed(ResourceLocation lootTableKey) {
         return !this.excludeFromTooltipSet.contains(lootTableKey);
      }

      private record LootTableKeyLevelData(int level, boolean containsItem) implements Comparable<LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData> {
         public int compareTo(@NotNull LootInfoConfig.TooltipLinesProvider.LootTableKeyLevelData o) {
            return Integer.compare(this.level, o.level);
         }
      }
   }
}
