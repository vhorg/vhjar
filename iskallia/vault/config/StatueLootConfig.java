package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.config.entry.StatueDecay;
import iskallia.vault.util.StatueType;
import iskallia.vault.util.data.WeightedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class StatueLootConfig extends Config {
   @Expose
   private int MAX_ACCELERATION_CHIPS;
   @Expose
   private HashMap<Integer, Integer> INTERVAL_DECREASE_PER_CHIP = new HashMap<>();
   @Expose
   private WeightedList<SingleItemEntry> GIFT_NORMAL_STATUE_LOOT;
   @Expose
   private int GIFT_NORMAL_STATUE_INTERVAL;
   @Expose
   private StatueDecay GIFT_NORMAL_DECAY;
   @Expose
   private WeightedList<SingleItemEntry> GIFT_MEGA_STATUE_LOOT;
   @Expose
   private int GIFT_MEGA_STATUE_INTERVAL;
   @Expose
   private StatueDecay GIFT_MEGA_DECAY;
   @Expose
   private WeightedList<SingleItemEntry> VAULT_BOSS_STATUE_LOOT;
   @Expose
   private int VAULT_BOSS_STATUE_INTERVAL;
   @Expose
   private StatueDecay VAULT_BOSS_DECAY;
   @Expose
   private WeightedList<SingleItemEntry> OMEGA_STATUE_LOOT;
   @Expose
   private int OMEGA_STATUE_INTERVAL;

   @Override
   public String getName() {
      return "statue_loot";
   }

   public int getDecay(StatueType type) {
      switch (type) {
         case GIFT_NORMAL:
            return this.GIFT_NORMAL_DECAY.getDecay();
         case GIFT_MEGA:
            return this.GIFT_MEGA_DECAY.getDecay();
         case VAULT_BOSS:
            return this.VAULT_BOSS_DECAY.getDecay();
         default:
            return -1;
      }
   }

   @Override
   protected void reset() {
      this.MAX_ACCELERATION_CHIPS = 4;
      this.INTERVAL_DECREASE_PER_CHIP.put(1, 50);
      this.INTERVAL_DECREASE_PER_CHIP.put(2, 100);
      this.INTERVAL_DECREASE_PER_CHIP.put(3, 200);
      this.INTERVAL_DECREASE_PER_CHIP.put(4, 500);
      this.GIFT_NORMAL_STATUE_LOOT = new WeightedList<>();
      ItemStack fancyApple = new ItemStack(Items.field_151034_e);
      fancyApple.func_200302_a(new StringTextComponent("Fancy Apple"));
      this.GIFT_NORMAL_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(fancyApple), 1));
      ItemStack sword = new ItemStack(Items.field_151041_m);
      sword.func_77966_a(Enchantments.field_185302_k, 10);
      this.GIFT_NORMAL_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(sword), 1));
      this.GIFT_NORMAL_STATUE_INTERVAL = 500;
      this.GIFT_NORMAL_DECAY = new StatueDecay(100, 1000);
      this.GIFT_MEGA_STATUE_LOOT = new WeightedList<>();
      ItemStack fancierApple = new ItemStack(Items.field_151153_ao);
      fancierApple.func_200302_a(new StringTextComponent("Fancier Apple"));
      this.GIFT_MEGA_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(fancierApple), 1));
      sword = new ItemStack(Items.field_151048_u);
      sword.func_77966_a(Enchantments.field_185302_k, 10);
      this.GIFT_MEGA_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(sword), 1));
      this.GIFT_MEGA_STATUE_INTERVAL = 1000;
      this.GIFT_MEGA_DECAY = new StatueDecay(100, 1000);
      this.VAULT_BOSS_STATUE_LOOT = new WeightedList<>();
      ItemStack fanciestApple = new ItemStack(Items.field_196100_at);
      fanciestApple.func_200302_a(new StringTextComponent("Fanciest Apple"));
      this.VAULT_BOSS_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(fanciestApple), 1));
      sword = new ItemStack(Items.field_234754_kI_);
      sword.func_77966_a(Enchantments.field_185302_k, 10);
      this.VAULT_BOSS_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(sword), 1));
      this.VAULT_BOSS_STATUE_INTERVAL = 500;
      this.VAULT_BOSS_DECAY = new StatueDecay(100, 1000);
      this.OMEGA_STATUE_LOOT = new WeightedList<>();
      this.OMEGA_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(Blocks.field_150348_b), 1));
      this.OMEGA_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(Blocks.field_150347_e), 1));
      this.OMEGA_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(Blocks.field_196654_e), 1));
      this.OMEGA_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(Blocks.field_196656_g), 1));
      this.OMEGA_STATUE_LOOT.add(new WeightedList.Entry<>(new SingleItemEntry(Blocks.field_196617_K), 1));
      this.OMEGA_STATUE_INTERVAL = 1000;
   }

   public ItemStack randomLoot(StatueType type) {
      switch (type) {
         case GIFT_NORMAL:
            return this.getItem(this.GIFT_NORMAL_STATUE_LOOT.getRandom(new Random()));
         case GIFT_MEGA:
            return this.getItem(this.GIFT_MEGA_STATUE_LOOT.getRandom(new Random()));
         case VAULT_BOSS:
            return this.getItem(this.VAULT_BOSS_STATUE_LOOT.getRandom(new Random()));
         case OMEGA:
         case OMEGA_VARIANT:
            return this.getItem(this.OMEGA_STATUE_LOOT.getRandom(new Random()));
         default:
            throw new InternalError("Unknown Statue variant: " + type);
      }
   }

   public List<ItemStack> getOmegaOptions() {
      List<ItemStack> options = new ArrayList<>();
      WeightedList<SingleItemEntry> entries = this.OMEGA_STATUE_LOOT;

      for (int i = 0; i < 5; i++) {
         SingleItemEntry entry = entries.getRandom(new Random());
         entries.remove(entry);
         options.add(this.getItem(entry));
      }

      return options;
   }

   public int getInterval(StatueType type) {
      switch (type) {
         case GIFT_NORMAL:
            return this.GIFT_NORMAL_STATUE_INTERVAL;
         case GIFT_MEGA:
            return this.GIFT_MEGA_STATUE_INTERVAL;
         case VAULT_BOSS:
            return this.VAULT_BOSS_STATUE_INTERVAL;
         case OMEGA:
         case OMEGA_VARIANT:
            return this.OMEGA_STATUE_INTERVAL;
         default:
            throw new IllegalArgumentException("Unknown Statue variant: " + type);
      }
   }

   public int getMaxAccelerationChips() {
      return this.MAX_ACCELERATION_CHIPS;
   }

   private ItemStack getItem(SingleItemEntry entry) {
      ItemStack stack = ItemStack.field_190927_a;

      try {
         Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.ITEM));
         stack = new ItemStack(item);
         if (entry.NBT != null) {
            CompoundNBT nbt = JsonToNBT.func_180713_a(entry.NBT);
            stack.func_77982_d(nbt);
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return stack;
   }

   public int getIntervalDecrease(int chipCount) {
      return this.INTERVAL_DECREASE_PER_CHIP.get(chipCount);
   }
}
