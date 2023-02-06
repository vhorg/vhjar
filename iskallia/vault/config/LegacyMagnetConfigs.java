package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.LegacyMagnetItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LegacyMagnetConfigs extends Config {
   public static final int MATERIAL_COUNT = 4;
   @Expose
   private static final int DEMAGNETIZER_RADIUS = 32;
   @Expose
   private Map<LegacyMagnetItem.Stat, LegacyMagnetConfigs.Upgrade> UPGRADES;
   @Expose
   private Map<LegacyMagnetItem.Perk, LegacyMagnetConfigs.PerkUpgrade> PERKS;
   @Expose
   private int ITEMS_FOR_ONE_DURABILITY;
   @Expose
   private int STURDINESS_FOR_PERK = 50;
   @Expose
   private int STURDINESS_DECREMENT = 5;
   @Expose
   private int BASE_REPAIR_SLOTS = 2;
   @Expose
   private Item[] MATERIAL_ITEMS = new Item[4];

   public Item getMaterialItem(int i) {
      return this.MATERIAL_ITEMS[i];
   }

   public Map<LegacyMagnetItem.Stat, LegacyMagnetConfigs.Upgrade> getAllUpgrades() {
      return Map.copyOf(this.UPGRADES);
   }

   public int getSturdinessCutoff() {
      return this.STURDINESS_FOR_PERK;
   }

   public int getSturdinessDecrement() {
      return this.STURDINESS_DECREMENT;
   }

   public LegacyMagnetConfigs.Upgrade getUpgrade(LegacyMagnetItem.Stat stat) {
      return this.UPGRADES.get(stat);
   }

   public LegacyMagnetConfigs.PerkUpgrade getPerkUpgrade(LegacyMagnetItem.Perk perk) {
      return this.PERKS.get(perk);
   }

   public int getBaseRepairSlots() {
      return this.BASE_REPAIR_SLOTS;
   }

   public TextColor getStatColor(LegacyMagnetItem.Stat s) {
      return this.getUpgrade(s).COLOR;
   }

   public float getItemsForOneDurability() {
      return this.ITEMS_FOR_ONE_DURABILITY;
   }

   public int getDemagnetizerRadius() {
      return 32;
   }

   @Override
   protected boolean isValid() {
      try {
         boolean r = checkAllFieldsAreNotNull(this);
         if (!r) {
            return false;
         }

         if (this.MATERIAL_ITEMS.length != 4) {
            return false;
         }

         if (this.UPGRADES.size() != LegacyMagnetItem.Stat.values().length) {
            return false;
         }

         for (LegacyMagnetConfigs.Upgrade u : this.UPGRADES.values()) {
            if (u.MATERIAL_COSTS.length != 4) {
               return false;
            }
         }

         if (this.PERKS.size() != LegacyMagnetItem.Perk.values().length) {
            return false;
         }
      } catch (Exception var4) {
      }

      return true;
   }

   @Override
   public String getName() {
      return "magnet_table";
   }

   @Override
   protected void reset() {
      this.UPGRADES = Map.of(
         LegacyMagnetItem.Stat.DURABILITY,
         new LegacyMagnetConfigs.Upgrade(1, 2, 1, 0, 10),
         LegacyMagnetItem.Stat.RANGE,
         new LegacyMagnetConfigs.Upgrade(1, 0, 1, 0, 10),
         LegacyMagnetItem.Stat.MANA_EFFICIENCY,
         new LegacyMagnetConfigs.Upgrade(1, 1, 2, 0, 10),
         LegacyMagnetItem.Stat.VELOCITY,
         new LegacyMagnetConfigs.Upgrade(0, 1, 1, 0, 10)
      );
      this.MATERIAL_ITEMS = new Item[]{ModItems.MAGNETITE, ModItems.PAINITE_GEM, ModItems.VAULT_PLATING, ModBlocks.VAULT_BRONZE};
      this.STURDINESS_FOR_PERK = 50;
      this.STURDINESS_DECREMENT = 5;
      this.BASE_REPAIR_SLOTS = 2;
      this.ITEMS_FOR_ONE_DURABILITY = 64;
      this.PERKS = Arrays.stream(LegacyMagnetItem.Perk.values())
         .collect(Collectors.toMap(Function.identity(), p -> new LegacyMagnetConfigs.PerkUpgrade(1, 5, TextColor.fromRgb(-1), "configure me")));
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

      public String getAdvancedTooltip(int power) {
         return String.format(this.TOOLTIP_HINT, power);
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
      protected final int MIN_YIELD;
      @Expose
      protected final int MAX_YIELD;
      @Expose
      protected final int BASE_VALUE;
      @Expose
      protected final int MAX_VALUE;
      @Expose
      protected final TextColor COLOR;
      @Expose
      protected final String TOOLTIP_HINT;

      public Upgrade(int m0, int m1, int m2, int m3, int base) {
         this.MATERIAL_COSTS = new int[]{m0, m1, m2, m3};
         this.MIN_YIELD = 1;
         this.MAX_YIELD = 3;
         this.BASE_VALUE = base;
         this.MAX_VALUE = 100000;
         this.COLOR = TextColor.fromRgb(-1);
         this.TOOLTIP_HINT = "configure me";
         this.POSITIVE = true;
      }

      public int getMaterialCost(int index) {
         return this.MATERIAL_COSTS[index];
      }

      public boolean canCraftAndApply(int[] availableMaterials, ItemStack stack, LegacyMagnetItem.Stat stat) {
         for (int i = 0; i < this.MATERIAL_COSTS.length; i++) {
            if (availableMaterials[i] < this.MATERIAL_COSTS[i]) {
               return false;
            }
         }

         int s = LegacyMagnetItem.getUsableStat(stack, stat);
         return !this.POSITIVE ? s >= -this.MAX_VALUE : s <= this.MAX_VALUE;
      }

      public int getYield(Random random) {
         return Mth.randomBetweenInclusive(random, this.MIN_YIELD, this.MAX_YIELD) * (this.POSITIVE ? 1 : -1);
      }

      public String getAdvancedTooltip() {
         return this.TOOLTIP_HINT;
      }

      public List<Component> getTooltip(int[] availableMaterials, LegacyMagnetItem.Stat stat) {
         List<Component> list = new ArrayList<>();
         String s = (this.POSITIVE ? "" : "-") + this.MIN_YIELD + " to " + (this.POSITIVE ? "" : "-") + this.MAX_YIELD;
         list.add(new TextComponent(stat.getReadableName() + " " + s));

         for (int v = 0; v < this.MATERIAL_COSTS.length; v++) {
            int cost = this.MATERIAL_COSTS[v];
            if (cost != 0) {
               list.add(
                  new TranslatableComponent("tooltip.the_vault.magnet_upgrade", new Object[]{ModConfigs.MAGNET_CONFIG.MATERIAL_ITEMS[v].getDescription(), cost})
                     .withStyle(cost > availableMaterials[v] ? ChatFormatting.RED : ChatFormatting.GREEN)
               );
            }
         }

         return list;
      }

      public int getBaseValue() {
         return this.BASE_VALUE;
      }
   }
}
