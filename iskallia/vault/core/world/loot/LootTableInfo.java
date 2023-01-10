package iskallia.vault.core.world.loot;

import iskallia.vault.core.data.key.LootPoolKey;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.world.loot.entry.ItemLootEntry;
import iskallia.vault.core.world.loot.entry.LootEntry;
import iskallia.vault.core.world.loot.entry.ReferenceLootEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class LootTableInfo {
   private static final Set<ResourceLocation> PROCESSED_LOOT_TABLE_KEY_SET = new HashSet<>();
   private static final Map<ResourceLocation, Set<LootTableInfo.Info>> ITEM_TO_INFO_MAP = new HashMap<>();
   private static final Map<ResourceLocation, Set<ResourceLocation>> LOOT_TABLE_KEY_TO_ITEMS_MAP = new HashMap<>();

   public static void clear() {
      PROCESSED_LOOT_TABLE_KEY_SET.clear();
      ITEM_TO_INFO_MAP.clear();
      LOOT_TABLE_KEY_TO_ITEMS_MAP.clear();
   }

   public static void cache(LootTableKey key) {
      if (!PROCESSED_LOOT_TABLE_KEY_SET.contains(key.getId())) {
         PROCESSED_LOOT_TABLE_KEY_SET.add(key.getId());

         for (LootTable table : key.getMap().values()) {
            for (LootTable.Entry entry : table.entries) {
               cache(key, entry.pool);
            }
         }
      }
   }

   private static void cache(LootTableKey lootTableKey, LootPoolKey lootPoolKey) {
      for (LootPool pool : lootPoolKey.getMap().values()) {
         cache(lootTableKey, pool);
      }
   }

   private static void cache(LootTableKey lootTableKey, LootPool pool) {
      pool.iterate(lootEntry -> cache(lootTableKey, lootEntry));
   }

   private static boolean cache(LootTableKey lootTableKey, LootEntry lootEntry) {
      if (lootEntry instanceof ItemLootEntry itemLootEntry) {
         cache(lootTableKey, itemLootEntry);
      } else {
         if (!(lootEntry instanceof ReferenceLootEntry referenceLootEntry)) {
            throw new UnsupportedOperationException("Missing handling for %s of type %s".formatted(LootEntry.class, lootEntry.getClass()));
         }

         cache(lootTableKey, referenceLootEntry.getReference());
      }

      return true;
   }

   private static void cache(LootTableKey lootTableKey, ItemLootEntry itemLootEntry) {
      Item item = itemLootEntry.getItem();
      ResourceLocation registryName = item.getRegistryName();
      if (registryName != null) {
         cache(lootTableKey, registryName);
      }
   }

   private static void cache(LootTableKey lootTableKey, ResourceLocation registryName) {
      ITEM_TO_INFO_MAP.computeIfAbsent(registryName, resourceLocation -> new HashSet<>()).add(LootTableInfo.Info.of(lootTableKey));
      LOOT_TABLE_KEY_TO_ITEMS_MAP.computeIfAbsent(lootTableKey.getId(), resourceLocation -> new HashSet<>()).add(registryName);
   }

   public static boolean containsInfoForItem(ResourceLocation resourceLocation) {
      return ITEM_TO_INFO_MAP.containsKey(resourceLocation);
   }

   private static Set<LootTableInfo.Info> getInfoForItem(ResourceLocation resourceLocation) {
      return ITEM_TO_INFO_MAP.get(resourceLocation);
   }

   public static Set<ResourceLocation> getLootTableKeysForItem(ResourceLocation resourceLocation) {
      return getInfoForItem(resourceLocation).stream().map(LootTableInfo.Info::resourceLocation).collect(Collectors.toSet());
   }

   public static Set<ResourceLocation> getItemsForLootTableKey(ResourceLocation resourceLocation) {
      return LOOT_TABLE_KEY_TO_ITEMS_MAP.getOrDefault(resourceLocation, Collections.emptySet());
   }

   public record Info(ResourceLocation resourceLocation) {
      public static LootTableInfo.Info of(LootTableKey lootTableKey) {
         return new LootTableInfo.Info(lootTableKey.getId());
      }
   }
}
