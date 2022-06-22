package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.data.WeightedList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.ForgeRegistries;

public class ScavengerHuntConfig extends Config {
   @Expose
   private ScavengerHuntConfig.ItemPool requiredItems;
   @Expose
   private ScavengerHuntConfig.ItemPool chestItems;
   @Expose
   private ScavengerHuntConfig.ItemPool treasureItems;
   @Expose
   private final Map<String, ScavengerHuntConfig.ItemPool> mobDropItems = new HashMap<>();

   @Nullable
   public ScavengerHuntConfig.ItemEntry getRandomRequiredItem(Predicate<Item> excludedItems) {
      return this.requiredItems.pool.copyFiltered(entry -> !excludedItems.test(entry.getItem())).getRandom(rand);
   }

   public int getTotalRequiredItems() {
      return this.requiredItems.getRandomAmount();
   }

   public List<ScavengerHuntConfig.ItemEntry> generateChestLoot(Predicate<ScavengerHuntConfig.ItemEntry> dropFilter) {
      return this.chestItems.getRandomEntries(dropFilter);
   }

   public List<ScavengerHuntConfig.ItemEntry> generateTreasureLoot(Predicate<ScavengerHuntConfig.ItemEntry> dropFilter) {
      return this.treasureItems.getRandomEntries(dropFilter);
   }

   public List<ScavengerHuntConfig.ItemEntry> generateMobDropLoot(Predicate<ScavengerHuntConfig.ItemEntry> dropFilter, EntityType<?> mobType) {
      return this.mobDropItems.getOrDefault(mobType.getRegistryName().toString(), ScavengerHuntConfig.ItemPool.EMPTY).getRandomEntries(dropFilter);
   }

   public ScavengerHuntConfig.SourceType getRequirementSource(ItemStack stack) {
      Item requiredItem = stack.func_77973_b();

      for (WeightedList.Entry<ScavengerHuntConfig.ItemEntry> entry : this.chestItems.pool) {
         if (requiredItem.equals(entry.value.getItem())) {
            return ScavengerHuntConfig.SourceType.CHEST;
         }
      }

      for (WeightedList.Entry<ScavengerHuntConfig.ItemEntry> entryx : this.treasureItems.pool) {
         if (requiredItem.equals(entryx.value.getItem())) {
            return ScavengerHuntConfig.SourceType.TREASURE;
         }
      }

      return ScavengerHuntConfig.SourceType.MOB;
   }

   @Nullable
   public ResourceLocation getRequirementMobType(ItemStack stack) {
      Item requiredItem = stack.func_77973_b();

      for (Entry<String, ScavengerHuntConfig.ItemPool> mobDropEntry : this.mobDropItems.entrySet()) {
         for (WeightedList.Entry<ScavengerHuntConfig.ItemEntry> entry : mobDropEntry.getValue().getPool()) {
            if (requiredItem.equals(entry.value.getItem())) {
               return new ResourceLocation(mobDropEntry.getKey());
            }
         }
      }

      return null;
   }

   @Override
   public String getName() {
      return "scavenger_hunt";
   }

   @Override
   protected void reset() {
      this.requiredItems = new ScavengerHuntConfig.ItemPool(10, 15);
      this.chestItems = new ScavengerHuntConfig.ItemPool(1, 3);
      this.treasureItems = new ScavengerHuntConfig.ItemPool(1, 3);
      this.mobDropItems.clear();
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_CREEPER_EYE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_CREEPER_FOOT);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_CREEPER_FUSE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_CREEPER_TNT);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_CREEPER_VIAL);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_CREEPER_CHARM);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_DROWNED_BARNACLE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_DROWNED_EYE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_DROWNED_HIDE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_DROWNED_VIAL);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_DROWNED_CHARM);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SKELETON_SHARD);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SKELETON_EYE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SKELETON_RIBCAGE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SKELETON_SKULL);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SKELETON_WISHBONE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SKELETON_VIAL);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SKELETON_CHARM);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SPIDER_FANGS);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SPIDER_LEG);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SPIDER_WEBBING);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SPIDER_CURSED_CHARM);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SPIDER_VIAL);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SPIDER_CHARM);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_ZOMBIE_BRAIN);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_ZOMBIE_ARM);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_ZOMBIE_EAR);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_ZOMBIE_EYE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_ZOMBIE_HIDE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_ZOMBIE_NOSE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_ZOMBIE_VIAL);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_TREASURE_BANGLE_BLUE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_TREASURE_BANGLE_PINK);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_TREASURE_BANGLE_GREEN);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_TREASURE_EARRINGS);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_TREASURE_GOBLET);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_TREASURE_SACK);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_TREASURE_SCROLL_RED);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_TREASURE_SCROLL_BLUE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_BROKEN_POTTERY);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_CRACKED_PEARL);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_CRACKED_SCRIPT);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_EMPTY_JAR);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_OLD_BOOK);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_POTTERY_SHARD);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_POULTICE_JAR);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_PRESERVES_JAR);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_RIPPED_PAGE);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_SADDLE_BAG);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_SPICE_JAR);
      this.addRequiredItem(this.requiredItems, ModItems.SCAVENGER_SCRAP_WIZARD_WAND);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_BROKEN_POTTERY);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_CRACKED_PEARL);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_CRACKED_SCRIPT);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_EMPTY_JAR);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_OLD_BOOK);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_POTTERY_SHARD);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_POULTICE_JAR);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_PRESERVES_JAR);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_RIPPED_PAGE);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_SADDLE_BAG);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_SPICE_JAR);
      this.addDropItem(this.chestItems, ModItems.SCAVENGER_SCRAP_WIZARD_WAND);
      this.addDropItem(this.treasureItems, ModItems.SCAVENGER_TREASURE_BANGLE_BLUE);
      this.addDropItem(this.treasureItems, ModItems.SCAVENGER_TREASURE_BANGLE_PINK);
      this.addDropItem(this.treasureItems, ModItems.SCAVENGER_TREASURE_BANGLE_GREEN);
      this.addDropItem(this.treasureItems, ModItems.SCAVENGER_TREASURE_EARRINGS);
      this.addDropItem(this.treasureItems, ModItems.SCAVENGER_TREASURE_GOBLET);
      this.addDropItem(this.treasureItems, ModItems.SCAVENGER_TREASURE_SACK);
      this.addDropItem(this.treasureItems, ModItems.SCAVENGER_TREASURE_SCROLL_RED);
      this.addDropItem(this.treasureItems, ModItems.SCAVENGER_TREASURE_SCROLL_BLUE);
      ScavengerHuntConfig.ItemPool creeperPool = new ScavengerHuntConfig.ItemPool(1, 3);
      this.addDropItem(creeperPool, ModItems.SCAVENGER_CREEPER_EYE);
      this.addDropItem(creeperPool, ModItems.SCAVENGER_CREEPER_FOOT);
      this.addDropItem(creeperPool, ModItems.SCAVENGER_CREEPER_FUSE);
      this.addDropItem(creeperPool, ModItems.SCAVENGER_CREEPER_TNT);
      this.addDropItem(creeperPool, ModItems.SCAVENGER_CREEPER_VIAL);
      this.addDropItem(creeperPool, ModItems.SCAVENGER_CREEPER_CHARM);
      this.mobDropItems.put(EntityType.field_200797_k.getRegistryName().toString(), creeperPool);
      ScavengerHuntConfig.ItemPool zombiePool = new ScavengerHuntConfig.ItemPool(1, 3);
      this.addDropItem(zombiePool, ModItems.SCAVENGER_ZOMBIE_BRAIN);
      this.addDropItem(zombiePool, ModItems.SCAVENGER_ZOMBIE_ARM);
      this.addDropItem(zombiePool, ModItems.SCAVENGER_ZOMBIE_EAR);
      this.addDropItem(zombiePool, ModItems.SCAVENGER_ZOMBIE_EYE);
      this.addDropItem(zombiePool, ModItems.SCAVENGER_ZOMBIE_HIDE);
      this.addDropItem(zombiePool, ModItems.SCAVENGER_ZOMBIE_NOSE);
      this.addDropItem(zombiePool, ModItems.SCAVENGER_ZOMBIE_VIAL);
      this.mobDropItems.put(EntityType.field_200725_aD.getRegistryName().toString(), zombiePool);
      this.mobDropItems.put(EntityType.field_200763_C.getRegistryName().toString(), zombiePool);
      ScavengerHuntConfig.ItemPool drownedPool = new ScavengerHuntConfig.ItemPool(1, 3);
      drownedPool.pool.addAll(zombiePool.pool.copy());
      this.addDropItem(drownedPool, ModItems.SCAVENGER_DROWNED_BARNACLE);
      this.addDropItem(drownedPool, ModItems.SCAVENGER_DROWNED_EYE);
      this.addDropItem(drownedPool, ModItems.SCAVENGER_DROWNED_HIDE);
      this.addDropItem(drownedPool, ModItems.SCAVENGER_DROWNED_VIAL);
      this.addDropItem(drownedPool, ModItems.SCAVENGER_DROWNED_CHARM);
      this.mobDropItems.put(EntityType.field_204724_o.getRegistryName().toString(), drownedPool);
      ScavengerHuntConfig.ItemPool spiderPool = new ScavengerHuntConfig.ItemPool(1, 3);
      this.addDropItem(spiderPool, ModItems.SCAVENGER_SPIDER_FANGS);
      this.addDropItem(spiderPool, ModItems.SCAVENGER_SPIDER_LEG);
      this.addDropItem(spiderPool, ModItems.SCAVENGER_SPIDER_WEBBING);
      this.addDropItem(spiderPool, ModItems.SCAVENGER_SPIDER_CURSED_CHARM);
      this.addDropItem(spiderPool, ModItems.SCAVENGER_SPIDER_VIAL);
      this.addDropItem(spiderPool, ModItems.SCAVENGER_SPIDER_CHARM);
      this.mobDropItems.put(EntityType.field_200748_an.getRegistryName().toString(), spiderPool);
      this.mobDropItems.put(EntityType.field_200794_h.getRegistryName().toString(), spiderPool);
      ScavengerHuntConfig.ItemPool skeletonPool = new ScavengerHuntConfig.ItemPool(1, 3);
      this.addDropItem(skeletonPool, ModItems.SCAVENGER_SKELETON_SHARD);
      this.addDropItem(skeletonPool, ModItems.SCAVENGER_SKELETON_EYE);
      this.addDropItem(skeletonPool, ModItems.SCAVENGER_SKELETON_RIBCAGE);
      this.addDropItem(skeletonPool, ModItems.SCAVENGER_SKELETON_SKULL);
      this.addDropItem(skeletonPool, ModItems.SCAVENGER_SKELETON_WISHBONE);
      this.addDropItem(skeletonPool, ModItems.SCAVENGER_SKELETON_VIAL);
      this.addDropItem(skeletonPool, ModItems.SCAVENGER_SKELETON_CHARM);
      this.mobDropItems.put(EntityType.field_200741_ag.getRegistryName().toString(), skeletonPool);
      this.mobDropItems.put(EntityType.field_200722_aA.getRegistryName().toString(), skeletonPool);
   }

   private void addRequiredItem(ScavengerHuntConfig.ItemPool out, Item i) {
      out.pool.add(new ScavengerHuntConfig.ItemEntry(i, 10, 15), 1);
   }

   private void addDropItem(ScavengerHuntConfig.ItemPool out, Item i) {
      out.pool.add(new ScavengerHuntConfig.ItemEntry(i, 1, 1), 1);
   }

   public static class ItemEntry {
      @Expose
      private final String item;
      @Expose
      private final int min;
      @Expose
      private final int max;

      public ItemEntry(Item item, int min, int max) {
         this(item.getRegistryName().toString(), min, max);
      }

      public ItemEntry(String item, int min, int max) {
         this.item = item;
         this.min = min;
         this.max = max;
      }

      public Item getItem() {
         return (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.item));
      }

      public int getRandomAmount() {
         return MathUtilities.getRandomInt(this.min, this.max + 1);
      }

      public ItemStack createItemStack() {
         return new ItemStack(this.getItem(), this.getRandomAmount());
      }
   }

   public static class ItemPool {
      private static final ScavengerHuntConfig.ItemPool EMPTY = new ScavengerHuntConfig.ItemPool(0, 0);
      @Expose
      private final WeightedList<ScavengerHuntConfig.ItemEntry> pool = new WeightedList<>();
      @Expose
      private final int min;
      @Expose
      private final int max;

      public ItemPool(int min, int max) {
         this.min = min;
         this.max = max;
      }

      public List<WeightedList.Entry<ScavengerHuntConfig.ItemEntry>> getPool() {
         return Collections.unmodifiableList(this.pool);
      }

      public ScavengerHuntConfig.ItemEntry getRandomEntry(Predicate<ScavengerHuntConfig.ItemEntry> dropFilter) {
         return this.pool.copyFiltered(dropFilter).getRandom(Config.rand);
      }

      public int getRandomAmount() {
         return MathUtilities.getRandomInt(this.min, this.max + 1);
      }

      public List<ScavengerHuntConfig.ItemEntry> getRandomEntries(Predicate<ScavengerHuntConfig.ItemEntry> dropFilter) {
         return IntStream.range(0, this.getRandomAmount())
            .mapToObj(index -> this.getRandomEntry(dropFilter))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
      }
   }

   public static enum SourceType {
      MOB(Vault.id("textures/gui/overlay/scav_mob.png"), TextFormatting.RED),
      CHEST(Vault.id("textures/gui/overlay/scav_chest.png"), TextFormatting.GREEN),
      TREASURE(Vault.id("textures/gui/overlay/scav_treasure.png"), TextFormatting.GOLD);

      private final ResourceLocation iconPath;
      private final TextFormatting requirementColor;

      private SourceType(ResourceLocation iconPath, TextFormatting requirementColor) {
         this.iconPath = iconPath;
         this.requirementColor = requirementColor;
      }

      public ResourceLocation getIconPath() {
         return this.iconPath;
      }

      public TextFormatting getRequirementColor() {
         return this.requirementColor;
      }
   }
}
