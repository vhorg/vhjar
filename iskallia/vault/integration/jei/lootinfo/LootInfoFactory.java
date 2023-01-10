package iskallia.vault.integration.jei.lootinfo;

import iskallia.vault.config.LootInfoConfig;
import iskallia.vault.core.world.loot.LootTableInfo;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public final class LootInfoFactory {
   @Nonnull
   public static List<LootInfo> create(ResourceLocation lootInfoGroupKey) {
      Set<ResourceLocation> lootTableKeys = getLootTableKeysForLootInfoGroup(lootInfoGroupKey);
      List<ItemStack> itemStackList = getItemsInLootTableKeys(lootTableKeys);
      return getPaginatedLootInfo(itemStackList, 54);
   }

   @Nonnull
   private static List<LootInfo> getPaginatedLootInfo(List<ItemStack> itemStackList, int itemCountPerPage) {
      if (itemStackList.isEmpty()) {
         return Collections.emptyList();
      } else {
         int pages = (itemStackList.size() - 1) / itemCountPerPage + 1;
         List<LootInfo> result = new ArrayList<>(pages);

         for (int i = 0; i < pages; i++) {
            int fromIndex = i * itemCountPerPage;
            int toIndex = Math.min((i + 1) * itemCountPerPage, itemStackList.size());
            result.add(new LootInfo(itemStackList.subList(fromIndex, toIndex)));
         }

         return result;
      }
   }

   @Nonnull
   private static List<ItemStack> getItemsInLootTableKeys(Set<ResourceLocation> lootTableKeys) {
      return lootTableKeys.stream()
         .map(LootTableInfo::getItemsForLootTableKey)
         .flatMap(Collection::stream)
         .distinct()
         .<Item>map(ForgeRegistries.ITEMS::getValue)
         .filter(Objects::nonNull)
         .filter(item -> item != Items.AIR)
         .<ItemStack>map(ItemStack::new)
         .toList();
   }

   @Nonnull
   private static Set<ResourceLocation> getLootTableKeysForLootInfoGroup(ResourceLocation lootInfoGroupKey) {
      LootInfoConfig.LootInfo lootInfo = ModConfigs.LOOT_INFO_CONFIG.getLootInfoMap().get(lootInfoGroupKey);
      return lootInfo != null ? lootInfo.getLootTableKeys() : Collections.emptySet();
   }

   private LootInfoFactory() {
   }
}
