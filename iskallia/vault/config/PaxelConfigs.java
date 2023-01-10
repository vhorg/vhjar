package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.paxel.PaxelItem;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PaxelConfigs extends Config {
   private static final DecimalFormat FORMAT = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT));
   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ROOT));
   public static final int MATERIAL_COUNT = 6;
   @Expose
   private Map<ResourceLocation, PaxelConfigs.Entry> PULVERIZING_MAP;
   @Expose
   private Map<Item, PaxelConfigs.PaxelTierValues> TIER_VALUES;
   @Expose
   private Map<PaxelItem.Stat, PaxelConfigs.Upgrade> UPGRADES;
   @Expose
   private Map<PaxelItem.Perk, PaxelConfigs.PerkUpgrade> PERKS;
   @Expose
   private Item[] MATERIAL_ITEMS = new Item[6];
   @Expose
   private int MAX_HARDNESS_ABOVE_TARGET;

   public Item getMaterialItem(int i) {
      return this.MATERIAL_ITEMS[i];
   }

   public Map<PaxelItem.Stat, PaxelConfigs.Upgrade> getAllUpgrades() {
      return Map.copyOf(this.UPGRADES);
   }

   public PaxelConfigs.Upgrade getUpgrade(PaxelItem.Stat stat) {
      return this.UPGRADES.get(stat);
   }

   public PaxelConfigs.PerkUpgrade getPerkUpgrade(PaxelItem.Perk perk) {
      return this.PERKS.get(perk);
   }

   public TextColor getStatColor(PaxelItem.Stat s) {
      return this.getUpgrade(s).COLOR;
   }

   public Optional<ItemStack> getPulverizedState(BlockState state, Random random) {
      PaxelConfigs.Entry v = this.PULVERIZING_MAP.get(state.getBlock().getRegistryName());
      return v != null && random.nextFloat() < v.probability ? Optional.of(v.item.copy()) : Optional.empty();
   }

   public PaxelConfigs.PaxelTierValues getTierValues(ItemStack paxel) {
      return this.TIER_VALUES.get(paxel.getItem());
   }

   public int getMaxHardnessAboveTarget() {
      return this.MAX_HARDNESS_ABOVE_TARGET;
   }

   @Override
   public String getName() {
      return "paxel";
   }

   @Override
   protected void reset() {
      this.UPGRADES = Map.of(
         PaxelItem.Stat.DURABILITY,
         new PaxelConfigs.Upgrade(new int[]{1, 0, 0, 0, 0, 0}, 10),
         PaxelItem.Stat.MINING_SPEED,
         new PaxelConfigs.Upgrade(new int[]{0, 1, 1, 1, 1, 0}, 10),
         PaxelItem.Stat.COPIOUSLY,
         new PaxelConfigs.Upgrade(new int[]{0, 1, 2, 1, 1, 0}, 10),
         PaxelItem.Stat.REACH,
         new PaxelConfigs.Upgrade(new int[]{0, 0, 0, 0, 0, 1}, 10)
      );
      this.MATERIAL_ITEMS = new Item[]{
         ModItems.MAGNETITE, ModItems.PAINITE_GEM, ModItems.VAULT_PLATING, ModBlocks.VAULT_BRONZE, ModItems.ASHIUM_GEM, ModItems.ISKALLIUM_GEM
      };
      this.TIER_VALUES = Map.of(
         ModItems.VAULTERITE_PICKAXE,
         new PaxelConfigs.PaxelTierValues(5, 5, 0, 2),
         ModItems.VAULT_PICKAXE,
         new PaxelConfigs.PaxelTierValues(5, 5, 2, 3),
         ModItems.BLACK_CHROMATIC_PICKAXE,
         new PaxelConfigs.PaxelTierValues(5, 5, 2, 3),
         ModItems.ECHOING_PICKAXE,
         new PaxelConfigs.PaxelTierValues(5, 5, 2, 3),
         ModItems.PRISMATIC_PICKAXE,
         new PaxelConfigs.PaxelTierValues(5, 5, 2, 3)
      );
      this.PERKS = Arrays.stream(PaxelItem.Perk.values())
         .collect(Collectors.toMap(Function.identity(), p -> new PaxelConfigs.PerkUpgrade(1, 5, TextColor.fromRgb(-1), "configure me")));
      this.PULVERIZING_MAP = Map.of(Blocks.DIORITE.getRegistryName(), new PaxelConfigs.Entry(new ItemStack(Items.DIAMOND, 3), 0.5F));
      this.MAX_HARDNESS_ABOVE_TARGET = 5;
   }

   private static class Entry {
      @Expose
      final ItemStack item;
      @Expose
      final float probability;

      private Entry(ItemStack item, float probability) {
         this.item = item;
         this.probability = probability;
      }
   }

   public static class PaxelTierValues {
      @Expose
      protected final int STURDINESS_DECREMENT_PER_LEVEL;
      @Expose
      protected final int LEVEL_DELTA_PER_SOCKET;
      @Expose
      protected final int TIER_BASE_DURABILITY;
      @Expose
      private final int BASE_REPAIR_SLOTS;

      public PaxelTierValues(int sturdinessDec, int levelsPerSocket, int additionalDurability, int baseRepairSlots) {
         this.STURDINESS_DECREMENT_PER_LEVEL = sturdinessDec;
         this.LEVEL_DELTA_PER_SOCKET = levelsPerSocket;
         this.TIER_BASE_DURABILITY = additionalDurability;
         this.BASE_REPAIR_SLOTS = baseRepairSlots;
      }

      public int getAdditionalDurability() {
         return this.TIER_BASE_DURABILITY;
      }

      public int getBaseRepairSlots() {
         return this.BASE_REPAIR_SLOTS;
      }

      public int getSturdinessDecrement() {
         return this.STURDINESS_DECREMENT_PER_LEVEL;
      }

      public int getLevelsPerSocket() {
         return this.LEVEL_DELTA_PER_SOCKET;
      }

      public float getMaxLevel() {
         return 100.0F / this.STURDINESS_DECREMENT_PER_LEVEL;
      }
   }

   public static class PerkUpgrade {
      @Expose
      protected final int MIN_YIELD;
      @Expose
      protected final int MAX_YIELD;
      @Expose
      protected final int MAX_VALUE;
      @Expose
      protected final TextColor COLOR;
      @Expose
      protected final String TOOLTIP_HINT;

      public PerkUpgrade(int min_yield, int max_yield, TextColor color, String tooltip) {
         this.MIN_YIELD = min_yield;
         this.MAX_YIELD = max_yield;
         this.COLOR = color;
         this.TOOLTIP_HINT = tooltip;
         this.MAX_VALUE = 10000;
      }

      public int getYield(Random random) {
         return Mth.randomBetweenInclusive(random, this.MIN_YIELD, this.MAX_YIELD);
      }

      public int getColor() {
         return this.COLOR == null ? -1 : this.COLOR.getValue();
      }

      public String getAdvancedTooltip() {
         return String.format(this.TOOLTIP_HINT);
      }

      public int getMaxValue() {
         return this.MAX_VALUE;
      }
   }

   public static class Upgrade {
      @Expose
      protected final int[] MATERIAL_COSTS;
      @Expose
      protected final boolean POSITIVE;
      @Expose
      protected final boolean IS_PERCENT;
      @Expose
      protected final boolean IS_DECIMAL;
      @Expose
      protected final float MIN_YIELD;
      @Expose
      protected final float MAX_YIELD;
      @Expose
      protected final float BASE_VALUE;
      @Expose
      protected final float MAX_VALUE;
      @Expose
      protected final TextColor COLOR;
      @Expose
      protected final String TOOLTIP_HINT;

      public Upgrade(int[] costs, int base) {
         this.MATERIAL_COSTS = costs;
         this.MIN_YIELD = 1.0F;
         this.MAX_YIELD = 3.0F;
         this.BASE_VALUE = base;
         this.MAX_VALUE = 100000.0F;
         this.COLOR = TextColor.fromRgb(-1);
         this.TOOLTIP_HINT = "configure me";
         this.POSITIVE = true;
         this.IS_PERCENT = false;
         this.IS_DECIMAL = false;
      }

      public int getMaterialCost(int index) {
         return this.MATERIAL_COSTS[index];
      }

      public boolean canCraftAndApply(int[] availableMaterials, ItemStack stack, PaxelItem.Stat stat) {
         for (int i = 0; i < this.MATERIAL_COSTS.length; i++) {
            if (availableMaterials[i] < this.MATERIAL_COSTS[i]) {
               return false;
            }
         }

         float statValue = PaxelItem.getUsableStat(stack, stat);
         return !this.POSITIVE ? statValue >= -this.MAX_VALUE : statValue <= this.MAX_VALUE;
      }

      public float getYield(Random random) {
         return Mth.randomBetween(random, this.MIN_YIELD, this.MAX_YIELD) * (this.POSITIVE ? 1 : -1);
      }

      public boolean isPercent() {
         return this.IS_PERCENT;
      }

      public boolean isDecimal() {
         return this.IS_DECIMAL;
      }

      public String getAdvancedTooltip() {
         return this.TOOLTIP_HINT;
      }

      public String formatValue(float value) {
         String valueStr;
         if (this.isPercent()) {
            valueStr = PaxelConfigs.FORMAT.format(value) + "%";
         } else if (this.isDecimal()) {
            valueStr = PaxelConfigs.DECIMAL_FORMAT.format(value);
         } else {
            valueStr = String.valueOf((int)value);
         }

         return valueStr;
      }

      public List<Component> getTooltip(int[] availableMaterials, PaxelItem.Stat stat) {
         List<Component> list = new ArrayList<>();
         String s = (this.POSITIVE ? "" : "-") + this.MIN_YIELD + " to " + (this.POSITIVE ? "" : "-") + this.MAX_YIELD;
         list.add(new TextComponent(stat.getReadableName() + " " + s));

         for (int v = 0; v < this.MATERIAL_COSTS.length; v++) {
            int cost = this.MATERIAL_COSTS[v];
            if (cost != 0) {
               list.add(
                  new TranslatableComponent("tooltip.the_vault.magnet_upgrade", new Object[]{ModConfigs.PAXEL_CONFIGS.MATERIAL_ITEMS[v].getDescription(), cost})
                     .withStyle(cost > availableMaterials[v] ? ChatFormatting.RED : ChatFormatting.GREEN)
               );
            }
         }

         return list;
      }

      public float getBaseValue() {
         return this.BASE_VALUE;
      }
   }
}
