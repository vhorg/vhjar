package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.altar.RequiredItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultAltarConfig extends Config {
   @Expose
   public List<VaultAltarConfig.AltarConfigItem> ITEMS = new ArrayList<>();
   @Expose
   public float PULL_SPEED;
   @Expose
   public double PLAYER_RANGE_CHECK;
   @Expose
   public double ITEM_RANGE_CHECK;
   @Expose
   public int INFUSION_TIME;
   private Random rand = new Random();

   @Override
   public String getName() {
      return "vault_altar";
   }

   @Override
   protected void reset() {
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:cobblestone", 1000, 6000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:gold_ingot", 300, 900));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:iron_ingot", 400, 1300));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:sugar_cane", 800, 1600));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:oak_log", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:spruce_log", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:acacia_log", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:jungle_log", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:dark_oak_log", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:apple", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:redstone", 400, 1000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:ink_sac", 300, 600));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:slime_ball", 200, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:rotten_flesh", 500, 1500));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:blaze_rod", 80, 190));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:brick", 500, 1500));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:bone", 500, 1500));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:spider_eye", 150, 400));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:melon_slice", 1000, 5000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:pumpkin", 1000, 5000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:sand", 1000, 5000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:gravel", 1000, 5000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:wheat", 1000, 2000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:wheat_seeds", 1000, 2000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:carrot", 1000, 2000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:potato", 1000, 2000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:obsidian", 100, 300));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:leather", 300, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:string", 500, 1200));
      this.PULL_SPEED = 1.0F;
      this.PLAYER_RANGE_CHECK = 32.0;
      this.ITEM_RANGE_CHECK = 8.0;
      this.INFUSION_TIME = 5;
   }

   public List<RequiredItem> generateItems(ServerWorld world, PlayerEntity player) {
      return this.getRequiredItemsFromTables(world, player);
   }

   private List<RequiredItem> getRequiredItemsFromJson() {
      List<RequiredItem> requiredItems = new ArrayList<>();
      List<VaultAltarConfig.AltarConfigItem> configItems = new ArrayList<>(this.ITEMS);

      for (int i = 0; i < 4; i++) {
         VaultAltarConfig.AltarConfigItem configItem = configItems.remove(this.rand.nextInt(configItems.size()));
         Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(configItem.ITEM_ID));
         requiredItems.add(new RequiredItem(new ItemStack(item), 0, this.getRandomInt(configItem.MIN, configItem.MAX)));
      }

      return requiredItems;
   }

   private List<RequiredItem> getRequiredItemsFromTables(ServerWorld world, PlayerEntity player) {
      LootContext ctx = new Builder(world)
         .func_216015_a(LootParameters.field_216281_a, player)
         .func_216023_a(world.field_73012_v)
         .func_186469_a(player.func_184817_da())
         .func_216022_a(LootParameterSets.field_237453_h_);
      List<ItemStack> stacks = world.func_73046_m().func_200249_aQ().func_186521_a(Vault.id("chest/altar")).func_216113_a(ctx);
      List<RequiredItem> items = stacks.stream()
         .map(stack -> new RequiredItem(new ItemStack(stack.func_77973_b()), 0, stack.func_190916_E()))
         .sorted(Comparator.comparingInt(o -> o.getItem().func_77973_b().getRegistryName().hashCode()))
         .collect(Collectors.toList());
      List<RequiredItem> stackedItems = new ArrayList<>();
      RequiredItem lastItem = null;

      for (RequiredItem item : items) {
         if (lastItem == null) {
            lastItem = item;
         } else if (item.getItem().func_77973_b() == lastItem.getItem().func_77973_b()) {
            lastItem = new RequiredItem(lastItem.getItem(), 0, lastItem.getAmountRequired() + item.getAmountRequired());
         } else {
            stackedItems.add(lastItem);
            lastItem = item;
         }
      }

      stackedItems.add(lastItem);
      return stackedItems.stream().limit(4L).collect(Collectors.toList());
   }

   private int getRandomInt(int min, int max) {
      return (int)(Math.random() * (max - min) + min);
   }

   public class AltarConfigItem {
      @Expose
      public String ITEM_ID;
      @Expose
      public int MIN;
      @Expose
      public int MAX;

      public AltarConfigItem(String item, int min, int max) {
         this.ITEM_ID = item;
         this.MIN = min;
         this.MAX = max;
      }
   }
}
