package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.util.data.WeightedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;

public class StatueLootConfig extends Config {
   @Expose
   private int MAX_ACCELERATION_CHIPS;
   @Expose
   private HashMap<Integer, Integer> INTERVAL_DECREASE_PER_CHIP = new HashMap<>();
   @Expose
   private WeightedList<SingleItemEntry> LOOT = new WeightedList<>();
   @Expose
   private int INTERVAL;

   @Override
   public String getName() {
      return "statue_loot";
   }

   @Override
   protected void reset() {
      this.MAX_ACCELERATION_CHIPS = 4;
      this.INTERVAL_DECREASE_PER_CHIP.put(1, 50);
      this.INTERVAL_DECREASE_PER_CHIP.put(2, 100);
      this.INTERVAL_DECREASE_PER_CHIP.put(3, 200);
      this.INTERVAL_DECREASE_PER_CHIP.put(4, 500);
      this.LOOT = new WeightedList<>();
      ItemStack fancyApple = new ItemStack(Items.APPLE);
      fancyApple.setHoverName(new TextComponent("Fancy Apple"));
      this.LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(fancyApple), 1));
      ItemStack sword = new ItemStack(Items.WOODEN_SWORD);
      sword.enchant(Enchantments.SHARPNESS, 10);
      this.LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(sword), 1));
      this.LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(Items.DIORITE_SLAB.getDefaultInstance()), 1));
      this.LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(Items.DIORITE.getDefaultInstance()), 1));
      this.LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(Items.DIORITE_WALL.getDefaultInstance()), 1));
      this.LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(Items.DIORITE_STAIRS.getDefaultInstance()), 1));
      this.INTERVAL = 500;
   }

   public ItemStack randomLoot() {
      return this.getItem(this.LOOT.getRandom(new Random()));
   }

   public int getInterval() {
      return this.INTERVAL;
   }

   public int getMaxAccelerationChips() {
      return this.MAX_ACCELERATION_CHIPS;
   }

   private ItemStack getItem(SingleItemEntry entry) {
      ItemStack stack = ItemStack.EMPTY;

      try {
         Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.ITEM));
         stack = new ItemStack(item);
         if (entry.NBT != null) {
            CompoundTag nbt = TagParser.parseTag(entry.NBT);
            stack.setTag(nbt);
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return stack;
   }

   public int getIntervalDecrease(int chipCount) {
      return this.INTERVAL_DECREASE_PER_CHIP.get(chipCount);
   }

   public List<ItemStack> getOptions() {
      List<ItemStack> options = new ArrayList<>();
      WeightedList<SingleItemEntry> entries = this.LOOT;
      if (entries.size() < 5) {
         VaultMod.LOGGER.error("Invalid config: statue weighted list should have more than 5 entries");
         return List.of(Items.DIORITE.getDefaultInstance());
      } else {
         label29:
         while (options.size() < 5) {
            SingleItemEntry entry = entries.getRandom(new Random());
            ItemStack item = this.getItem(entry);
            if (item.isEmpty()) {
               entries.remove(entry);
            } else {
               for (ItemStack i : options) {
                  if (i.getItem() == item.getItem()) {
                     continue label29;
                  }
               }

               options.add(item);
            }
         }

         return options;
      }
   }
}
